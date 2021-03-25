import java.util.*;

/* A Name Resolution System (NRS) */
public class NRS {

    //total number of false positives from content queries to NAPs
    int false_positives = 0;
    HashMap<String, BloomFilter> bloom_filters;

    public NRS() {
        
    }

    /*
    public void request_content(String id) {
        //check bloom filters
        boolean exists;
        for(int i = 0; i < naps.length; i++) {
            if(naps[i].request_bloomfilter(id)) {
                exists = naps[i].request_content(id);
                if(exists == true) break;
                else false_positives++;
            }
        }
    }
*/
}