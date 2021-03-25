import java.util.ArrayList; 

/* A Network Access Point (NAP) */
public class NAP {

    BloomFilter bloom; //bloom filter for specifying content attached to the NAP
    ArrayList<String> content_ids; //content ids attached to the NAP
    int total_removals,update_factor;
    String id;

    //constructor 
    public NAP(int bloom_size, int bloom_hashes, int update_factor, String id) {
        content_ids = new ArrayList<String>();
        bloom = new BloomFilter(bloom_size,bloom_hashes);
        this.update_factor = update_factor;
        total_removals = 0;
        this.id = id;
    }

    /* Attach a content id to this NAP */
    public void add_content(String id){
        content_ids.add(id);
        bloom.add(id);
    }

    /* Remove a content id from this NAP */
    public void remove_content(String id) {
        content_ids.remove(id);
        total_removals++;
        if(total_removals > update_factor) { //recreate the bloom filter
            total_removals = 0;
            bloom.removeAll();
            bloom.add(content_ids);
        }
    }

    public int total_ids() {
        return content_ids.size();
    }

    public String id() {
        return id;
    }


    /* Check if content is actually attached to the NAP */
    public boolean isAttached(String id) {
        return content_ids.contains(id);
    }

    /* Update with content located on this NAP */
    public BloomFilter update() {
        return bloom;
    }
    
}