public class test {

    public static void main(String[] args) {
        BloomFilter f = new BloomFilter(10,5);

        f.add("hello");
        System.out.println(f.exists("hello"));
        System.out.println(f.exists("goodbye"));
    }
}