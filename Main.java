import java.util.ArrayList;
import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;
/* RUN: java Main #_total_naps #_bloom_size #_bloom_hashes */
public class Main {
    public static void main(String args[]) {
        
        int total_naps = 0,bloom_size = 0,bloom_hashes = 0,total_ids = 0;
        try {
            total_naps = Integer.parseInt(args[0]);
            bloom_size = Integer.parseInt(args[1]);
            bloom_hashes = Integer.parseInt(args[2]);
            total_ids = Integer.parseInt(args[3]);
        }catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("RUN: java Main #_total_naps #_bloom_size #_bloom_hashes #_total_ids");
            return;
        }

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
        double exponent =  1;
        BigDecimal sum = new BigDecimal("0"); 
        for(int i = 1; i < total_ids+1; i++) {
            //System.out.println(new BigDecimal(1/Math.pow(i,exponent)));
            sum = sum.add(new BigDecimal(1/Math.pow(i,exponent)));
            //System.out.println(sum);
        }
        System.out.println("Sum = "+sum);
        for(int i = 1; i < total_ids+1; i++) {
            BigDecimal zipf = new BigDecimal(1/Math.pow(i,exponent)).divide(sum,RoundingMode.HALF_UP);
            System.out.println("for rank = "+i+" zipf = "+zipf);
        }

        System.out.println("A random bigdecimal = "+new BigDecimal(Math.random()));

    }
}