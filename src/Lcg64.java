public final class Lcg64 {

    private long state;

    private static final long A = 6364136223846793005L;
    private static final long C = 1442695040888963407L;

    public Lcg64(long seed) {
        if (seed == 0) seed = 1;
        this.state = seed;
    }

    public int nextInt() {
        state = A * state + C;
        return (int) (state >>> 32);
    }

    public int nextInt(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive");
        int r = nextInt() & Integer.MAX_VALUE;
        return r % n;
    }
}
