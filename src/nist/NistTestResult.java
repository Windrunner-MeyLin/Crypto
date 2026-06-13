package nist;

public final class NistTestResult {
    public final String name;
    public final String chineseName;
    public final double pValue;
    public final boolean pass;
    public final String extra;

    public NistTestResult(String name, String chineseName, double pValue, boolean pass, String extra) {
        this.name = name;
        this.chineseName = chineseName;
        this.pValue = pValue;
        this.pass = pass;
        this.extra = extra == null ? "" : extra;
    }

    public NistTestResult(String name, String chineseName, double pValue, boolean pass) {
        this(name, chineseName, pValue, pass, "");
    }
}
