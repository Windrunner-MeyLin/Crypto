import java.util.Random;

public final class RandomSource {

    private final Random rng;
    private final int n;

    public RandomSource(int n, long seed) {
        if (n <= 0) throw new IllegalArgumentException("N must be positive");
        this.n = n;
        this.rng = new Random(seed);
    }

    public int nextInt() {
        int raw = rng.nextInt(Integer.MAX_VALUE);
        int x = raw % n;
        if (x < 0) x += n;
        return x;
    }

    public int[] generate(int count) {
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) arr[i] = nextInt();
        return arr;
    }

    public int getN() { return n; }
}
