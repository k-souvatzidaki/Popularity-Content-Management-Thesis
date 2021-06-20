package utils;
import java.util.*;

/* A weighted Bloom Filter data structure for content IDs 
 * Content is seperated in 2 groups - popular and unpopular*/
public class WeightedBloomFilter extends BloomFilter {

    private int top_pop, function_integer; 

    public WeightedBloomFilter(int m,int k,int top_pop) {
        super(m,k);
        this.top_pop = top_pop;
        this.function_integer = new Random().nextInt(m);            
    }

    /* Returns true if the id MIGHT exist in the filter (false positives)
       Returns false if the id DOESN'T exist in the filter (no false negatives) */
    public boolean exists(String id,int pop){
        if(pop <=top_pop) {
            //get with k hash functions
            return super.exists(id);
        }else {
            int index = hash(id,function_integer);
            return bloom[index];
        }
    }

    /* Add id(s) in the filter */
    public void add(String id,int pop) {
        if(pop <=top_pop) {
            //add with k hash functions
            super.add(id);
        }else {
            int index = hash(id,function_integer);
            bloom[index] = true;
        }
    }

}