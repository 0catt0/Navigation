public class PathSegment {
    public final Node from;
    public final Node to;
    public final boolean isMudang;

    public PathSegment(Node from, Node to, boolean isMudang) {
        this.from = from;
        this.to = to;
        this.isMudang = isMudang;
    }
}
