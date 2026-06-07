package nist;

public final class LongestRunTest {

    private LongestRunTest() {}

    public static NistTestResult run(int[] bits, double alpha) {
        int n = bits.length;
        if (n < 6272) {
            return new NistTestResult("Longest Run of Ones", "最长1游程", 0, false, "样本不足(<6272)");
        }
        int M, K;
        double[] pi;
        if (n < 1000000) {
            M = 8; K = 4;
            pi = new double[]{0.2148, 0.3672, 0.2305, 0.1875};
        } else {
            M = 128; K = 6;
            pi = new double[]{0.1174, 0.2430, 0.2493, 0.1752, 0.1027, 0.1124};
        }
        int blocks = n / M;
        int[] nu = new int[K];
        for (int b = 0; b < blocks; b++) {
            int run = 0, maxRun = 0;
            for (int j = 0; j < M; j++) {
                if (bits[b * M + j] == 1) {
                    run++;
                    if (run > maxRun) maxRun = run;
                } else {
                    run = 0;
                }
            }
            int cls;
            if (M == 8) {
                if (maxRun <= 1) cls = 0;
                else if (maxRun == 2) cls = 1;
                else if (maxRun == 3) cls = 2;
                else cls = 3;
            } else {
                if (maxRun <= 4) cls = 0;
                else if (maxRun == 5) cls = 1;
                else if (maxRun == 6) cls = 2;
                else if (maxRun == 7) cls = 3;
                else if (maxRun == 8) cls = 4;
                else cls = 5;
            }
            nu[cls]++;
        }
        double chi2 = 0.0;
        for (int k = 0; k < K; k++) {
            double expected = blocks * pi[k];
            double d = nu[k] - expected;
            chi2 += d * d / expected;
        }
        int df = K - 1;
        double p = Gamma.regularizedQ(df / 2.0, chi2 / 2.0);
        return new NistTestResult("Longest Run of Ones", "最长1游程", p, p >= alpha,
                String.format("M=%d blocks=%d chi2=%.4f df=%d", M, blocks, chi2, df));
    }
}
