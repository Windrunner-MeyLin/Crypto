package nist;

public final class Gamma {

    private Gamma() {}

    private static double lnGamma(double x) {
        double[] c = {76.18009172947146, -86.50532032941677,
                24.01409824083091, -1.231739572450155,
                0.1208650973866179e-2, -0.5395239384953e-5};
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) {
            y += 1.0;
            ser += c[j] / y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    public static double regularizedP(double a, double x) {
        if (x <= 0.0) return 0.0;
        if (x < a + 1.0) {
            return seriesP(a, x);
        } else {
            return 1.0 - continuedFractionQ(a, x);
        }
    }

    public static double regularizedQ(double a, double x) {
        if (x <= 0.0) return 1.0;
        if (x < a + 1.0) {
            return 1.0 - seriesP(a, x);
        } else {
            if (x > a + 50.0) return 0.0;
            return continuedFractionQ(a, x);
        }
    }

    private static double seriesP(double a, double x) {
        double eps = 1e-14;
        double ap = a;
        double sum = 1.0 / a;
        double del = sum;
        for (int n = 1; n <= 500; n++) {
            ap += 1.0;
            del *= x / ap;
            sum += del;
            if (Math.abs(del) < Math.abs(sum) * eps) break;
        }
        return sum * Math.exp(-x + a * Math.log(x) - lnGamma(a));
    }

    private static double continuedFractionQ(double a, double x) {
        double eps = 1e-14;
        double fpmin = 1e-300;
        double b = x + 1.0 - a;
        double c = 1.0 / fpmin;
        double d = 1.0 / b;
        double h = d;
        for (int i = 1; i <= 500; i++) {
            double an = -i * (i - a);
            b += 2.0;
            d = an * d + b;
            if (Math.abs(d) < fpmin) d = fpmin;
            c = b + an / c;
            if (Math.abs(c) < fpmin) c = fpmin;
            d = 1.0 / d;
            double del = d * c;
            h *= del;
            if (Math.abs(del - 1.0) < eps) break;
        }
        return Math.exp(-x + a * Math.log(x) - lnGamma(a)) * h;
    }
}
