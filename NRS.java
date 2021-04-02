import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/* A Name Resolution System (NRS) */
public class NRS {

    final int total_naps = 20; //the total number of NAPs
    final int bloom_size = 30; //the size of the bloom filters
    final int bloom_hashes = 1; //how many times ids are hashed in the bloom filter
    final int total_ids = 1000; //the total number of content ids to be generated
    final double exponent =  1.3; //the Zipf distribution exponent
    final int total_queries = 100; //the number of queries to be generated

    int false_positives = 0; //total number of false positives from content queries to NAPs
    NAP[] naps;
    ArrayList<String> ids;
    int[] counters;
    //BloomFilter[] bloom_filters; //NAPs bloom filters 

    public NRS() {
        prepare();
        start();
    }

    public void prepare() {
        System.out.println("PREPARING SIMULATION . . .");

        //Initialize Network Access Points
        naps = new NAP[total_naps];
        for(int n = 0; n <total_naps; n++ ) naps[n] = new NAP(bloom_size,bloom_hashes,1,Integer.toString(n));

        //Generate random content IDs and evenly distribute them to NAPs
        ids = new ArrayList<String>();
        IdGenerator gen = new IdGenerator(); // generates 4 byte string ids
        Random rand = new Random(); // to get random NAP 
        for(int i = 0; i <total_ids; i++) {
            String id = gen.generate();
            if(!ids.contains(id)) ids.add(id);
            else {
                i = i-1; //id exists, generate new
                continue;
            }
            //add the id to a Random NAP
            naps[rand.nextInt(total_naps)].add_content(id);
        }
        System.out.println("Total ids generated: "+ids.size());
        for(NAP nap : naps) System.out.println("Total ids in NAP #"+nap.id()+" = "+nap.total_ids());

        //initialize counters for total queries per rank
        counters = new int[total_ids];
    }

    public void start() {
        System.out.println("\nSTART GENERATING QUERIES . . .");
        BigDecimal temp; int rank; String id;
        //Zipfian distribution for popularity aware query generation
        Zipf zipf = new Zipf(exponent,total_ids);
        /*
        System.out.println("ALL ZIPF VALUES");
        for(int a = 1; a < total_ids+1; a++) {
            System.out.println("for rank = "+a+" zipf = "+zipf.getZipfVal(a));
        }
        */
        int false_pos;
        for(int k = 0; k < total_queries; k++) {
            //get a content id based on popularity rank
            temp = new BigDecimal("0.0");
            while(temp.compareTo(zipf.getZipfVal(total_ids)) < 0)
                temp = new BigDecimal(Math.random()).setScale(32,RoundingMode.HALF_EVEN); 
            rank = zipf.getRank(temp);
            id = ids.get(rank-1); counters[rank-1]++;

            //query execution - check bloom filters for the id
            false_pos = 0;
            for(NAP nap: naps) {
                //check if id exists in bloom filter
                if(nap.update().exists(id)) {
                    //check if id actually exists in nap
                    if(nap.isAttached(id)) {
                        System.out.println("Found id "+id+" in NAP "+nap.id()+" after "+false_pos+" tries");
                        break;
                    } else false_pos++;
                }
            }
            //update total false positives
            false_positives += false_pos;

            //System.out.println("Query #"+k+" for content id \'"+id+"\' with rank "+rank+"/"+total_ids);
        }
        
        System.out.println("QUERY GENERATION COMPLETED. RESULTS: ");
        System.out.println("QUERIES PER RANK");
        for(int c = 0; c < total_ids; c++) System.out.println("Rank #"+(c+1)+" : "+counters[c]+" queries");

        System.out.println("\nTotal false positives: "+ false_positives);
    }

    public static void main(String[] args) {
        new NRS();
    }
}