import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.annotations.Annotation;
import com.ibm.wala.util.CancelException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GraphMaker {
    public ArrayList<Graph> analyze(AnalysisScope scope) throws ClassHierarchyException, CancelException, IOException {
        // 1.生成类层次关系对象
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        // 2.生成进入点
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        // 3.利用CHA算法构建调用图
        CHACallGraph cg = new CHACallGraph(cha);
        cg.init(eps);
        // 类、方法级别的调用图
        Graph classCallGraph = new Graph(Graph.ClassGraph);
        Graph methodCallGraph = new Graph(Graph.MethodGraph);
        // 类、方法级别的 Dot 文件
        Dot classDot = new Dot("class");
        Dot methodDot = new Dot("method");


        // 遍历调用图
        for (CGNode node : cg) {
            // 过滤掉无用节点
            if (isUseless(node)) {
                continue;
            }

            // 记录类名、方法签、方法名
            String className = node.getMethod().getDeclaringClass().getName().toString();
            String methodSignature = node.getMethod().getSignature();
            String methodName = className + " " + methodSignature;

            // 判断是否为测试方法
            for (Annotation annotation : node.getMethod().getAnnotations()) {
                if (annotation.getType().getName().toString().equals("Lorg/junit/Test")) {
                    Main.testMethods.add(methodName);
                }
            }

            // 得到该节点的所有前驱节点，即调用该方法节点的所有方法
            Iterator<CGNode> iterator = cg.getPredNodes(node);
            while (iterator.hasNext()) {
                CGNode predNode = iterator.next();
                if (!isUseless(predNode)){
                    String predClassName = predNode.getMethod().getDeclaringClass().getName().toString();
                    String predMethodSignature = predNode.getMethod().getSignature();
                    String predMethodName = predClassName + " " + predMethodSignature;

                    // 记录方法调用关系
                    classCallGraph.createEdge(className, predClassName);
                    methodCallGraph.createEdge(methodName, predMethodName);
                    classDot.addEdge(className, predClassName);
                    methodDot.addEdge(methodSignature, predMethodSignature);
                }
            }
        }

        classDot.saveDotFile("class-cfa.dot");
        methodDot.saveDotFile("method-cfa.dot");

        ArrayList<Graph> res = new ArrayList<>();
        res.add(classCallGraph);
        res.add(methodCallGraph);
        return res;
    }

    private boolean isUseless(CGNode node) {
        return !(node.getMethod() instanceof ShrikeBTMethod) || !node.getMethod().getDeclaringClass().getClassLoader().getReference().equals(ClassLoaderReference.Application);
    }
}
