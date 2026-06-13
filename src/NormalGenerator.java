import java.util.Random;

public final class NormalGenerator {

    private final Random rng;

    public NormalGenerator(long seed) {
        this.rng = new Random(seed);
    }

    public double nextGaussian() {
        double u1, u2;
        do {
            u1 = rng.nextDouble();
        } while (u1 <= 1e-12);
        u2 = rng.nextDouble();
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
    }

    public double[] generate(int count) {
        double[] arr = new double[count];
        for (int i = 0; i < count; i++) arr[i] = nextGaussian();
        return arr;
    }
}
