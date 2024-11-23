import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class MapPanel extends JPanel {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final List<Mudang> mudangs;
    private List<PathSegment> shortestPath;
    
    // 강조할 노드를 저장
    private List<Node> highlightedNodes;

    // Mudang 경로 포함 여부
    private boolean includeMudangPaths = false;
    
    public MapPanel(List<Node> nodes, List<Edge> edges, List<Mudang> mudangs, List<Object> shortestPath) {
        this.nodes = nodes;
        this.edges = edges;
        this.mudangs = mudangs;
        this.shortestPath = new ArrayList<>();
        this.highlightedNodes = new ArrayList<>();
    }
    // 최단 경로 설정 메서드
    public void setShortestPath(List<PathSegment> path) {
        this.shortestPath = path;
        repaint(); // 화면 갱신
    }
    
    // 강조할 노드를 설정
    public void setHighlightedNodes(List<Node> highlightedNodes) {
        this.highlightedNodes = highlightedNodes;
        repaint(); // 화면 갱신
    }
    
    //토글 기능 설정
    public void setIncludeMudangPaths(boolean includeMudangPaths) {
        this.includeMudangPaths = includeMudangPaths;
        repaint(); // 화면 갱신
    }
    
    public void findAndDisplayShortestPath(Node startNode, Node endNode) {
        // Edge와 Mudang 경로를 결합하여 사용할 엣지 리스트 결정
        List<Edge> activeEdges = new ArrayList<>(edges);
        if (includeMudangPaths) {
            for (Mudang mudang : mudangs) {
                activeEdges.add(new Edge(mudang.from, mudang.to, mudang.cost));
            }
        }

        // Dijkstra 알고리즘 실행
        List<PathSegment> path = Dijkstra.findShortestPath(startNode, endNode, activeEdges, new ArrayList<>());
        setShortestPath(path);
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 배경색 그라데이션 추가
        GradientPaint gradient = new GradientPaint(0, 0, Color.LIGHT_GRAY, getWidth(), getHeight(), Color.WHITE);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 학교 영역을 파란색으로 칠하기
        g2.setColor(new Color(173, 216, 230, 150)); // 반투명한 파란색
        int[] xPoints = {30, 40, 400, 470, 480, 700, 880, 870, 410, 150}; // X 좌표
        int[] yPoints = {80, 300, 250, 500, 690, 750, 590, 450, 150, 80}; // Y 좌표
        g2.fillPolygon(xPoints, yPoints, xPoints.length);

        // 앤티앨리어싱 설정
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 무당이 경로 그리기 (초록색, 무당이 경로 토글 상태일때)
        if (includeMudangPaths) {
            g2.setColor(new Color(34, 139, 34)); // 초록색
            for (Mudang mudang : mudangs) {
                g2.drawLine(mudang.from.x, mudang.from.y, mudang.to.x, mudang.to.y);
            }
        }
        
        //최단경로 그리기 (무당이 보도 구분)
        if (shortestPath != null) {
            for (Object obj : shortestPath) {
                if (obj instanceof PathSegment) {
                    PathSegment segment = (PathSegment) obj;
                    if (segment.isMudang) {
                        g2.setColor(Color.RED); // 무당 경로
                        g2.setStroke(new BasicStroke(4));
                    } else {
                        g2.setColor(Color.BLUE); // 보도 경로
                        g2.setStroke(new BasicStroke(3));
                    }
                    g2.drawLine(segment.from.x, segment.from.y, segment.to.x, segment.to.y);
                }
            }
        }
        // 엣지 그리기 (곡선)
        g2.setColor(new Color(70, 130, 180)); // 파란색 계열
        g2.setStroke(new BasicStroke(2)); // 선 두께
        for (Edge edge : edges) {
            double ctrlX1 = (edge.from.x + edge.to.x) / 2.0 + 30; // 첫 번째 제어점
            double ctrlY1 = (edge.from.y + edge.to.y) / 2.0 - 30;
            double ctrlX2 = (edge.from.x + edge.to.x) / 2.0 - 30; // 두 번째 제어점
            double ctrlY2 = (edge.from.y + edge.to.y) / 2.0 + 30;

            CubicCurve2D.Double curve = new CubicCurve2D.Double();
            curve.setCurve(
                    edge.from.x, edge.from.y, // 시작점
                    ctrlX1, ctrlY1,           // 첫 번째 제어점
                    ctrlX2, ctrlY2,           // 두 번째 제어점
                    edge.to.x, edge.to.y      // 끝점
            );
            g2.draw(curve);

//            // 가중치 표시 - 예찬
//
//            double costPointX = (edge.from.x + edge.to.x) / 2.0;
//            double costPointY = (edge.from.x + edge.to.x) / 2.0;
//
//            g2.setColor(Color.BLACK);
//            g2.drawString(edge.toString(), (int) costPointX, (int) costPointY);
//            // 여기까지

        }

        // 노드 그리기
        for (Node node : nodes) {
            // 노드 원
            g2.setColor(new Color(220, 20, 60)); // 붉은색 계열
            
            // 강조된 노드 색상 변경
            if (highlightedNodes.contains(node)) {
                g2.setColor(Color.YELLOW); // 강조 표시
            }
            g2.fillOval(node.x - 15, node.y - 15, 30, 30);

            // 노드 테두리
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(node.x - 15, node.y - 15, 30, 30);

            // 노드 이름 표시
            g2.setColor(Color.BLACK);
            g2.drawString(node.name, node.x - 20, node.y - 20);
        }
        
    }
}