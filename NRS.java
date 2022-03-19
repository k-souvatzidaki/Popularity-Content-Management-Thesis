import java.util.Random;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import utils.*;

/* A Name Resolution System (NRS) */
public class NRS {

    static final double false_positive_ratio = 0.1;
    static final int k_unpop = 1;
    static final int id_len = 4; //in bytes
    static final int total_queries = 1000;
    static final IdGenerator gen = new IdGenerator(id_len);
    static final Random rand = new Random();


    private int n_items;
    private int n_naps;
    private Zipf zipf;
    private int top_popular;
    private int k_popular;
    private int bloom_size;

    private NAP[] naps; //all NAPs
    private ArrayList<String> ids; //list of all content ids

    // // static final BigDecimal top_popularity_percent = new BigDecimal("0.7");
    // static final int top_pop = 100; // zipf.getRank(top_popularity_percent); //get # of top ranks to be considered popular 
    

    public NRS(int n_items, int n_naps, double exponent, String top_popularity_percent, int k_popular) {
        this.n_items = n_items;
        this.n_naps = n_naps;
        this.zipf =  new Zipf(exponent,n_items);
        this.top_popular = this.zipf.getRank(new BigDecimal(top_popularity_percent));
        this.k_popular = k_popular;
        this.bloom_size = (int)Math.round( -(n_items/ n_naps) / Math.log(1-false_positive_ratio) );
        System.out.println("Bloom size ="+bloom_size+" bits");
    }


    /* Prepare the simulation: create NAPs, create contend IDs, attach IDs to NAPs  */
    public void prepare() {
        //Initialize Network Attachment Points
        naps = new NAP[n_naps];
        for(int n = 0; n <n_naps; n++ ) naps[n] = new NAP(bloom_size,k_popular,Integer.toString(n),top_popular,k_unpop);

        //Generate random content IDs and evenly distribute them to NAPs
        ids = new ArrayList<String>();
        int rand_index = 0;
        for(int i = 0; i <n_items; i++) {
            String id = gen.generate();
            if(!ids.contains(id)) ids.add(id);
            else {
                i = i-1; //id exists, generate new
                continue;
            }
            rand_index = rand.nextInt(n_naps);
            naps[rand_index].add_content(id,i);
        }
    }


    /* Start the simulation: generate queries for random ids based on Zipf frequency distribution 
       Execute many experiments for each #_naps */
    public double start() {
        BigDecimal temp; int rank; String id; int false_pos;
        int false_positives = 0;
        rank = 1;
        for(int q = 0; q < total_queries; q++) {
            //select an ID to query
            temp = new BigDecimal("0.0");
            while(temp.compareTo(zipf.getZipfVal(n_items)) < 0)
                temp = new BigDecimal(Math.random()).setScale(32,RoundingMode.HALF_EVEN); //a random number (1>temp>minimum zipf value)
            rank = zipf.getRank(temp); 
            id = ids.get(rank-1); 
            
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
        return false_positives;
    }


    //main app
    public static void main(String[] args) {

        int[] n_items = {10000, 64000};
        int[] n_naps = {10,100,1000};
        double[] exponents = {.8};
        String[] top_popularity_percent = {"1.0"};
        int[] k_popular = {1};

        for(int n_item : n_items) {
            for(double exp : exponents) {
                for(int k_pop : k_popular) {
                    System.out.println("====================== n_items = "+n_item+", exp = "+exp+", k_popular ="+k_pop+" ======================");

                    for(int n_nap : n_naps) {
                        for(String top_pop : top_popularity_percent) {
                            System.out.println(" -- n_naps= "+n_nap+", top_popularity_percent= "+top_pop);
                            NRS nrs = new NRS(n_item,n_nap,exp,top_pop,k_pop);
                            int experiments = 0; 
                            int total_false_positives = 0;
                            do {
                                nrs.prepare();
                                total_false_positives += nrs.start();
                                experiments++;
                            } while(experiments < 5);
                            double false_positive_ratio = ((total_false_positives/5.0)/ ((double)n_nap/2.0))/1000.0; // on average half the NAPs are queried per request
                            System.out.println(" -- result false positive ratio: " + false_positive_ratio);
                            System.out.println("===================================================");
                        }
                    }

                }
            }
        }
    //     new NRS();
    //     int[] k_values = {1};
    //     double[] fp_values = {0.1, 0.01, 0.001, 0.0001, 0.00001};
    //     //for each k, for each desired false positive ratio, evaluate m, create new NRS and do experiments
    //     for(int k_val : k_values) {
    //         NRS.k = k_val;
    //         System.out.println("====================== k = "+NRS.k+" ======================");
    //         for(double fp_val : fp_values) {
    //             bloom_size = (int)Math.round( -(total_ids / total_naps) / Math.log(1-fp_val) );
    //             System.out.println(" -- desired false positive = "+fp_val);
    //             System.out.println(" -- Bloom filter size = "+bloom_size);
    //             System.out.println(" -- m (bits/item) = "+ bloom_size / (total_ids/total_naps));
    //             int experiments = 0; 
    //             int total_false_positives = 0;
    //             do {
    //                 prepare();
    //                 total_false_positives += start();
    //                 experiments++;
    //             } while(experiments < 5);
    //             double false_positive_ratio = ((NRS.total_false_positives/5)/ (total_naps/2))/ total_queries; // on average half the NAPs are queried per request
    //             System.out.println(" -- result false positive ratio: " + false_positive_ratio);
    //             System.out.println("===================================================");
    //             // System.out.println("k = "+NRS.k+", size = "+bloom_size+" total FP = "+(NRS.total_false_positives/5)/20);
    //             NRS.total_false_positives = 0;
    // }}
    }

}