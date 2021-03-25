import java.util.ArrayList;
import java.util.Random;
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
    
        for(int i = 0; i <total_ids; i++) {
            String id = gen.generate();
            if(!ids.contains(id)) ids.add(id);
            else {
                i = i-1; //id exists, generate new
                continue;
            }
            //add the id to a Random NAP
            Random rand = new Random();
            int kk = rand.nextInt(total_naps);
            naps[kk].add_content(id);
            System.out.println("must be true "+naps[kk].isAttached(id));
            System.out.println(naps[kk].isAttached("HAHA"));
        }
        System.out.println("DONE: Total ids generated: "+ids.size());
        for(NAP nap : naps) System.out.println("Total ids in NAP #"+nap.id()+" = "+nap.total_ids());


        System.out.println("PREPARING BLOOM FILTERS MAPPING");

    }
}