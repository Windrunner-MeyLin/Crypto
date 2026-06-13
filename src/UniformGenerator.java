import java.util.Random;
import java.util.function.IntSupplier;

public final class UniformGenerator {

    private final IntSupplier supplier;
    private final int n;

    public UniformGenerator(int n, long seed) {
        if (n <= 0) throw new IllegalArgumentException("N must be positive");
        this.n = n;
        Random rng = new Random(seed);
        this.supplier = () -> rng.nextInt(n);
    }

    public UniformGenerator(int n, IntSupplier supplier) {
        if (n <= 0) throw new IllegalArgumentException("N must be positive");
        this.n = n;
        this.supplier = supplier;
    }

    public int nextInt() {
        return supplier.getAsInt();
    }

    public int[] generate(int count) {
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) arr[i] = nextInt();
        return arr;
    }

    public int getN() { return n; }
}
