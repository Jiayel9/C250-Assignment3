import java.util.Random;
public class Main {
    public static void main(String[] args) {

        Random gen = new Random();
        System.out.println(gen.nextFloat(1));
        float random = 0;
        if (true) {
            random = gen.nextFloat(1);
        }
        System.out.println(random);
    }
}