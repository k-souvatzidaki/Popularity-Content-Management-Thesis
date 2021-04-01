import java.util.ArrayList;
import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String args[]) {

        final int total_naps = 10; //the total number of NAPs
        final int bloom_size = 10; //the size of the bloom filters
        final int bloom_hashes = 1; //how many times ids are hashed in the bloom filter
        final int total_ids = 1000; //the total number of content ids to be generated
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
        BigDecimal sum = new BigDecimal("0"); 
        for(int i = 1; i < total_ids+1; i++) {
            //System.out.println(new BigDecimal(1/Math.pow(i,exponent)));
            sum = sum.add(new BigDecimal(1/Math.pow(i,exponent)));
            //System.out.println(sum);
        }
        System.out.println("Sum = "+sum);
        //double sum_double = sum.doubleValue();
        //System.out.println("Sum doubled = "+sum_double);

        for(int i = 1; i < total_ids+1; i++) {
            //finding zipf value for rank i
            BigDecimal zipf = new BigDecimal(1/Math.pow(i,exponent)).divide(sum,64,RoundingMode.HALF_EVEN);
            //System.out.println("for rank = "+i+" zipf = "+zipf/*.setScale(10,RoundingMode.HALF_EVEN)*/);
            //finding rank for new zipf value
            BigDecimal reverse = BigDecimal.ONE.divide(sum.multiply(zipf),64,RoundingMode.HALF_EVEN);
            int rank = (int)Math.round(Math.pow(reverse.doubleValue(),1/exponent));
            System.out.println("for zipf = "+zipf+" rank ="+rank);
            /*
            //System.out.println(new BigDecimal(1/Math.pow(i,exponent)));
            BigDecimal zipf = new BigDecimal(1/Math.pow(i,exponent)).divide(sum,5,RoundingMode.HALF_EVEN);
            System.out.println("for rank = "+i+" zipf = "+zipf);
            double eh = BigDecimal.ONE.divide(sum.multiply(zipf),5,RoundingMode.HALF_EVEN).doubleValue();
            System.out.println(eh);
            double rank = Math.round(Math.pow(eh,1/exponent));
            System.out.println("for zipf = "+zipf+" rank ="+rank);

            */
        }

        
        
        System.out.println("Printing random bigdecimal ranks");
        BigDecimal test,temp,newrank;
        for(int j = 0; j < 1000; j++) {
            test = new BigDecimal(Math.random());
            temp = BigDecimal.ONE.divide(sum.multiply(test),64,RoundingMode.HALF_EVEN);
            newrank = new BigDecimal(Math.pow(temp.doubleValue(),1/exponent));
            System.out.println(Math.ceil(newrank.doubleValue()));
        }
        
        /*
        BigDecimal test = new BigDecimal("0.9092967170759090998219618873957696375276818926171797417068491028");
        BigDecimal eh2 = BigDecimal.ONE.divide(sum.multiply(test),64,RoundingMode.HALF_EVEN);
        BigDecimal rank2 = new BigDecimal(Math.pow(eh2.doubleValue(),1/exponent));
        System.out.println(Math.ceil(rank2.doubleValue()));
        */
        
        
        /*
        BigDecimal test = new BigDecimal("0.07");
        BigDecimal eh2 = BigDecimal.ONE.divide(sum.multiply(test),64,RoundingMode.HALF_EVEN);
        System.out.println(eh2);
        double rank2 = Math.pow(eh2.doubleValue(),1/exponent);
        System.out.println("MUST BE 3: "+rank2);
        //double rank2 = Math.pow(eh2,1/exponent);
        //System.out.println("for zipf = "+test+" rank ="+rank2);
        */


    }
}