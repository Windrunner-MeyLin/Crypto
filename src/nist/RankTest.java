package nist;

public final class RankTest {

    private RankTest() {}

    public static NistTestResult run(int[] bits, double alpha) {
        int M = 32, Q = 32;
        int blockBits = M * Q;
        int n = bits.length;
        if (n < (M * Q * 38)) {
            return new NistTestResult("Binary Matrix Rank", "矩阵秩", 0, false, "样本不足");
        }
        int N = n / blockBits;
        int FM = 0, FMM = 0, rest = 0;
        for (int blk = 0; blk < N; blk++) {
            int rank = computeRank(bits, blk * blockBits, M, Q);
            if (rank == M) FM++;
            else if (rank == M - 1) FMM++;
            else rest++;
        }
        double pFull = 0.2887880950866025;
        double pMinus1 = 0.5775761901732050;
        double pRest = 1.0 - pFull - pMinus1;
        double eFull = N * pFull;
        double eMinus1 = N * pMinus1;
        double eRest = N * pRest;
        double chi2 = 0.0;
        if (eFull > 0) chi2 += (FM - eFull) * (FM - eFull) / eFull;
        if (eMinus1 > 0) chi2 += (FMM - eMinus1) * (FMM - eMinus1) / eMinus1;
        if (eRest > 0) chi2 += (rest - eRest) * (rest - eRest) / eRest;
        double p = Math.exp(-chi2 / 2.0);
        return new NistTestResult("Binary Matrix Rank", "矩阵秩", p, p >= alpha,
                String.format("N=%d FM=%d FMM=%d rest=%d chi2=%.4f", N, FM, FMM, rest, chi2));
    }

    private static int computeRank(int[] bits, int offset, int rows, int cols) {
        int[][] a = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                a[r][c] = bits[offset + r * cols + c] & 1;
            }
        }
        int rank = 0;
        int row = 0;
        for (int col = 0; col < cols && row < rows; col++) {
            int sel = -1;
            for (int i = row; i < rows; i++) {
                if (a[i][col] == 1) { sel = i; break; }
            }
            if (sel == -1) continue;
            if (sel != row) {
                int[] tmp = a[sel]; a[sel] = a[row]; a[row] = tmp;
            }
            for (int i = 0; i < rows; i++) {
                if (i != row && a[i][col] == 1) {
                    for (int k = col; k < cols; k++) {
                        a[i][k] ^= a[row][k];
                    }
                }
            }
            row++;
            rank++;
        }
        return rank;
    }
}
