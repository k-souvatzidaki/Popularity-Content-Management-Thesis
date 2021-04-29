import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/* Zipf's distribution implementation */
public class Zipf {

    final double exponent;
    final int n;
    final BigDecimal sum;
    NavigableMap<BigDecimal,Integer> ranks_frequencies;
    BigDecimal total;

    //constructor
    public Zipf(double exponent,int n) {
        this.exponent = exponent;
        this.n = n;
        //calculate sum
        BigDecimal temp = new BigDecimal("0.0");
        for(int i = 1; i < n+1; i++) {
            temp = temp.add(new BigDecimal(1/Math.pow(i,exponent)));
        }
        sum = temp.setScale(32,RoundingMode.HALF_EVEN);
        initialize();
    }

    public void initialize() {
        ranks_frequencies = new TreeMap<BigDecimal,Integer>();
        total = new BigDecimal("0.0");
        for(int i = 1; i <=n; i++) {
            total = total.add(getZipfVal(i));
            ranks_frequencies.put(total,i);
        }
    }

    /* Get Zipf frequency value given a rank */
    public BigDecimal getZipfVal(int rank) {
        return new BigDecimal(1/Math.pow(rank,exponent)).divide(sum,32,RoundingMode.HALF_EVEN);
    }

    /* Get rank given a Zipf frequency value */
    public int getRank(BigDecimal zipfval) {
        return ranks_frequencies.ceilingEntry(zipfval).getValue();
    }
}