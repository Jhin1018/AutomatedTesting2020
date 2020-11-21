import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestMethodSelector {
    public void select(String mode, String change_info_path,ArrayList<Graph> graphs) throws IOException {
        Graph classCallGraph = graphs.get(0);
        Graph methodCallGraph = graphs.get(1);

        // 产生变化的类和方法
        Set<String> changedClass = new HashSet<>();
        Set<String> changedMethod = new HashSet<>();
        // 读取change_info文件
        FileInputStream inputStream = new FileInputStream(change_info_path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            changedClass.add(str.split(" ")[0]);
            changedMethod.add(str);
        }
        bufferedReader.close();
        inputStream.close();

        // 判断输出模式
        if (mode.equals("-m")) {
            // 输出方法级测试选择
            FileWriter writer = new FileWriter("selection-method.txt");
            writer.write(String.join("\n", this.selectTestMethod(changedMethod,methodCallGraph)));
            writer.flush();
            writer.close();
        }

        if (mode.equals("-c")) {
            // 输出被选择的测试类
            FileWriter writer = new FileWriter("selection-class.txt");
            writer.write(String.join("\n", this.selectTestMethod(changedClass,classCallGraph)));
            writer.flush();
            writer.close();
        }
    }

    public Set<String> selectTestMethod(Set<String> changed, Graph graph) {
        // 被修改的方法默认添加到集合中
        Set<String> callers = new HashSet<>(changed);

        while (true) {
            Set<String> newCallers = new HashSet<>(callers);
            // 遍历每个调用了被修改过的方法的类或方法，加入集合中
            for (String caller : callers) {
                newCallers.addAll(graph.graph.get(caller));
            }
            // 节点数量不发生变化时说明已经遍历结束
            if (newCallers.size() == callers.size()) {
                break;
            }
            callers = newCallers;
        }

        // 选择测试方法
        Set<String> selected = new HashSet<>();

        if (graph.type == 0) {
            for (String testMethod : Main.testMethods) {
                if (callers.contains(testMethod.split(" ")[0])) {
                    selected.add(testMethod);
                }
            }
        } else if (graph.type == 1) {
            for (String testMethod : Main.testMethods) {
                if (callers.contains(testMethod)) {
                    selected.add(testMethod);
                }
            }
        }

        return selected;
    }
}
