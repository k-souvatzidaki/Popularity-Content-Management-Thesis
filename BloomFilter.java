/* A Bloom Filter data structure for content IDs */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.ArrayList;

public class BloomFilter {

    private boolean[] bloom;
    private int size,hashes;

    //constructor
    public BloomFilter(int size,int hashes) {
        bloom = new boolean[size]; //initialized with false
        this.size = size;
        this.hashes = hashes;
    }

    /* Returns true if the id MIGHT exist in the filter (false positives)
       Returns false if the id DOESN'T exist in the filter (no false negatives) */
    public boolean exists(String id){
        return bloom[hash(id)];
    }

    /* Add id(s) in the filter */
    public void add(String id) {
        bloom[hash(id)] = true;
    }
    public void add(ArrayList<String> ids) {
        for(String id : ids) this.add(id);
    }

    /* remove all idd from the bloom filter */
    public void removeAll() {
        bloom = new boolean[size];
    }

    /* Bloom filter bit array getter */
    public boolean[] bloom() {
        return this.bloom;
    }

    /* Hash function to get the index of an id in the bloom filter. Using SHA-1.
       Hash performed as many times as the value of class variable "hashes" */
    public int hash(String id) {
        MessageDigest message = null;
        try {
            message = MessageDigest.getInstance("SHA-1"); 
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        byte[] hash = null;
        String temp = id;
        //get all hashes 
        for(int i = 0; i < hashes; i++) {
            hash = message.digest(temp.getBytes());
            temp = new String(hash);
        }
        //convert to integer in range [0,size-1]
        BigInteger hashint = new BigInteger(1, hash); 
        int value = hashint.intValue();
        if(value < 0) value*=-1;
        value = value % size; //in range
        System.out.println(value);
        
        return value;
    }

}