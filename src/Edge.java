public class Edge {
    public final Node from;
    public final Node to;
    public final int cost;

    public Edge(Node from, Node to, int cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }

    // 시작 노드를 반환
    public Node getStart() {
        return from;
    }

    // 끝 노드를 반환
    public Node getEnd() {
        return to;
    }

    // 가중치를 반환
    public int getWeight() {
        return cost;
    }
}
