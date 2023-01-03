# 自验证步骤

## 1、环境依赖

JDK1.8

maven

## 2、创建可执行文件

### 拉取最新源代码文件 执行命令生成可执行文件

`mvn clean package`

### 检查target目录下编译好的可执行文件 .jar

`./target/mexc-proof-of-reserves.jar`

## 3、验证步骤

将证明文件内容粘贴至当前文件夹的新文件中，并将此文件命名为“myProof.json”。然后，执行以下命令：

`java -jar ./target/mexc-proof-of-reserves.jar myProof.json`

观察执行结果：

验证通过

`validate result is true`

验证失败

`validate result is false`

### 3.1、代码执行

将证明文件内容粘贴至test/resources目录下的"merkle_proof.json"文件中，然后运行case类：com.mexc.merkle.MexcMerkleValidatorTest，观察测试结果
