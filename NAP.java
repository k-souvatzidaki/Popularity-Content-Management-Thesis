import java.util.ArrayList; 
import utils.*;

/* A Network Access Point (NAP) */
public class NAP {

    WeightedBloomFilter bloom; //bloom filter for specifying content attached to the NAP
    ArrayList<String> content_ids; //content ids attached to the NAP
    int top_pop;
    String id;

    //constructor 
    public NAP(int bloom_size, int bloom_hashes, String id,int top_pop,int k_unpop) {
        content_ids = new ArrayList<String>();
        bloom = new WeightedBloomFilter(bloom_size,bloom_hashes,top_pop,k_unpop);
        this.id = id;
        this.top_pop = top_pop;
    }

    /* Attach a content id to this NAP */
    public void add_content(String id,int pop){
        content_ids.add(id);
        bloom.add(id,pop);
    }

    /* Get # of total content ids in this NAP */
    public int total_ids() {
        return content_ids.size();
    }

    /* Get NAP id */
    public String id() {
        return id;
    }

    /* Check if content is actually attached to the NAP */
    public boolean isAttached(String id) {
        return content_ids.contains(id);
    }

    /* Update with content located on this NAP */
    public WeightedBloomFilter update() {
        return bloom;
    }

    /* Get list of content ids in this NAP */ 
    public ArrayList<String> getContent() {
        return content_ids;
    }

    /* toString method */
    @Override
    public String toString() {
        String result = "";
        result += "Total ids in NAP #"+id+" = "+content_ids.size()+"\n{";
        for(String id : content_ids) result+=id+" ";
        result+="}\nBloom Filter = "+bloom;
        return result;
    }
    
}