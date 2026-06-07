package nist;

public final class RunsTest {

    private RunsTest() {}

    public static NistTestResult run(int[] bits, double alpha) {
        int n = bits.length;
        double pi = 0.0;
        for (int b : bits) pi += b;
        pi /= n;
        if (Math.abs(pi - 0.5) >= 2.0 / Math.sqrt(n)) {
            return new NistTestResult("Runs", "游程检验", 0.0, false,
                    "频率预检验未通过(pi=" + String.format("%.4f", pi) + ")");
        }
        int V = 1;
        for (int i = 1; i < n; i++) {
            if (bits[i] != bits[i - 1]) V++;
        }
        double num = Math.abs(V - 2.0 * n * pi * (1.0 - pi));
        double den = 2.0 * Math.sqrt(2.0 * n) * pi * (1.0 - pi);
        double p = FrequencyTest.erfc(num / den);
        return new NistTestResult("Runs", "游程检验", p, p >= alpha,
                String.format("V=%d pi=%.4f", V, pi));
    }
}
