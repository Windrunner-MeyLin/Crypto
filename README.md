# 基于 NIST STS 的伪随机数统计分布测评与蒙特卡罗应用

《密码学导论》课程大作业 —  `rand()%N` 随机数统计分布测评工具，含 10 项测评、自设计 PRNG、正态分布生成、蒙特卡罗 π。

## 项目结构

```
crypto/
├── README.md                      本文件
├── result.txt                     程序输出
├── 基于 NIST STS 的伪随机数统计分布测评与蒙特卡罗应用实验报告.pdf
└── src/
    ├── Main.java                  主程序入口 (25 个静态方法)
    ├── RandomSource.java          rand()%N 模拟 (被测对象)
    ├── UniformGenerator.java      均匀分布包装器 (IntSupplier 注入)
    ├── Xorshift64.java            自设计 xorshift64* PRNG 
    ├── Lcg64.java                 LCG64 MMIX 对照
    ├── NormalGenerator.java       Box-Muller 正态分布
    ├── BitsConverter.java         整数序列 ↔ 比特流
    ├── DistributionAnalyzer.java  概率/间隔统计
    ├── ChiSquareTest.java         整数卡方检验
    ├── MonteCarloPi.java          蒙特卡罗求 π
    ├── ReportWriter.java          报告输出 (控制台 + UTF-8 文件)
    └── nist/
        ├── Gamma.java             正则化不完全 Gamma 函数 (P / Q)
        ├── NistTestResult.java    单项测试结果
        ├── FrequencyTest.java     1) 频数检验 (Monobit)
        ├── BlockFrequencyTest.java 2) 块内频数
        ├── RunsTest.java          3) 游程检验
        ├── LongestRunTest.java    4) 块内最长 1-游程
        ├── RankTest.java          5) 二进制矩阵秩
        ├── SpectralTest.java      6) 离散傅里叶 (频谱)
        └── SerialTest.java        7) 序列检验
```

## 编译运行

### IntelliJ IDEA
1. `File → Open` → 选中 `D:\code_java\crypto` 目录
2. 右键 `src` → `Mark Directory as → Sources Root`
3. 右键 `Main.java` → `Run 'Main.main()'`

### 命令行
```bash
cd D:\code_java\crypto
javac -encoding UTF-8 -d out src\*.java src\nist\*.java
java -Dfile.encoding=UTF-8 -cp out Main
```

控制台输出同步写入 `report.txt` (UTF-8 编码)。

## 测评工具一览 (10 项)

| # | 工具 | 适用 | 来源 |
|---|------|------|------|
| 1 | 卡方检验 (整数均匀性) | N-状态整数序列 | 自研 |
| 2 | Frequency (Monobit) | 比特流 | NIST §2.1 |
| 3 | Block Frequency (M=1000) | 比特流 | NIST §2.2 |
| 4 | Runs | 比特流 | NIST §2.3 |
| 5 | Longest Run of Ones (M=128) | 比特流 | NIST §2.4 |
| 6 | Binary Matrix Rank (32×32) | 比特流 | NIST §2.5 |
| 7 | Discrete Fourier Transform | 比特流 | NIST §2.6 |
| 8 | Serial (m=2) | 比特流 | NIST §2.11 |
| 9 | 多 N 取模扫描 (N=2~1000) | 整数序列 | 自研 |
| 10 | PRNG 三栏对比 | 比特流 | 自研 |

## 自设计 PRNG

### Xorshift64\* (主生成器)
Marsaglia (2003) 移位寄存 + 乘法混洗架构, 周期 2^64-1, NIST 7/7 全通过。

```java
public int nextInt() {
    state ^= state >>> 12;
    state ^= state << 25;
    state ^= state >>> 27;
    return (int)((state * 0x9E3779B97F4A7C15L) >>> 32);
}
```

### LCG64 (MMIX 对照)
Knuth MMIX 参数, 周期 2^64, 输出高 32 位回避低比特弱点。

```java
public int nextInt() {
    state = 6364136223846793005L * state + 1442695040888963407L;
    return (int)(state >>> 32);
}
```

## 核心算法

- **rand()%N 模拟**: `new Random().nextInt(Integer.MAX_VALUE) % N`
- **均匀分布基线**: `java.util.Random.nextInt(N)`
- **正态分布**: Box-Muller `Z = √(-2 ln U₁) · cos(2π U₂)`
- **蒙特卡罗 π**: `(x,y) = (rand()%10000)/1000.0`, `π ≈ 4 × inside / total`
- **NIST STS**: 7 项核心测试 (Frequency ~ Serial)

## 关键结果 (α=0.01, 种子=20240607L)

| 测试项 | 结果 |
|--------|------|
| rand()%N 整数 χ² | p=0.481 ✓ |
| NIST 对照组 | 7/7 ✓ |
| Xorshift64\* NIST | **7/7** ✓ (优于 JDK 4/7) |
| LCG64 NIST | 7/7 ✓ |
| Box-Muller χ² | p=0.381 ✓ |
| 蒙特卡罗 π (M=1M) | 3.14192, 误差 0.01% |
| 多 N 扫描 (N=2~1000) | 全部 ✓ |

## 参考

- NIST SP 800-22 Rev.1a (2010)
- Knuth D E. *The Art of Computer Programming*, Vol 2, §3.3
- Box & Muller. *A Note on the Generation of Random Normal Deviates* (1958)
- Marsaglia G. *Xorshift RNGs*. J. Statistical Software, 2003
