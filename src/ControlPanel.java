import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    public ControlPanel(List<Node> nodes, MapPanel mapPanel, List<Edge> edges, List<Mudang> mudangs) {
        setLayout(new BorderLayout()); // BorderLayout으로 변경

        // 출발지, 도착지 선택 및 버튼 (하단 영역)
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JComboBox<String> startComboBox = new JComboBox<>();
        JComboBox<String> endComboBox = new JComboBox<>();
        JButton findPathButton = new JButton("최단 경로 찾기");
        JToggleButton toggleButton = new JToggleButton("무당이 경로 표시");

        for (Node node : nodes) {
            startComboBox.addItem(node.name);
            endComboBox.addItem(node.name);
        }
        
        // 출발지와 도착지 선택 시 강조 표시
        startComboBox.addActionListener(e -> updateHighlightedNodes(startComboBox, endComboBox, nodes, mapPanel));
        endComboBox.addActionListener(e -> updateHighlightedNodes(startComboBox, endComboBox, nodes, mapPanel));

        bottomPanel.add(new JLabel("출발지:"));
        bottomPanel.add(startComboBox);
        bottomPanel.add(new JLabel("도착지:"));
        bottomPanel.add(endComboBox);
        bottomPanel.add(findPathButton);
        bottomPanel.add(toggleButton);

        // 경로 요약 정보와 세부 정보 (오른쪽 영역)
        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel pathSummaryLabel = new JLabel("경로 요약 정보:");
        JLabel pathDetailsLabel = new JLabel("<html>경로 세부 정보 없음</html>");
        pathDetailsLabel.setVerticalAlignment(SwingConstants.TOP); // 텍스트 상단 정렬

        rightPanel.add(pathSummaryLabel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(pathDetailsLabel), BorderLayout.CENTER);

        // 무당 경로 토글 버튼 동작 정의
        toggleButton.addActionListener(e -> {
            boolean includeMudang = toggleButton.isSelected();
            toggleButton.setText(includeMudang ? "무당이 경로 끄기" : "무당이 경로 표시");
            mapPanel.setIncludeMudangPaths(includeMudang); // MapPanel에 상태 전달
        });

        // 최단 경로 버튼 동작 정의
        findPathButton.addActionListener(e -> {
            String startName = (String) startComboBox.getSelectedItem();
            String endName = (String) endComboBox.getSelectedItem();

            if (startName != null && endName != null && !startName.equals(endName)) {
                Node startNode = nodes.stream().filter(n -> n.name.equals(startName)).findFirst().orElse(null);
                Node endNode = nodes.stream().filter(n -> n.name.equals(endName)).findFirst().orElse(null);

                if (startNode != null && endNode != null) {
                    // Dijkstra 실행 및 정보 업데이트
                    List<PathSegment> shortestPath = Dijkstra.findShortestPath(startNode, endNode, edges, mudangs);
                    if (shortestPath != null && !shortestPath.isEmpty()) {
                        mapPanel.setShortestPath(shortestPath);

                        PathInfo pathInfo = PathInfo.findPathInfo(startNode, endNode, edges, mudangs);
                        pathSummaryLabel.setText(String.format(
                            "출발지: %s, 도착지: %s | 총 거리: %d, 무당 구간: %d, 보도 구간: %d",
                            startName,
                            endName,
                            pathInfo.totalDistance,
                            pathInfo.mudangCount,
                            pathInfo.roadCount
                        ));

                        String detailedInfo = pathInfo.getDetailedPathInfo(edges, mudangs);
                        pathDetailsLabel.setText("<html>" + detailedInfo.replace(" → ", "<br> → ") + "</html>");
                    } else {
                        JOptionPane.showMessageDialog(this, "최단 경로를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "출발지와 목적지를 올바르게 선택하세요.", "오류", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 전체 레이아웃에 패널 추가
        add(bottomPanel, BorderLayout.SOUTH);  // 하단 버튼 및 선택 패널
        add(rightPanel, BorderLayout.EAST);    // 오른쪽 정보 패널
    }
    
    //선택된 노드 강조
    private void updateHighlightedNodes(JComboBox<String> startComboBox, JComboBox<String> endComboBox, List<Node> nodes, MapPanel mapPanel) {
        String startName = (String) startComboBox.getSelectedItem();
        String endName = (String) endComboBox.getSelectedItem();
        Node startNode = nodes.stream().filter(n -> n.name.equals(startName)).findFirst().orElse(null);
        Node endNode = nodes.stream().filter(n -> n.name.equals(endName)).findFirst().orElse(null);
        List<Node> highlightedNodes = new ArrayList<>();
        if (startNode != null) highlightedNodes.add(startNode);
        if (endNode != null) highlightedNodes.add(endNode);
        mapPanel.setHighlightedNodes(highlightedNodes);
    }
    
}
