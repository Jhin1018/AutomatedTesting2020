import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// 用于生成 dot 文件
public class Dot {
    String dotName;
    Set<String> content = new HashSet<>();

    public Dot(String dotName) {
        this.dotName = dotName;
    }

    // 添加一条边
    public void addEdge(String caller, String callee) {
        content.add("\t\"" + caller + "\" -> \"" + callee + "\";");
    }

    // 保存文件
    public void saveDotFile(String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write("digraph " + dotName + " {\n" + String.join("\n", content) + "\n}\n");
        writer.flush();
        writer.close();
    }
}
