package nist;

public final class FrequencyTest {

    private FrequencyTest() {}

    public static NistTestResult run(int[] bits, double alpha) {
        int n = bits.length;
        long sum = 0;
        for (int b : bits) sum += (b == 0) ? -1 : 1;
        double sObs = Math.abs(sum) / Math.sqrt(2.0 * n);
        double p = erfc(sObs);
        return new NistTestResult("Frequency (Monobit)", "频数检验",
                p, p >= alpha, String.format("S_n/sqrt(2n)=%.4f", sObs));
    }

    public static double erfc(double x) {
        if (x == 0.0) return 1.0;
        double z = Math.abs(x);
        double t = 1.0 / (1.0 + 0.5 * z);
        double ans = t * Math.exp(-z * z - 1.26551223 +
                t * (1.00002368 +
                t * (0.37409196 +
                t * (0.09678418 +
                t * (-0.18628806 +
                t * (0.27886807 +
                t * (-1.13520398 +
                t * (1.48851587 +
                t * (-0.82215223 +
                t * 0.17087277)))))))));
        return (x >= 0) ? ans : 2.0 - ans;
    }
}
