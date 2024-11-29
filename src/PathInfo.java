import java.util.List;

public class PathInfo {
    public final Node start;
    public final Node end;
    public final List<PathSegment> path;
    public final int totalDistance;
    public final int mudangCount;
    public final int roadCount;

    public PathInfo(Node start, Node end, List<PathSegment> path, int totalDistance, int mudangCount, int roadCount) {
        this.start = start;
        this.end = end;
        this.path = path;
        this.totalDistance = totalDistance;
        this.mudangCount = mudangCount;
        this.roadCount = roadCount;
    }
    
    public static PathInfo findPathInfo(Node start, Node end, List<Edge> edges, List<Mudang> mudangs) {
        List<PathSegment> path = Dijkstra.findShortestPath(start, end, edges, mudangs);

        int totalDistance = 0;
        int mudangCount = 0;
        int roadCount = 0;

        for (PathSegment segment : path) {
            totalDistance += segment.isMudang
                ? mudangs.stream().filter(m -> m.getStart().equals(segment.from) && m.getEnd().equals(segment.to)).findFirst().get().getWeight()
                : edges.stream().filter(e -> e.getStart().equals(segment.from) && e.getEnd().equals(segment.to)).findFirst().get().getWeight();

            if (segment.isMudang) mudangCount++;
            else roadCount++;
        }

        return new PathInfo(start, end, path, totalDistance, mudangCount, roadCount);
    }
    
    // 경로 세부 정보를 문자열로 반환
    public String getDetailedPathInfo(List<Edge> edges, List<Mudang> mudangs) {
        StringBuilder details = new StringBuilder();
        details.append("경로: ");
        Node previous = null;

        for (PathSegment segment : path) {
            if (previous != null) {
                details.append(" → ");
            }
            details.append(segment.from.name);

            // 구간 정보 추가
            int weight = segment.isMudang
                ? mudangs.stream().filter(m -> m.getStart().equals(segment.from) && m.getEnd().equals(segment.to)).findFirst().get().getWeight()
                : edges.stream().filter(e -> e.getStart().equals(segment.from) && e.getEnd().equals(segment.to)).findFirst().get().getWeight();

            details.append(String.format("(%s: %d)", segment.isMudang ? "무당" : "보도", weight));
            previous = segment.to;
        }

        // 마지막 노드 추가
        if (!path.isEmpty()) {
            details.append(" → ").append(path.get(path.size() - 1).to.name);
        }

        return details.toString();
    }
}
