import java.util.Random;

/* Generate ASCII character ids of length 4 (32 bits) */
public class IdGenerator {

    int size;
    char[] string;
    Random rand;

    public IdGenerator(int size) {
        this.size = size;
        string = new char[size];
        for(int i = 0; i < size; i++) string[i] = 0;
        rand = new Random();
    }

    public String generate() {
        for(int i =0; i < size; i++ )
            string[i] = (char)(rand.nextInt(94)+32); //to skip special characters like [BACKSPACE],[DEL] etc
        return new String(string);
    }

}