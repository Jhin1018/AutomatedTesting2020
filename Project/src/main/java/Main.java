import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Main {
    static public Set<String> testMethods = new HashSet<>(); //用来记录被选中的测试方法

    public static void main(String[] args) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {
        //分析输入参数
        String mode = args[0];//取得本次进行测试用例选择的级别 -c:类级，-m:方法级
        String path = args[1];//项目路径
        String change_info_path = args[2];//变更文件路径

        ArrayList<String> file_paths = new ArrayList<>(); // 保存所有.class文件路径
        findClass(path, file_paths); // 遍历找到所有.class文件

        AnalysisScopeMaker analysisScopeMaker = new AnalysisScopeMaker();
        AnalysisScope scope = analysisScopeMaker.loadClass(file_paths);//构建分析域对象

        GraphMaker graphMaker = new GraphMaker();
        ArrayList<Graph> graphs = graphMaker.analyze(scope);//得到类调用图和方法调用图

        TestMethodSelector tms = new TestMethodSelector();
        tms.select(mode,change_info_path,graphs);//进行测试方法选择

    }

    public static void findClass(String path, ArrayList<String> file_paths) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    String s = f.getAbsolutePath();
                    if (f.isDirectory()) {
                        findClass(f.getAbsolutePath(), file_paths);
                    } else {
                        if (s.endsWith(".class")) {
                            file_paths.add(s);
                        }
                    }
                }
            }
        } else {
            System.out.println("Error：File doesn't exist！");
        }
    }



}


