import java.util.Random;

/* Generate ASCII character ids of length 4 (32 bits) */
public class IdGenerator {

    int size = 4;
    char[] string;
    Random rand;

    public IdGenerator() {
        string = new char[]{0,0,0,0};
        rand = new Random();
    }

    public String generate() {
        for(int i =0; i < size; i++ )
            string[i] = (char)(rand.nextInt(128));
        return new String(string);
    }

}