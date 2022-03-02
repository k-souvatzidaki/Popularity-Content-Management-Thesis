package utils;

/* A weighted Bloom Filter data structure for content IDs 
 * Content is seperated in 2 groups - popular and unpopular*/
public class WeightedBloomFilter extends BloomFilter {

    private int top_pop, k_unpop; 

    public WeightedBloomFilter(int size,int k,int top_pop,int k_unpop) {
        super(size,k);
        this.top_pop = top_pop;
        this.k_unpop = k_unpop;
    }

    /* Returns true if the id MIGHT exist in the filter (false positives)
       Returns false if the id DOESN'T exist in the filter (no false negatives) */
    public boolean exists(String id,int pop){
        if(pop <=top_pop) {
            //get with k hash functions
            return super.exists(id);
        }else {
            //get with k_unpop hash functions
            for(int h = 0; h < k_unpop; h++) {
                int index = super.hash(id,functions_integers[h]);
                //System.out.println("id = "+id+" : " +index);
                if(super.bloom[index]==false) return false;
            }
            return true;
            /*
            //get with k_unpop functions
            return super.exists(id,k_unpop);
            //int index = hash(id,function_integer);
            //return bloom[index]; */
        }
    }

    /* Add id(s) in the filter */
    public void add(String id,int pop) {
        if(pop <=top_pop) {
            //add with k hash functions
            super.add(id);
        }else {
            //compute all hash functions
            for(int h = 0; h < k_unpop; h++) {
                int index = super.hash(id,functions_integers[h]);
                super.bloom[index] = true;
            }
            /*
            //add with k_unpop hash functions
            super.add(id,k_unpop);
            //int index = hash(id,function_integer);
            //bloom[index] = true; */
        }
    }

}