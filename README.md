# 基于 NIST STS 的伪随机数统计分布测评与蒙特卡罗应用

《密码学导论》课程大作业 — 随机数统计分布测试工具。

## 项目结构

```
crypto/
├── 基于 NIST STS 的伪随机数统计分布测评与蒙特卡罗应用实验报告.pdf         实验报告
├── README.md                   本文件
├── report.txt                  程序运行时输出的纯文本报告
└── src/
    ├── Main.java               主程序入口
    ├── RandomSource.java       rand()%N 模拟 (被测对象)
    ├── UniformGenerator.java   Java Random.nextInt(N) 均匀分布
    ├── NormalGenerator.java    Box-Muller 正态分布
    ├── BitsConverter.java      整数序列 ↔ 比特流
    ├── DistributionAnalyzer.java  概率/间隔统计
    ├── ChiSquareTest.java      整数卡方检验
    ├── MonteCarloPi.java       蒙特卡罗求 π
    ├── ReportWriter.java       报告输出 (控制台 + UTF-8 文件)
    └── nist/
        ├── Gamma.java              正则化不完全 Gamma 函数 (P / Q)
        ├── NistTestResult.java     单项测试结果
        ├── FrequencyTest.java      1) 频数检验 (Monobit)
        ├── BlockFrequencyTest.java 2) 块内频数
        ├── RunsTest.java           3) 游程检验
        ├── LongestRunTest.java     4) 块内最长 1-游程
        ├── RankTest.java           5) 二进制矩阵秩
        ├── SpectralTest.java       6) 离散傅里叶 (频谱)
        └── SerialTest.java         7) 序列检验
```

## 编译运行

### IntelliJ IDEA

1. `File → Open` → 选中 `D:\code_java\crypto` 目录
2. 右键 `src` 目录 → `Mark Directory as → Sources Root`
3. 右键 `Main.java` → `Run 'Main.main()'`

### 命令行

```bash
cd D:\code_java\crypto
javac -encoding UTF-8 -d out src\*.java src\nist\*.java
java -Dfile.encoding=UTF-8 -cp out Main
```

控制台输出同步写入 `report.txt` (UTF-8 编码)。

## 测评工具一览 (≥ 5 项)

| # | 工具 | 适用 | NIST 编号 |
|---|---|---|---|
| 1 | 卡方检验 (整数均匀性) | N-状态整数序列 | 补充项 |
| 2 | Frequency (Monobit) | 比特流 | NIST §2.1 |
| 3 | Block Frequency | 比特流 | NIST §2.2 |
| 4 | Runs | 比特流 | NIST §2.3 |
| 5 | Longest Run of Ones | 比特流 | NIST §2.4 |
| 6 | Binary Matrix Rank | 比特流 | NIST §2.5 |
| 7 | Discrete Fourier Transform | 比特流 | NIST §2.6 |
| 8 | Serial | 比特流 | NIST §2.11 |

## 核心算法

- **`rand()%N` 模拟**：`new Random().nextInt(Integer.MAX_VALUE) % N` (Java 复现 C 风格)
- **均匀分布基线**：`java.util.Random.nextInt(N)` (L'Ecuyer LXM 算法)
- **正态分布**：Box-Muller 变换 `Z = √(-2 ln U₁) · cos(2π U₂)`
- **蒙特卡罗 π**：`x,y = (rand()%10000)/1000.0` 撒点，π ≈ 4 × (x² + y² ≤ 100 比例)

## 关键参数 (Main.java 顶部)

```java
N              = 10         // rand()%N 的模数
SAMPLE_SIZE    = 1_048_576  // 整数个数 = 2^20
ALPHA          = 0.01       // 显著性水平
BLOCK_M        = 1000       // 块内频数
SERIAL_M       = 2          // 序列检验 m
NORMAL_COUNT   = 1_000_000  // 正态分布样本数
```

## 输出示例

```
[1] rand()%N 整数级测评
    χ²=8.5389  df=9  p=0.4809  ✓ 通过 (序列均匀)

[2] NIST STS 7 项 (均匀比特流对照组)
    Frequency (Monobit)     p=0.8574  ✓
    Block Frequency         p=0.9075  ✓
    Runs                    p=0.5488  ✓
    Longest Run of Ones     p=0.4292  ✓
    Binary Matrix Rank      p=0.8125  ✓
    Discrete Fourier Trans. p=0.5754  ✓
    Serial                  p=0.8211  ✓
    通过率: 7 / 7

[3] 正态分布验证
    样本均值: -0.000342  (理论 0)
    样本方差: 1.002645   (理论 1)
    12 区间卡方: p=0.3809  ✓ 通过

[4] 蒙特卡罗 π
    M=1,000,000  π≈3.14192  误差 0.00032 (0.010%)
```

## 参考

- NIST SP 800-22 Rev.1a: *A Statistical Test Suite for Random and Pseudorandom Number Generators* (2010)
- Knuth D E. *The Art of Computer Programming*, Vol 2, §3.3
- Box G E P, Muller M E. *A Note on the Generation of Random Normal Deviates* (1958)
