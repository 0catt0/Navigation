import javax.swing.SwingUtilities;


//실행만 담당하는 클래스

public class Main {
    public static void main(String[] args) {
        // 그래프 관리 클래스 생성
        GraphManager graphManager = new GraphManager();
        graphManager.initializeData();

        //엣지 노드 동적 추가 기능
//        graphManager.addNode("새로운 노드", 600, 600);
//        graphManager.addEdge("정문", "새로운 노드", 3);

        // GUI 실행
        SwingUtilities.invokeLater(() -> {
            new MapFrame(
                graphManager.getNodes(),
                graphManager.getEdges(),
                graphManager.getMudangs()
            ).setVisible(true);
        });
    }
}
