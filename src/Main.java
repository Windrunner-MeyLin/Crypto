import java.util.ArrayList;
import java.util.List;

import nist.NistTestResult;
import nist.FrequencyTest;
import nist.BlockFrequencyTest;
import nist.RunsTest;
import nist.LongestRunTest;
import nist.RankTest;
import nist.SpectralTest;
import nist.SerialTest;

public class Main {

    static final int N = 10;
    static final int SAMPLE_SIZE = 1_048_576;
    static final double ALPHA = 0.01;
    static final int BLOCK_M = 1000;
    static final int SERIAL_M = 2;
    static final int NORMAL_COUNT = 1_000_000;
    static final int PI_M_MAX = 1_000_000;

    public static void main(String[] args) {
        ReportWriter rw = new ReportWriter();
        long seed = 20240607L;

        rw.header("随机数统计分布测试与蒙特卡罗应用");
        rw.printf("参数: N=%d, 样本量=%d, 显著性水平α=%.3f, 种子=%d%n",
                N, SAMPLE_SIZE, ALPHA, seed);
        rw.line();

        step1_distribution(rw, seed);
        step2_nist_clean(rw, seed);
        step3_normal(rw, seed);
        step4_montecarlo(rw, seed);

        rw.header("报告结束");
        rw.writeToFile("report.txt");
    }

    static void step1_distribution(ReportWriter rw, long seed) {
        rw.header("1. rand()%N 概率分布与重复间隔统计");
        RandomSource rs = new RandomSource(N, seed);
        int[] data = rs.generate(SAMPLE_SIZE);
        long[] freq = DistributionAnalyzer.frequency(data, N);
        double[] p = DistributionAnalyzer.probability(freq);

        rw.printf("%-6s %-12s %-12s %-12s %-12s %-12s%n",
                "值", "观测频数", "期望频数", "实际概率", "期望概率", "偏差");
        for (int i = 0; i < N; i++) {
            double dev = p[i] - 1.0 / N;
            rw.printf("%-6d %-12d %-12.0f %-12.6f %-12.6f %+.6f%n",
                    i, freq[i], (double) SAMPLE_SIZE / N, p[i], 1.0 / N, dev);
        }
        rw.printf("样本均值: %.4f (理论: %.1f)   样本方差: %.4f (理论: %.4f)%n",
                DistributionAnalyzer.mean(p),
                (N - 1) / 2.0,
                DistributionAnalyzer.variance(p),
                (N * N - 1) / 12.0);
        rw.line();

        rw.subHeader("重复出现间隔统计 (各值出现间隔的均值与标准差)");
        int[][] gaps = DistributionAnalyzer.gapDistribution(data, N);
        rw.printf("%-6s %-10s %-14s %-14s%n", "值", "间隔数", "平均间隔", "间隔标准差");
        for (int i = 0; i < N; i++) {
            double m = DistributionAnalyzer.meanGap(gaps[i]);
            double s = DistributionAnalyzer.stdGap(gaps[i], m);
            rw.printf("%-6d %-10d %-14.4f %-14.4f%n", i, gaps[i].length, m, s);
        }
        rw.printf("(理论平均间隔 = N = %d)%n", N);
        rw.line();

        rw.subHeader("1.1 卡方检验 (整数均匀性测评工具)");
        ChiSquareTest.Result r = ChiSquareTest.testUniform(freq);
        rw.printf("χ²统计量: %.4f   自由度: %d   p-value: %.6f%n",
                r.chiSquare, r.degreesOfFreedom, r.pValue);
        rw.printf("判定 (α=%.2f): %s%n", ALPHA,
                r.pValue >= ALPHA ? "✓ 通过 (序列均匀)" : "✗ 不通过 (序列不均匀)");
        rw.line();
    }

    static void step2_nist_clean(ReportWriter rw, long seed) {
        rw.header("2. NIST STS 随机性测试 (7项核心) — 均匀比特流对照组");
        rw.printf("(注: 比特由 java.util.Random.nextInt(2) 独立产生,");
        rw.printf("避免 rand()%%N 整数到比特的编码偏置干扰测试结论)%n");

        java.util.Random rng = new java.util.Random(seed + 2);
        int[] bits = new int[SAMPLE_SIZE];
        for (int i = 0; i < SAMPLE_SIZE; i++) bits[i] = rng.nextInt(2);
        rw.printf("比特流长度: %d bits (即 2^20)%n", bits.length);

        List<NistTestResult> rs = new ArrayList<>();
        rs.add(FrequencyTest.run(bits, ALPHA));
        rs.add(BlockFrequencyTest.run(bits, BLOCK_M, ALPHA));
        rs.add(RunsTest.run(bits, ALPHA));
        rs.add(LongestRunTest.run(bits, ALPHA));
        rs.add(RankTest.run(bits, ALPHA));
        rs.add(SpectralTest.run(bits, ALPHA));
        rs.add(SerialTest.run(bits, SERIAL_M, ALPHA));
        rw.print(rw.dumpNistTable("Uniform bits", rs, ALPHA));

        long passed = rs.stream().filter(x -> x.pass).count();
        rw.printf("通过率: %d / %d   (%s)%n", passed, rs.size(),
                passed == rs.size() ? "全部通过, 测试方法学有效" : "存在失败项, 需查因");
        rw.line();
    }

