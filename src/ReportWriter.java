import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import nist.NistTestResult;

public final class ReportWriter {

    private final PrintStream console;
    private final StringBuilder buffer = new StringBuilder();

    public ReportWriter() {
        this.console = System.out;
    }

    public ReportWriter println(String s) {
        console.println(s);
        buffer.append(s).append('\n');
        return this;
    }

    public ReportWriter print(String s) {
        console.print(s);
        buffer.append(s);
        return this;
    }

    public ReportWriter printf(String fmt, Object... args) {
        String s = String.format(fmt, args);
        console.print(s);
        buffer.append(s);
        return this;
    }

    public ReportWriter line() {
        console.println();
        buffer.append('\n');
        return this;
    }

    public ReportWriter header(String title) {
        String bar = "=".repeat(60);
        println(bar);
        println(title);
        println(bar);
        return this;
    }

    public ReportWriter subHeader(String title) {
        String bar = "-".repeat(50);
        println(bar);
        println(title);
        println(bar);
        return this;
    }

    public void writeToFile(String path) {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path, false), StandardCharsets.UTF_8)) {
            w.write(buffer.toString());
            console.println("\n[报告已写入: " + path + "]");
        } catch (IOException e) {
            console.println("写报告失败: " + e.getMessage());
        }
    }

    public String dumpNistTable(String title, List<NistTestResult> results, double alpha) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-30s %-20s %-12s %-6s %s%n",
                "Test", "中文名", "p-value", "结果", "附加信息"));
        sb.append("-".repeat(100)).append("\n");
        for (NistTestResult r : results) {
            String mark = r.pass ? "✓" : "✗";
            sb.append(String.format("%-30s %-20s %-12.6f %-6s %s%n",
                    r.name, r.chineseName, r.pValue, mark, r.extra));
        }
        return sb.toString();
    }
}
