import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    public ControlPanel(List<Node> nodes, MapPanel mapPanel, List<Edge> edges, List<Mudang> mudangs) {
        setLayout(new FlowLayout());

        JComboBox<String> startComboBox = new JComboBox<>();
        JComboBox<String> endComboBox = new JComboBox<>();
        JButton findPathButton = new JButton("최단 경로 찾기");
        JToggleButton toggleButton = new JToggleButton("무당이 경로 표시");


        // 노드 이름을 ComboBox에 추가
        for (Node node : nodes) {
            startComboBox.addItem(node.name);
            endComboBox.addItem(node.name);
        }

        // 출발지와 도착지 선택 시 강조 표시
        startComboBox.addActionListener(e -> updateHighlightedNodes(startComboBox, endComboBox, nodes, mapPanel));
        endComboBox.addActionListener(e -> updateHighlightedNodes(startComboBox, endComboBox, nodes, mapPanel));


        // 무당 경로 포함 여부 설정
        toggleButton.addActionListener(e -> {
            boolean includeMudang = toggleButton.isSelected();
            toggleButton.setText(includeMudang ? "무당이 경로 표시" : "무당이 경로 끄기");
            mapPanel.setIncludeMudangPaths(includeMudang); // 토글 여부 MapPanel에 전달
        });
        
       
        // 버튼 동작 정의
        findPathButton.addActionListener(e -> {
            String startName = (String) startComboBox.getSelectedItem();
            String endName = (String) endComboBox.getSelectedItem();

            if (startName != null && endName != null && !startName.equals(endName)) {
                Node startNode = nodes.stream().filter(n -> n.name.equals(startName)).findFirst().orElse(null);
                Node endNode = nodes.stream().filter(n -> n.name.equals(endName)).findFirst().orElse(null);

                if (startNode != null && endNode != null) {
                    // Dijkstra 알고리즘 실행
                    List<PathSegment> shortestPath = Dijkstra.findShortestPath(startNode, endNode, edges, mudangs);
                    if (shortestPath != null && !shortestPath.isEmpty()) {
                        mapPanel.setShortestPath(shortestPath);
                    } else {
                        JOptionPane.showMessageDialog(this, "최단 경로를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "출발지와 목적지를 올바르게 선택하세요.", "오류", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 컴포넌트를 컨트롤 패널에 추가
        add(new JLabel("출발지:"));
        add(startComboBox);
        add(new JLabel("목적지:"));
        add(endComboBox);
        add(findPathButton);
        add(toggleButton); // JToggleButton 추가
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

