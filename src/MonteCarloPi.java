import java.util.Random;

public final class MonteCarloPi {

    private MonteCarloPi() {}

    public static double estimatePi(int M, long seed) {
        Random rng = new Random(seed);
        int inside = 0;
        for (int i = 0; i < M; i++) {
            double x = (rng.nextInt(10000)) / 1000.0;
            double y = (rng.nextInt(10000)) / 1000.0;
            if (x * x + y * y <= 100.0) inside++;
        }
        return 4.0 * inside / M;
    }

    public static class MultiResult {
        public final int[] sizes;
        public final double[] estimates;
        public final double[] errors;
        public MultiResult(int[] sizes, double[] est, double[] err) {
            this.sizes = sizes; this.estimates = est; this.errors = err;
        }
    }

    public static MultiResult estimateSeries(int[] sizes, long seed) {
        Random rng = new Random(seed);
        double[] est = new double[sizes.length];
        double[] err = new double[sizes.length];
        int total = 0;
        int inside = 0;
        int idx = 0;
        int max = sizes[sizes.length - 1];
        for (int i = 0; i < max; i++) {
            double x = (rng.nextInt(10000)) / 1000.0;
            double y = (rng.nextInt(10000)) / 1000.0;
            total++;
            if (x * x + y * y <= 100.0) inside++;
            if (idx < sizes.length && i + 1 == sizes[idx]) {
                est[idx] = 4.0 * inside / total;
                err[idx] = Math.abs(est[idx] - Math.PI);
                idx++;
            }
        }
        return new MultiResult(sizes, est, err);
    }
}
