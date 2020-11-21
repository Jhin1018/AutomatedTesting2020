import java.util.*;

/**
 * 依赖图的定义（包括类依赖图和方法依赖图,一个类两张）
 */
public class Graph extends Object{
    // key：方法名或类名
    // value：调用了key的方法名或类名
    public Map<String, Set<String>> graph = new HashMap<>();
    // 调用者使用set容器保存，可以确保其不会重复
    public int type;
    static final int ClassGraph = 0;
    static final int MethodGraph = 1;

    Graph(int type) {
        this.type = type;
    }

    public void createNode(String name) {
        if (!graph.containsKey(name)) {
            //当节点不存在时才创建
            graph.put(name, new HashSet<>());
        }
    }

    public void createEdge(String callee, String caller) {
        createNode(callee);
        createNode(caller);
        // 先保证调用者和被调用者的节点都在图里
        graph.get(callee).add(caller);
    }

}

