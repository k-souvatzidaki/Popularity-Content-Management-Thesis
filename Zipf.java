import java.math.BigDecimal;
import java.math.RoundingMode;

/* Zipf's distribution implementation */
public class Zipf {

    final double exponent;
    final int n;
    final BigDecimal sum;

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
    }

    /* Get Zipf frequency value given a rank */
    public BigDecimal getZipfVal(int rank) {
        return new BigDecimal(1/Math.pow(rank,exponent)).divide(sum,32,RoundingMode.HALF_EVEN);
    }

    /* Get rank given a Zipf frequency value */
    public int getRank(BigDecimal zipfval) {
        BigDecimal temp = BigDecimal.ONE.divide(sum.multiply(zipfval),32,RoundingMode.HALF_EVEN);
        double result = (new BigDecimal(Math.pow(temp.doubleValue(),1/exponent))).doubleValue();
        return (int)Math.ceil((new BigDecimal(Math.pow(temp.doubleValue(),1/exponent))).doubleValue());
    }
}