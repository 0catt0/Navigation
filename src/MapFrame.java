import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Map에 컨트롤 패널 넣었다가 ControlPanel과 MapFrame패널로 임시로 분리

public class MapFrame extends JFrame {
    private final MapPanel mapPanel;

    public MapFrame(List<Node> nodes, List<Edge> edges, List<Mudang> mudang) {
        setTitle("Campus Map with Highlighted Area");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 지도 패널 추가
        mapPanel = new MapPanel(nodes, edges, mudang, new ArrayList<>());
        add(mapPanel, BorderLayout.CENTER);

        // 컨트롤 패널 추가
        ControlPanel controlPanel = new ControlPanel(nodes, mapPanel, edges, mudang);
        add(controlPanel, BorderLayout.SOUTH);
    }

}