    static void step3_normal(ReportWriter rw, long seed) {
        rw.header("3. 正态分布生成 (Box-Muller) 与验证");
        NormalGenerator ng = new NormalGenerator(seed);
        double[] data = ng.generate(NORMAL_COUNT);

        double sum = 0;
        for (double v : data) sum += v;
        double mean = sum / data.length;
        double varSum = 0;
        for (double v : data) varSum += (v - mean) * (v - mean);
        double variance = varSum / data.length;
        double stddev = Math.sqrt(variance);

        rw.printf("样本数: %d   样本均值: %.6f (理论: 0)   样本方差: %.6f (理论: 1)   标准差: %.6f%n",
                data.length, mean, variance, stddev);
        rw.line();

        rw.subHeader("12 区间直方图 (理论标准正态 CDF 积分)");
        int BINS = 12;
        double lo = -3.0, hi = 3.0;
        double step = (hi - lo) / BINS;
        long[] hist = new long[BINS];
        long overflowLow = 0, overflowHigh = 0;
        for (double v : data) {
            if (v < lo) { overflowLow++; continue; }
            if (v >= hi) { overflowHigh++; continue; }
            int idx = (int) ((v - lo) / step);
            if (idx < 0) idx = 0;
            if (idx >= BINS) idx = BINS - 1;
            hist[idx]++;
        }
        long maxCount = 0;
        for (long c : hist) if (c > maxCount) maxCount = c;
        rw.printf("%-14s %-12s %-12s %-12s%n", "区间", "观测频数", "理论频数", "直方图");
        double[] expected = new double[BINS];
        for (int i = 0; i < BINS; i++) {
            double zLo = lo + i * step;
            double zHi = lo + (i + 1) * step;
            expected[i] = (normalCDF(zHi) - normalCDF(zLo)) * data.length;
        }
        for (int i = 0; i < BINS; i++) {
            int barLen = (int) Math.round(40.0 * hist[i] / maxCount);
            StringBuilder bar = new StringBuilder();
            for (int b = 0; b < barLen; b++) bar.append('█');
            rw.printf("[%5.2f,%5.2f) %-12d %-12.0f %s%n",
                    lo + i * step, lo + (i + 1) * step, hist[i], expected[i], bar);
        }
        rw.printf("越界 < -3.00: %d   越界 >= 3.00: %d%n", overflowLow, overflowHigh);
        rw.line();

        rw.subHeader("正态分布卡方拟合检验 (12 区间, 用 CDF 计算期望频数)");
        double chi2 = 0;
        for (int i = 0; i < BINS; i++) {
            double d = hist[i] - expected[i];
            chi2 += d * d / expected[i];
        }
        int df = BINS - 1;
        double p = nist.Gamma.regularizedQ(df / 2.0, chi2 / 2.0);
        rw.printf("χ²统计量: %.4f   自由度: %d   p-value: %.6f   %s%n",
                chi2, df, p, p >= ALPHA ? "✓ 通过 (符合正态分布)" : "✗ 不通过");
        rw.line();
    }

    static double normalCDF(double z) {
        return 1.0 - 0.5 * nist.FrequencyTest.erfc(z / Math.sqrt(2.0));
    }

    static void step4_montecarlo(ReportWriter rw, long seed) {
        rw.header("4. 蒙特卡罗法求 π");
        rw.println("公式: x, y = (rand()%10000)/1000.0 ∈ [0, 10)");
        rw.println("      π ≈ 4 × (满足 x² + y² ≤ 100 的点数 / 总点数)");
        rw.line();

        int[] sizes = {1_000, 10_000, 100_000, 1_000_000};
        MonteCarloPi.MultiResult mr = MonteCarloPi.estimateSeries(sizes, seed + 3);
        rw.printf("%-12s %-18s %-18s %-18s%n",
                "样本量M", "π估计值", "真值", "绝对误差");
        for (int i = 0; i < sizes.length; i++) {
            rw.printf("%-12d %-18.8f %-18.8f %-18.8f%n",
                    mr.sizes[i], mr.estimates[i], Math.PI, mr.errors[i]);
        }
        rw.printf("%n真值 π = %.10f%n", Math.PI);
        rw.line();
    }
}
