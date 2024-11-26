import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
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
    
    //지도 이미지
    private Image backgroundImage;
    
    //줌 드래그 
    private double zoomLevel = 1.0; // 줌 레벨
    private double offsetX = 0;    // 드래그 시 X축 이동
    private double offsetY = 0;    // 드래그 시 Y축 이동
    private Point lastDragPoint;  // 드래그 시작 지점
    
    // 줌 레벨 제한
    private static final double MIN_ZOOM = 0.6;
    private static final double MAX_ZOOM = 3.0;
    
    private void setInitialPosition(int x, int y) {
        offsetX = x;
        offsetY = y;
    }
    
    public MapPanel(List<Node> nodes, List<Edge> edges, List<Mudang> mudangs, List<Object> shortestPath) {
        this.nodes = nodes;
        this.edges = edges;
        this.mudangs = mudangs;
        this.shortestPath = new ArrayList<>();
        this.highlightedNodes = new ArrayList<>();
        
        // 지도 이미지 로드
        ImageIcon icon = new ImageIcon("navigation-main/map.png"); // 지도 이미지 경로
        backgroundImage = icon.getImage();
        
        setInitialPosition(55, -1510); //창 실행시 뷰포트 위치 설정
        
        
        // 마우스 이벤트 등록
        addMouseWheelListener(new ZoomHandler());
        addMouseListener(new DragStartHandler());
        addMouseMotionListener(new DragHandler());
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
    
    // 줌 이벤트 처리
    private class ZoomHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // 기존 줌 레벨 저장
            double oldZoomLevel = zoomLevel;

            // 줌 레벨 업데이트
            double delta = 0.1; // 줌 변화량
            if (e.getWheelRotation() < 0) {
                zoomLevel = Math.min(zoomLevel + delta, MAX_ZOOM); // 줌 인
            } else {
                zoomLevel = Math.max(zoomLevel - delta, MIN_ZOOM); // 줌 아웃
            }

            // 줌 중심점 조정
            double zoomFactor = zoomLevel / oldZoomLevel;
            Point mousePoint = e.getPoint();
            offsetX = zoomFactor * (offsetX - mousePoint.x) + mousePoint.x;
            offsetY = zoomFactor * (offsetY - mousePoint.y) + mousePoint.y;

            repaint();
        }
    }

    // 드래그 시작 이벤트 처리
    private class DragStartHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            lastDragPoint = e.getPoint(); // 드래그 시작 위치 저장
        }
    }

    // 드래그 이벤트 처리
    private class DragHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (lastDragPoint != null) {
                // 이동 거리 계산
                int dx = e.getX() - lastDragPoint.x;
                int dy = e.getY() - lastDragPoint.y;

                // 이동 거리만큼 오프셋 업데이트
                offsetX += dx;
                offsetY += dy;

                // 현재 드래그 위치 저장
                lastDragPoint = e.getPoint();

                repaint();
            }
        }
    }

    // 줌 초기화 메서드
    public void resetZoomAndPosition() {
        zoomLevel = 1.0;
        offsetX = 0;
        offsetY = 0;
        repaint();
    }

    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        System.out.println("Current Offset: X = " + offsetX + ", Y = " + offsetY);

        // 고화질 렌더링 설정
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // AffineTransform을 사용해 줌 및 드래그 적용
        AffineTransform transform = new AffineTransform();
        transform.translate(offsetX, offsetY);       // 드래그 이동
        transform.scale(zoomLevel, zoomLevel);       // 줌

        
//        // 배경색 그라데이션 추가
//        GradientPaint gradient = new GradientPaint(0, 0, Color.LIGHT_GRAY, getWidth(), getHeight(), Color.WHITE);
//        g2.setPaint(gradient);
//        g2.fillRect(0, 0, getWidth(), getHeight());
//        
        // 변환 적용 후 이미지 그리기
        g2.setTransform(transform);
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, this);
        }
        
//        // 학교 영역을 파란색으로 칠하기
//        g2.setColor(new Color(173, 216, 230, 150)); // 반투명한 파란색
//        int[] xPoints = {30, 40, 400, 470, 480, 700, 880, 870, 410, 150}; // X 좌표
//        int[] yPoints = {80, 300, 250, 500, 690, 750, 590, 450, 150, 80}; // Y 좌표
//        g2.fillPolygon(xPoints, yPoints, xPoints.length);

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