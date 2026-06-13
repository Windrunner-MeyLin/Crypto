package nist;

public final class SerialTest {

    private SerialTest() {}

    public static NistTestResult run(int[] bits, int m, double alpha) {
        int n = bits.length;
        int total = 1 << m;
        int[] freq = new int[total];
        for (int i = 0; i < n; i++) {
            int idx = 0;
            for (int k = 0; k < m; k++) {
                idx = (idx << 1) | bits[(i + k) % n];
            }
            freq[idx]++;
        }
        double psiSqM = 0.0;
        for (int f : freq) psiSqM += f * f;
        psiSqM = total * psiSqM / n - n;
        int[] freqM1 = new int[total / 2];
        for (int i = 0; i < n; i++) {
            int idx = 0;
            for (int k = 0; k < m - 1; k++) {
                idx = (idx << 1) | bits[(i + k) % n];
            }
            freqM1[idx]++;
        }
        double psiSqM1 = 0.0;
        for (int f : freqM1) psiSqM1 += f * f;
        psiSqM1 = (total / 2) * psiSqM1 / n - n;
        int[] freqMm1 = new int[total / 2];
        for (int i = 0; i < n; i++) {
            int idx = 0;
            for (int k = 1; k < m; k++) {
                idx = (idx << 1) | bits[(i + k) % n];
            }
            freqMm1[idx]++;
        }
        double psiSqMm1 = 0.0;
        for (int f : freqMm1) psiSqMm1 += f * f;
        psiSqMm1 = (total / 2) * psiSqMm1 / n - n;
        double gradPsiM = psiSqM - psiSqM1;
        double gradPsiMm1 = psiSqM1 - psiSqMm1;
        double a = Math.pow(2, m - 1) / 2.0;
        double p1 = Gamma.regularizedQ(a, gradPsiM / 2.0);
        double p2 = Gamma.regularizedQ(a, gradPsiMm1 / 2.0);
        double p = Math.min(p1, p2);
        return new NistTestResult("Serial", "序列检验", p, p >= alpha,
                String.format("m=%d p1=%.4f p2=%.4f", m, p1, p2));
    }
}
