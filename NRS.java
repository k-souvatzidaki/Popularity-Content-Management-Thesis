import java.util.Random;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import utils.*;

/* A Name Resolution System (NRS) */
public class NRS {
    static final double exponent = 0.8; //the Zipf distribution exponent
    static final int total_ids = 10000; //the total number of content ids to be generated (n)
    static final int total_queries = 1000; //the number of queries to be generated
    static final int id_len = 4; //the length of ids in bytes
    static final int total_naps = 20; //the total number of NAPs

    // static final BigDecimal top_popularity_percent = new BigDecimal("0.7");
    static final int top_pop = 100; // zipf.getRank(top_popularity_percent); //get # of top ranks to be considered popular 
    static final int k_unpop = 1;

    static final Zipf zipf =  new Zipf(exponent,total_ids); //Zipfian distribution for popularity aware query generation;
    static final IdGenerator gen = new IdGenerator(id_len); // generates 4 byte string ids
    static final Random rand = new Random(); // to get random NAP 
    static final boolean ZIPF = true;
    static final boolean FULL_MAP = false;
    
    static int bloom_size,k; //bits per bloom filter, number of hashes
    static double false_positives;
    static double total_false_positives = 0;
    static NAP[] naps; //all NAPs
    static ArrayList<String> ids; //list of all content ids


    /* Prepare the simulation: create NAPs, create contend IDs, attach IDs to NAPs  */
    public static void prepare() {
        //Initialize Network Attachment Points
        naps = new NAP[total_naps];
        for(int n = 0; n <total_naps; n++ ) naps[n] = new NAP(bloom_size,k,Integer.toString(n),top_pop,k_unpop);
        //Generate random content IDs and evenly distribute them to NAPs
        ids = new ArrayList<String>();
        int rand_index = 0;
        for(int i = 0; i <total_ids; i++) {
            String id = gen.generate();
            if(!ids.contains(id)) ids.add(id);
            else {
                i = i-1; //id exists, generate new
                continue;
            }
            rand_index = rand.nextInt(total_naps);
            naps[rand_index].add_content(id,i);
        }
    }


    /* Start the simulation: generate queries for random ids based on Zipf frequency distribution 
       Execute many experiments for each #_naps */
    public static void start() {
        BigDecimal temp; int rank; String id;
        int false_pos;
        false_positives = 0;
        rank = 1;
        for(int q = 0; q < total_queries; q++) {
            //select an ID to query
            //with Zipf distribution
            if(ZIPF) {
                temp = new BigDecimal("0.0");
                while(temp.compareTo(zipf.getZipfVal(total_ids)) < 0)
                    temp = new BigDecimal(Math.random()).setScale(32,RoundingMode.HALF_EVEN); //a random number (1>temp>minimum zipf value)
                rank = zipf.getRank(temp); 
            }
            //with uniform distribution
            else {
                rank = rand.nextInt(total_ids)+1; 
            }
            id = ids.get(rank-1); 
            
            //if a full mapping array is used, skip queries for popular content
            if(FULL_MAP) {
                if(rank < top_pop) continue;
            }
            //query execution - check bloom filters for the id
            false_pos = 0;
            for(NAP nap: naps) {
                if(nap.update().exists(id,rank)) { 
                    if(nap.isAttached(id) /*if id actually exists in NAP */) break;
                    else false_pos++;
                }
            }
            false_positives += false_pos; 
        }
        total_false_positives += false_positives;
    }


    //main app
    public static void main(String[] args) {
        new NRS();
        int[] k_values = {1};
        double[] fp_values = {0.1, 0.01, 0.001, 0.0001, 0.00001};
        //for each k, for each desired false positive ratio, evaluate m, create new NRS and do experiments
        for(int k_val : k_values) {
            NRS.k = k_val;
            System.out.println("====================== k = "+NRS.k+" ======================");
            for(double fp_val : fp_values) {
                bloom_size = (int)Math.round( -(total_ids / total_naps) / Math.log(1-fp_val) );
                System.out.println(" -- desired false positive = "+fp_val);
                System.out.println(" -- Bloom filter size = "+bloom_size);
                System.out.println(" -- m (bits/item) = "+ bloom_size / (total_ids/total_naps));
                int experiments = 0; 
                do {
                    prepare();
                    start();
                    experiments++;
                } while(experiments < 5);
                double false_positive_ratio = ((NRS.total_false_positives/5)/ (total_naps/2))/ total_queries; // on average half the NAPs are queried per request
                System.out.println(" -- result false positive ratio: " + false_positive_ratio);
                System.out.println("===================================================");
                // System.out.println("k = "+NRS.k+", size = "+bloom_size+" total FP = "+(NRS.total_false_positives/5)/20);
                NRS.total_false_positives = 0;
    }}}

}