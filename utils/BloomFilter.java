package utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.*;

/* A Bloom Filter data structure for content IDs */
public class BloomFilter {

    protected boolean[] bloom;
    private int size,k;
    protected int[] functions_integers;

    public BloomFilter(int size,int k) {
        bloom = new boolean[size]; //initialized with false
        this.size = size;
        this.k = k;

        functions_integers = new int[k]; //a random number for each hash function
        for(int i = 0; i < k; i++) {
            functions_integers[i] = new Random().nextInt(Integer.MAX_VALUE);
        }
    }

    /* Returns true if the id MIGHT exist in the filter (false positives)
       Returns false if the id DOESN'T exist in the filter (no false negatives) */
    public boolean exists(String id){
        for(int h = 0; h < k; h++) {
            int index = hash(id,functions_integers[h]);
            //System.out.println("id = "+id+" : " +index);
            if(bloom[index]==false) return false;
        }
        return true;
    }
    
    /* Add id(s) in the filter */
    public void add(String id) {
        //compute all hash functions
        for(int h = 0; h < k; h++) {
            int index = hash(id,functions_integers[h]);
            //System.out.println(index);
            bloom[index] = true;
        }
    }
    public void add(ArrayList<String> ids) {
        for(String id : ids) this.add(id);
    }


    /* remove all ids from the bloom filter */
    public void removeAll() {
        bloom = new boolean[size];
    }

    /* Bloom filter bit array getter */
    public boolean[] bloom() {
        return this.bloom;
    }

    /* Hash function to get the index of an id in the bloom filter. Using SHA-1.
       Hash performed as many times as the value of class variable "hashes" */
    public int hash(String id,int function_integer) {
        MessageDigest message = null;
        try {
            message = MessageDigest.getInstance("SHA-1"); 
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        byte[] hash = null;
        String temp = id;
        hash = message.digest(temp.getBytes());
        temp = new String(hash);
        
        //convert to integer in range [0,size-1]
        BigInteger hashint = new BigInteger(1, hash);
        int value = hashint.intValue();
        if(value < 0) value*=-1;
        value += function_integer; //function k
        value = value % size; //in range
        
        return Math.abs(value);
    }

    /* toString method */
    @Override
    public String toString() {
        String result= "";
        for(boolean b : bloom) {
            if(b == true) result+= "1 ";
            else result += "0 ";
        }
        return result;
    }

}