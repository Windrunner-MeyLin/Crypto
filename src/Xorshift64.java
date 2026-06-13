public final class Xorshift64 {

    private long state;

    public Xorshift64(long seed) {
        if (seed == 0) seed = 0xDEADBEEFL;
        this.state = seed;
    }

    public int nextInt() {
        state ^= state >>> 12;
        state ^= state << 25;
        state ^= state >>> 27;
        return (int) ((state * 0x9E3779B97F4A7C15L) >>> 32);
    }

    public int nextInt(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive");
        int r = nextInt() & Integer.MAX_VALUE;
        return r % n;
    }
}
