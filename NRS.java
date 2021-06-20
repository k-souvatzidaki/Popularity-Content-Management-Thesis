import java.util.Random;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import utils.*;

/* A Name Resolution System (NRS) */
public class NRS {
    final int experiments = 100; //total # of experiments
    final double exponent =  0.8; //the Zipf distribution exponent
    final int total_ids = 10000; //the total number of content ids to be generated (n)
    final int total_queries = 100; //the number of queries to be generated
    final int id_len = 4; //the length of ids in bytes

    final int total_naps = 20; //the total number of NAPs
    final int m = 1; //bits per id in bloom filter
    final int bloom_size = (total_ids/total_naps) * m; //the size of the bloom filters
    final int bloom_hashes = 10; //number of hash functions in the bloom filter (k)
    final int top_pop = 10000;

    int false_positives = 0;
    int false_positives_queries = 0;
    int total_false_positives = 0;
    int total_false_positives_queries = 0;
    int total_false_populars = 0;
    NAP[] naps; //all NAPs
    ArrayList<String> ids; //list of all content ids
    int[] counters; //total queries per rank/id

    public NRS() {
        prepare();
        start();
    }

    /* Prepare the simulation: create NAPs, create contend IDs, attach IDs to NAPs  */
    public void prepare() {
        //Initialize Network Access Points
        naps = new NAP[total_naps];
        for(int n = 0; n <total_naps; n++ ) naps[n] = new NAP(bloom_size,bloom_hashes,1,Integer.toString(n),top_pop);

        //Generate random content IDs and evenly distribute them to NAPs
        ids = new ArrayList<String>();
        IdGenerator gen = new IdGenerator(id_len); // generates 4 byte string ids
        Random rand = new Random(); // to get random NAP 
        for(int i = 0; i <total_ids; i++) {
            String id = gen.generate();
            if(!ids.contains(id)) ids.add(id);
            else {
                i = i-1; //id exists, generate new
                continue;
            }
            //if(i > top_pop) {
                //only add the non-popular ids to a Random NAP's bloom filter
                int rand_index = rand.nextInt(total_naps);
                naps[rand_index].add_content(id,i);
            //}
        }
        //initialize counters for total queries per rank
        counters = new int[total_ids];
    }


    /* Start the simulation: generate queries for random ids based on Zipf frequency distribution 
       Execute many experiments for each #_naps */
    public void start() {
        BigDecimal temp; int rank; String id;
        Zipf zipf = new Zipf(exponent,total_ids); //Zipfian distribution for popularity aware query generation
        int false_pos;

        //100 experiments per k
        for(int e=1; e <=experiments; e++) {
            int false_populars = 0;
            false_positives = 0;
            false_positives_queries = 0;
            for(int k = 0; k < total_queries; k++) {
                temp = new BigDecimal("0.0");
                while(temp.compareTo(zipf.getZipfVal(total_ids)) < 0)
                    temp = new BigDecimal(Math.random()).setScale(32,RoundingMode.HALF_EVEN); //a random number (1>temp>minimum zipf value)
                rank = zipf.getRank(temp); //get rank based on zipf value

                //if(rank <= top_pop) {
                //    continue;
                //}
                id = ids.get(rank-1); counters[rank-1]++; //get id and increase counter
                
                //query execution - check bloom filters for the id
                false_pos = 0;
                for(NAP nap: naps) {
                    if(nap.update().exists(id,rank)) { //check if id exists in bloom filter
                        if(nap.isAttached(id) /*if id actually exists in NAP*/) break;
                        else {
                            false_pos++;
                            if(rank < 250) false_populars++;
                        }
                    }
                }
                false_positives += false_pos; //update total false positives
                if(false_pos > 0) false_positives_queries++;
            }
            total_false_positives+=false_positives;
            total_false_positives_queries+=false_positives_queries;
            total_false_populars += false_populars;
        }
        System.out.println("FOR k= "+bloom_hashes);
        System.out.println("AVERAGE # OF FALSE POSITIVES FOR ALL EXPERIMENTS: "+total_false_positives/experiments);
        System.out.println("AVERAGE % OF FALSE POSITIVES FROM POPULAR IDS FOR ALL EXPERIMENTS: "+((total_false_populars/experiments)*100)/(total_false_positives/experiments));
        System.out.println("AVERAGE # OF QUERIES WITH AT LEAST ONE FALSE POSITIVE (FALSE POSITIVE RATE) FOR ALL EXPERIMENTS: "+total_false_positives_queries/experiments+"\n");
        
    }

    //main app
    public static void main(String[] args) {
        new NRS();
    }
}