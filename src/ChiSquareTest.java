import nist.Gamma;

public final class ChiSquareTest {

    private ChiSquareTest() {}

    public static class Result {
        public final double chiSquare;
        public final int degreesOfFreedom;
        public final double pValue;

        public Result(double chi, int df, double p) {
            this.chiSquare = chi;
            this.degreesOfFreedom = df;
            this.pValue = p;
        }
    }

    public static Result testUniform(long[] observed) {
        int k = observed.length;
        long total = 0;
        for (long o : observed) total += o;
        double expected = (double) total / k;
        double chi = 0.0;
        for (long o : observed) {
            double d = o - expected;
            chi += d * d / expected;
        }
        int df = k - 1;
        double p = Gamma.regularizedQ(df / 2.0, chi / 2.0);
        return new Result(chi, df, p);
    }

    public static double chiSquareCDF(double x, int df) {
        return Gamma.regularizedP(df / 2.0, x / 2.0);
    }
}
