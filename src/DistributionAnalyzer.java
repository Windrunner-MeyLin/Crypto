public final class DistributionAnalyzer {

    private DistributionAnalyzer() {}

    public static long[] frequency(int[] data, int n) {
        long[] freq = new long[n];
        for (int v : data) {
            if (v >= 0 && v < n) freq[v]++;
        }
        return freq;
    }

    public static double[] probability(long[] freq) {
        long total = 0;
        for (long f : freq) total += f;
        double[] p = new double[freq.length];
        if (total == 0) return p;
        for (int i = 0; i < freq.length; i++) p[i] = (double) freq[i] / total;
        return p;
    }

    public static double mean(double[] p) {
        double m = 0.0;
        for (int i = 0; i < p.length; i++) m += i * p[i];
        return m;
    }

    public static double variance(double[] p) {
        double m = mean(p);
        double v = 0.0;
        for (int i = 0; i < p.length; i++) v += (i - m) * (i - m) * p[i];
        return v;
    }

    public static int[][] gapDistribution(int[] data, int n) {
        int[][] gaps = new int[n][];
        int[] lastPos = new int[n];
        int[] count = new int[n];
        for (int i = 0; i < n; i++) {
            lastPos[i] = -1;
            count[i] = 0;
        }
        for (int i = 0; i < data.length; i++) {
            int v = data[i];
            if (v >= 0 && v < n) {
                if (lastPos[v] >= 0) {
                    count[v]++;
                }
                lastPos[v] = i;
            }
        }
        for (int i = 0; i < n; i++) {
            gaps[i] = new int[count[i]];
        }
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            lastPos[i] = -1;
            idx[i] = 0;
        }
        for (int i = 0; i < data.length; i++) {
            int v = data[i];
            if (v >= 0 && v < n) {
                if (lastPos[v] >= 0) {
                    gaps[v][idx[v]++] = i - lastPos[v];
                }
                lastPos[v] = i;
            }
        }
        return gaps;
    }

    public static double meanGap(int[] gaps) {
        if (gaps.length == 0) return 0;
        long s = 0;
        for (int g : gaps) s += g;
        return (double) s / gaps.length;
    }

    public static double stdGap(int[] gaps, double mean) {
        if (gaps.length == 0) return 0;
        double s = 0;
        for (int g : gaps) s += (g - mean) * (g - mean);
        return Math.sqrt(s / gaps.length);
    }
}
