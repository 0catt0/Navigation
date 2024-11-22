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
    private List<Object> shortestPath;

    public MapPanel(List<Node> nodes, List<Edge> edges, List<Mudang> mudangs, List<Object> shortestPath) {
        this.nodes = nodes;
        this.edges = edges;
        this.mudangs = mudangs;
        this.shortestPath = new ArrayList<>();
    }
    // 최단 경로 설정 메서드
    public void setShortestPath(List<Object> shortestPath) {
        this.shortestPath = shortestPath;
        repaint(); // 화면 갱신
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

        // 무당 경로 빨간색 표시
        g2.setColor(new Color(220, 20, 60));
        g2.setStroke(new BasicStroke(3));
        for (Mudang mudang : mudangs) {
            g2.drawLine(mudang.from.x, mudang.from.y, mudang.to.x, mudang.to.y);
        }
        // 최단 경로 표시 (굵은 파란색 선)
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(3));
        if (shortestPath != null) {
            for (Object obj : shortestPath) {
                if (obj instanceof Edge) {
                    Edge edge = (Edge) obj;
                    g2.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
                } else if (obj instanceof Mudang) {
                    Mudang mudang = (Mudang) obj;
                    g2.drawLine(mudang.from.x, mudang.from.y, mudang.to.x, mudang.to.y);
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