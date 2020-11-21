# AutomatedTesting2020

选题方向：经典自动化测试

***

`算法释义，所用第三方库及版本，程序入口，程序结构……`

- 项目简介：
- 创建了如下六个类来实现目标。
  - Main：主类，解析输入参数，调用其他类进行分析
    - findClass函数：递归寻找.class文件，将其地址存入数组并返回数组
  - AnalysisScopeMaker：用于建立分析域
    - loadClass函数： 读取.class文件，建立并返回AnalysisScope对象
  - Graph： 图类，用于保存调用信息
  - GraphMaker： 用于创建类调用图和方法调用图
    - analyze函数： 从WALA自带的cha算法生成的调用图中获取所有测试方法，存入Main的静态数组TestMethods中；并且获取类调用和方法调用关系，生成类调用图和方法调用图。
  - Dot： 创建、编辑并输出dot文件；
  - TestMethodSelector：测试方法选择类
    - select函数：读取change_info.txt，调用selectTestMethod函数得到结果并输出。
    - selectTestMethod函数： 迭代遍历产生变化的类和方法和受到影响的类和方法；从中选出测试方法或类。



