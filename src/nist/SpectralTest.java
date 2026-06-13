package nist;

public final class SpectralTest {

    private SpectralTest() {}

    public static NistTestResult run(int[] bits, double alpha) {
        int n = bits.length;
        int pow2 = 1;
        while (pow2 * 2 <= n) pow2 <<= 1;
        if (pow2 < n) n = pow2;
        if (n < 1024) return new NistTestResult("Discrete Fourier Transform", "离散傅里叶(频谱)", 0, false, "样本不足");
        int[] x = new int[n];
        for (int i = 0; i < n; i++) x[i] = bits[i] == 1 ? 1 : -1;
        double[] re = new double[n];
        double[] im = new double[n];
        fft(x, re, im);
        double[] m = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            m[i] = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
        }
        double upper = Math.sqrt(2.995732274 * n);
        double N0 = 0.95 * (n / 2.0);
        double N1 = 0.0;
        for (int i = 0; i < n / 2; i++) if (m[i] < upper) N1++;
        double d = (N1 - N0) / Math.sqrt(n * 0.95 * 0.05 / 2.0);
        double p = FrequencyTest.erfc(Math.abs(d) / Math.sqrt(2.0));
        return new NistTestResult("Discrete Fourier Transform", "离散傅里叶(频谱)", p, p >= alpha,
                String.format("N1=%.0f N0=%.0f d=%.4f", N1, N0, d));
    }

    private static void fft(int[] x, double[] re, double[] im) {
        int n = x.length;
        int bits = 0;
        int t = n;
        while (t > 1) { bits++; t >>= 1; }
        for (int i = 0; i < n; i++) {
            int j = 0;
            for (int k = 0; k < bits; k++) {
                if (((i >> k) & 1) == 1) j |= 1 << (bits - 1 - k);
            }
            re[i] = x[j];
            im[i] = 0;
        }
        for (int size = 2; size <= n; size <<= 1) {
            int half = size >> 1;
            double angle = -2.0 * Math.PI / size;
            double wReStep = Math.cos(angle);
            double wImStep = Math.sin(angle);
            for (int start = 0; start < n; start += size) {
                double wRe = 1.0, wIm = 0.0;
                for (int k = 0; k < half; k++) {
                    int i1 = start + k;
                    int i2 = i1 + half;
                    double tRe = wRe * re[i2] - wIm * im[i2];
                    double tIm = wRe * im[i2] + wIm * re[i2];
                    re[i2] = re[i1] - tRe;
                    im[i2] = im[i1] - tIm;
                    re[i1] += tRe;
                    im[i1] += tIm;
                    double nwRe = wRe * wReStep - wIm * wImStep;
                    double nwIm = wRe * wImStep + wIm * wReStep;
                    wRe = nwRe;
                    wIm = nwIm;
                }
            }
        }
    }
}
