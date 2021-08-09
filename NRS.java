import java.util.Random;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import utils.*;

/* A Name Resolution System (NRS) */
public class NRS {
    static final double exponent = 0.8; //the Zipf distribution exponent
    static final int total_ids = 10000; //the total number of content ids to be generated (n)
    static final int total_queries = 200; //the number of queries to be generated
    static final int id_len = 4; //the length of ids in bytes
    static final int total_naps = 20; //the total number of NAPs
    static final BigDecimal top_popularity_percent = new BigDecimal("1.0");
    static final Zipf zipf =  new Zipf(exponent,total_ids); //Zipfian distribution for popularity aware query generation;
    static final int top_pop = zipf.getRank(top_popularity_percent); //get # of top ranks to be considered popular 
    static final IdGenerator gen = new IdGenerator(id_len); // generates 4 byte string ids
    static final Random rand = new Random(); // to get random NAP 
    
    static int m,k; //bits per id in bloom filter, number of hashes
    static int bloom_size; //the size of the bloom filters
    static int false_positives;
    static int total_false_positives = 0;
    static NAP[] naps; //all NAPs
    static ArrayList<String> ids; //list of all content ids


    /* Prepare the simulation: create NAPs, create contend IDs, attach IDs to NAPs  */
    public static void prepare() {
        //Initialize Network Attachment Points
        naps = new NAP[total_naps];
        for(int n = 0; n <total_naps; n++ ) naps[n] = new NAP(bloom_size,k,1,Integer.toString(n),top_pop);
        //Generate random content IDs and evenly distribute them to NAPs
        ids = new ArrayList<String>();
        for(int i = 0; i <total_ids; i++) {
            String id = gen.generate();
            if(!ids.contains(id)) ids.add(id);
            else {
                i = i-1; //id exists, generate new
                continue;
            }
            int rand_index = rand.nextInt(total_naps);
            naps[rand_index].add_content(id,i);
        }
    }


    /* Start the simulation: generate queries for random ids based on Zipf frequency distribution 
       Execute many experiments for each #_naps */
    public static void start() {
        BigDecimal temp; int rank; String id;
        int false_pos;
        false_positives = 0;
        for(int q = 0; q < total_queries; q++) {
            temp = new BigDecimal("0.0");
            while(temp.compareTo(zipf.getZipfVal(total_ids)) < 0)
                temp = new BigDecimal(Math.random()).setScale(32,RoundingMode.HALF_EVEN); //a random number (1>temp>minimum zipf value)
            rank = zipf.getRank(temp); //get rank based on zipf value
            id = ids.get(rank-1); //get id
                
            //query execution - check bloom filters for the id
            false_pos = 0;
            for(NAP nap: naps) {
                if(nap.update().exists(id,rank)) { //check if id exists in bloom filter
                    if(nap.isAttached(id) /*if id actually exists in NAP*/) break;
                    else false_pos++;
                }
            }
            false_positives += false_pos; //update total false positives
        }
        //System.out.println("False positives of experiment = "+false_positives);
        total_false_positives+=false_positives;
    }

    //main app
    public static void main(String[] args) {
        new NRS();
        int[] k_values = {1,2,4,6,8,10,11,12};
        int[] m_values = {1,2,4,6,8,10,12,14};
        //for each k, for each m, create new NRS and do experiment x number 0f experiments
        for(int k_val = 0; k_val < k_values.length; k_val++) {
            NRS.k = k_values[k_val];
            for(int m_val = 0; m_val < m_values.length; m_val++) {
                NRS.m = m_values[m_val];
                bloom_size = (total_ids/total_naps) * m;
                int experiments = 0; 
                do {
                    prepare();
                    start();
                    experiments++;
                }while(experiments < 20);
                System.out.println("k = "+NRS.k+", m = "+NRS.m +" FP = "+NRS.total_false_positives/20);
                NRS.total_false_positives = 0;
    }}}
}