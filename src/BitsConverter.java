public final class BitsConverter {

    private BitsConverter() {}

    public static int[] toBits(int[] values, int bitsPerValue) {
        int[] out = new int[values.length * bitsPerValue];
        for (int i = 0; i < values.length; i++) {
            int v = values[i] & ((1 << bitsPerValue) - 1);
            for (int b = bitsPerValue - 1; b >= 0; b--) {
                out[i * bitsPerValue + (bitsPerValue - 1 - b)] = (v >> b) & 1;
            }
        }
        return out;
    }

    public static int[] toBitsFromBounded(int[] values, int n) {
        int bitsPerValue = 0;
        int t = n - 1;
        while (t > 0) {
            bitsPerValue++;
            t >>= 1;
        }
        if (bitsPerValue == 0) bitsPerValue = 1;
        return toBits(values, bitsPerValue);
    }

    public static String toBitString(int[] bits) {
        StringBuilder sb = new StringBuilder(bits.length);
        for (int b : bits) sb.append(b);
        return sb.toString();
    }

    public static int[] toBitString(String s) {
        int[] out = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '0') out[i] = 0;
            else if (c == '1') out[i] = 1;
            else throw new IllegalArgumentException("invalid char: " + c);
        }
        return out;
    }
}
