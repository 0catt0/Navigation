import java.util.ArrayList;
import java.util.List;

public class GraphManager {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final List<Mudang> mudang;

    public GraphManager() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.mudang = new ArrayList<>();
    }

    // 초기 데이터 설정
    public void initializeData() {
        nodes.add(new Node("정문", 118, 1976));  // 0
        nodes.add(new Node("비타", 77, 2224));  // 1
        nodes.add(new Node("공대2", 423, 2339));  // 2
        nodes.add(new Node("반단대", 91, 1840));  // 3
        nodes.add(new Node("글센", 76, 1553));  // 4
        nodes.add(new Node("전정도", 431, 1933));  // 5
        nodes.add(new Node("예체대", 554, 1536));  // 6
        nodes.add(new Node("가천관", 685, 2021));  // 7
        nodes.add(new Node("산협", 622, 2301));  // 8
        nodes.add(new Node("교대", 1176, 1564));  // 9
        nodes.add(new Node("중도", 1496, 1376));  // 10
        nodes.add(new Node("학생회관", 1816, 1137));  // 11
        nodes.add(new Node("에공", 1744, 517));  // 12
        nodes.add(new Node("3긱", 1584, 389));  // 13
        nodes.add(new Node("1,2긱", 1915, 294));  // 14
        nodes.add(new Node("운동장", 1970, 500));  // 15
        
//        nodes.add(new Node("정문", 700, 720));  // 0
//        nodes.add(new Node("비타", 780, 670));  // 1
//        nodes.add(new Node("공대2", 850, 590));  // 2
//        nodes.add(new Node("반단대", 620, 670));  // 3
//        nodes.add(new Node("글센", 550, 670));  // 4
//        nodes.add(new Node("전정도", 660, 590));  // 5
//        nodes.add(new Node("예체대", 520, 500));  // 6
//        nodes.add(new Node("가천관", 680, 450));  // 7
//        nodes.add(new Node("산협", 800, 470));  // 8
//        nodes.add(new Node("교대", 550, 280));  // 9
//        nodes.add(new Node("중도", 470, 220));  // 10
//        nodes.add(new Node("학생회관", 400, 180));  // 11
//        nodes.add(new Node("에공", 190, 200));  // 12
//        nodes.add(new Node("3긱", 100, 250));  // 13
//        nodes.add(new Node("1,2긱", 50, 140));  // 14
//        nodes.add(new Node("운동장", 150, 100));  // 15

        // 엣지 생성
        edges.add(new Edge(nodes.get(0), nodes.get(1), 1));
        edges.add(new Edge(nodes.get(0), nodes.get(3), 1));
        edges.add(new Edge(nodes.get(0), nodes.get(5), 3));
        edges.add(new Edge(nodes.get(1), nodes.get(2), 1));
        edges.add(new Edge(nodes.get(1), nodes.get(5), 3));
        edges.add(new Edge(nodes.get(2), nodes.get(8), 1));
        edges.add(new Edge(nodes.get(3), nodes.get(4), 1));
        edges.add(new Edge(nodes.get(3), nodes.get(5), 1));
        edges.add(new Edge(nodes.get(4), nodes.get(5), 2));
        edges.add(new Edge(nodes.get(4), nodes.get(6), 4));
        edges.add(new Edge(nodes.get(4), nodes.get(7), 5));
        edges.add(new Edge(nodes.get(5), nodes.get(6), 2));
        edges.add(new Edge(nodes.get(5), nodes.get(7), 1));
        edges.add(new Edge(nodes.get(6), nodes.get(7), 2));
        edges.add(new Edge(nodes.get(6), nodes.get(9), 5));
        edges.add(new Edge(nodes.get(7), nodes.get(8), 1));
        edges.add(new Edge(nodes.get(7), nodes.get(9), 5));
        edges.add(new Edge(nodes.get(9), nodes.get(10), 3));
        edges.add(new Edge(nodes.get(10), nodes.get(11), 2));
        edges.add(new Edge(nodes.get(11), nodes.get(12), 5));
        edges.add(new Edge(nodes.get(11), nodes.get(15), 3));
        edges.add(new Edge(nodes.get(12), nodes.get(13), 1));
        edges.add(new Edge(nodes.get(12), nodes.get(14), 2));
        edges.add(new Edge(nodes.get(12), nodes.get(15), 1));
        edges.add(new Edge(nodes.get(13), nodes.get(14), 1));
        edges.add(new Edge(nodes.get(14), nodes.get(15), 1));


        // 무당 경로 생성
        mudang.add(new Mudang(nodes.get(0), nodes.get(3), 2));
        mudang.add(new Mudang(nodes.get(3), nodes.get(9), 1));
        mudang.add(new Mudang(nodes.get(9), nodes.get(10), 1));
        mudang.add(new Mudang(nodes.get(10), nodes.get(11), 1));
        mudang.add(new Mudang(nodes.get(11), nodes.get(15), 1));
    }

    // 노드 추가
    public void addNode(String name, int x, int y) {
        nodes.add(new Node(name, x, y));
    }

    // 엣지 추가
    public void addEdge(String fromNodeName, String toNodeName, int cost) {
        Node fromNode = findNodeByName(fromNodeName);
        Node toNode = findNodeByName(toNodeName);
        if (fromNode != null && toNode != null) {
            edges.add(new Edge(fromNode, toNode, cost));
        }
    }

    // 노드 검색
    public Node findNodeByName(String name) {
        return nodes.stream().filter(node -> node.name.equals(name)).findFirst().orElse(null);
    }

    // 생성자
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Mudang> getMudangs() {
        return mudang;
    }
}
