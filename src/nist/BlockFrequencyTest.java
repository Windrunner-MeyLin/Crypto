package nist;

public final class BlockFrequencyTest {

    private BlockFrequencyTest() {}

    public static NistTestResult run(int[] bits, int m, double alpha) {
        int n = bits.length;
        if (n < m) return new NistTestResult("Block Frequency", "块内频数", 0, false, "样本不足");
        int blocks = n / m;
        double chi2 = 0.0;
        for (int b = 0; b < blocks; b++) {
            int ones = 0;
            for (int j = 0; j < m; j++) {
                if (bits[b * m + j] == 1) ones++;
            }
            double pi = (double) ones / m;
            chi2 += (pi - 0.5) * (pi - 0.5);
        }
        chi2 = 4.0 * m * chi2;
        double p = Gamma.regularizedQ(blocks / 2.0, chi2 / 2.0);
        return new NistTestResult("Block Frequency", "块内频数", p, p >= alpha,
                String.format("M=%d blocks=%d chi2=%.4f", m, blocks, chi2));
    }
}
