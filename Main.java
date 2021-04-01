import java.util.ArrayList;
import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String args[]) {

        final int total_naps = 10; //the total number of NAPs
        final int bloom_size = 10; //the size of the bloom filters
        final int bloom_hashes = 1; //how many times ids are hashed in the bloom filter
        final int total_ids = 100; //the total number of content ids to be generated
        final double exponent =  1.3; //the Zipf distribution exponent
        

        System.out.println("PREPARING SIMULATION . . .");
        NRS nrs = new NRS(); // the Name Resolution System
        NAP[] naps = new NAP[total_naps]; // the Network Access Points
        for(int n = 0; n <total_naps; n++ ) naps[n] = new NAP(bloom_size,bloom_hashes,1,Integer.toString(n));
        ArrayList<String> ids = new ArrayList<String>(); // the content ids
        IdGenerator gen = new IdGenerator(); // generates 4 byte string ids
        Random rand = new Random(); // to attach content id to a random NAP 
    
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
        System.out.println("DONE: Total ids generated: "+ids.size());
        for(NAP nap : naps) System.out.println("Total ids in NAP #"+nap.id()+" = "+nap.total_ids());

        System.out.println("Zipfian distribution");
        Zipf zipf = new Zipf(exponent,total_ids);

        for(int i = 1; i < total_ids+1; i++) {

            BigDecimal zipfval = zipf.getZipfVal(i);
            System.out.println("for rank = "+i+" zipf = "+zipfval);
            System.out.println("for zipf = "+zipfval+" rank ="+zipf.getRank(zipfval));

        }

        
        /*
        
        System.out.println("Printing random bigdecimal ranks");
        BigDecimal test,temp,newrank;
        for(int j = 0; j < 1000; j++) {
            test = new BigDecimal(Math.random());
            temp = BigDecimal.ONE.divide(sum.multiply(test),64,RoundingMode.HALF_EVEN);
            newrank = new BigDecimal(Math.pow(temp.doubleValue(),1/exponent));
            System.out.println(Math.ceil(newrank.doubleValue()));
        }

        */
        
    }
}