### Program Functions and Implementation Details by Class

---

### 1. **GUI Interface**

#### **Implemented in Classes**: `ControlPanel`, `MapPanel`

1. **Selecting Start and End Points (Dropdown Menu)**:
   - **Class**: `ControlPanel`
   - **Role**:
     - Provides `JComboBox` for users to select the start and end points.
     - A "Find Shortest Path" button to trigger pathfinding.
   - **Key Code**:
     ```java
     JComboBox<String> startComboBox = new JComboBox<>();
     JComboBox<String> endComboBox = new JComboBox<>();
     JButton findPathButton = new JButton("Find Shortest Path");

     findPathButton.addActionListener(e -> {
         String startName = (String) startComboBox.getSelectedItem();
         String endName = (String) endComboBox.getSelectedItem();

         Node startNode = nodes.stream().filter(n -> n.name.equals(startName)).findFirst().orElse(null);
         Node endNode = nodes.stream().filter(n -> n.name.equals(endName)).findFirst().orElse(null);

         mapPanel.findAndDisplayShortestPath(startNode, endNode);
     });
     ```

2. **Node Visualization (Circular Display on the Map)**:
   - **Class**: `MapPanel`
   - **Role**:
     - Draws nodes as circles on the map using `Graphics2D`, highlighting the start and end points.
   - **Key Code**:
     ```java
     for (Node node : nodes) {
         if (highlightedNodes.contains(node)) {
             g2.setColor(Color.YELLOW); // Highlight start/end points
         } else {
             g2.setColor(new Color(220, 20, 60)); // Default node color
         }
         g2.fillOval(node.x - 15, node.y - 15, 30, 30);
     }
     ```

3. **Path Visualization (Mudang and Pedestrian Paths)**:
   - **Class**: `MapPanel`
   - **Role**:
     - Visualizes the shortest path, distinguishing Mudang paths (red) and pedestrian paths (blue).
   - **Key Code**:
     ```java
     for (PathSegment segment : shortestPath) {
         g2.setColor(segment.isMudang ? Color.RED : Color.BLUE);
         g2.setStroke(new BasicStroke(segment.isMudang ? 4 : 3));
         g2.drawLine(segment.from.x, segment.from.y, segment.to.x, segment.to.y);
     }
     ```

---

### 2. **Zoom and Drag for Map Navigation**

#### **Implemented in Class**: `MapPanel`

1. **Zoom**:
   - **Role**:
     - Adjusts the `zoomLevel` using mouse wheel events to zoom in or out.
   - **Key Code**:
     ```java
     addMouseWheelListener(e -> {
         double oldZoomLevel = zoomLevel;
         double delta = 0.1;
         zoomLevel = e.getWheelRotation() < 0
             ? Math.min(zoomLevel + delta, MAX_ZOOM)
             : Math.max(zoomLevel - delta, MIN_ZOOM);

         double zoomFactor = zoomLevel / oldZoomLevel;
         offsetX = zoomFactor * (offsetX - e.getPoint().x) + e.getPoint().x;
         offsetY = zoomFactor * (offsetY - e.getPoint().y) + e.getPoint().y;

         repaint();
     });
     ```

2. **Drag**:
   - **Role**:
     - Moves the viewport by updating `offsetX` and `offsetY` based on mouse drag events.
   - **Key Code**:
     ```java
     addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
             int dx = e.getX() - lastDragPoint.x;
             int dy = e.getY() - lastDragPoint.y;
             offsetX += dx;
             offsetY += dy;
             lastDragPoint = e.getPoint();
             repaint();
         }
     });
     ```

---

### 3. **Detailed Path and Distance Information Display**

#### **Implemented in Class**: `ControlPanel`

1. **Summary Information Display**:
   - **Role**:
     - Displays the total distance, number of Mudang segments, and pedestrian segments based on the Dijkstra results.
   - **Key Code**:
     ```java
     JLabel pathSummaryLabel = new JLabel();
     findPathButton.addActionListener(e -> {
         PathInfo pathInfo = PathInfo.findPathInfo(startNode, endNode, edges, mudangs);
         pathSummaryLabel.setText(String.format(
             "Total Distance: %d, Mudang Segments: %d, Pedestrian Segments: %d",
             pathInfo.totalDistance, pathInfo.mudangCount, pathInfo.roadCount
         ));
     });
     ```

2. **Detailed Path Information**:
   - **Role**:
     - Outputs step-by-step path information, including nodes and segment distances.
   - **Key Code**:
     ```java
     String detailedInfo = pathInfo.getDetailedPathInfo(edges, mudangs);
     pathDetailsLabel.setText("<html>" + detailedInfo.replace(" → ", "<br> → ") + "</html>");
     ```

---

### 4. **Map Image Integration**

#### **Implemented in Class**: `MapPanel`

1. **Role**:
   - Loads and displays a map image (`backgroundImage`) as the background.
2. **Key Code**:
   ```java
   ImageIcon icon = new ImageIcon("navigation-main/map.png");
   backgroundImage = icon.getImage();

   g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
   ```

---

### Class Responsibilities Overview

| **Class**          | **Role**                                                                 |
|---------------------|-------------------------------------------------------------------------|
| **`ControlPanel`**  | Manages start/end point selection, pathfinding, and summary/detailed info display. |
| **`MapPanel`**      | Handles map rendering, node/path visualization, zoom, and drag functionality. |
| **`Dijkstra`**      | Implements the shortest pathfinding algorithm.                         |
| **`PathInfo`**      | Analyzes and summarizes path details.                                  |

### Detailed Explanation of All Classes in the Program

---

### **1. `ControlPanel`**

- **Role**: 
  - Manages user input (start/end point selection, toggles).
  - Handles pathfinding triggers and displays summary/detailed path information.

- **Key Functionalities**:
  1. **Dropdown Menus**:
     - Lets users select start and end nodes.
     - `JComboBox` populated with node names.
  2. **Pathfinding Trigger**:
     - `JButton` to trigger shortest path calculation.
  3. **Summary Information**:
     - Displays total distance and counts of Mudang and pedestrian segments.
  4. **Detailed Information**:
     - Shows step-by-step details of the computed path.
  5. **Mudang Path Toggle**:
     - `JToggleButton` to include/exclude Mudang paths in the calculation.

- **Core Code**:
```java
JComboBox<String> startComboBox = new JComboBox<>();
JComboBox<String> endComboBox = new JComboBox<>();
JButton findPathButton = new JButton("Find Shortest Path");
JToggleButton mudangToggle = new JToggleButton("Include Mudang Paths");

findPathButton.addActionListener(e -> {
    String startName = (String) startComboBox.getSelectedItem();
    String endName = (String) endComboBox.getSelectedItem();
    Node startNode = nodes.stream().filter(n -> n.name.equals(startName)).findFirst().orElse(null);
    Node endNode = nodes.stream().filter(n -> n.name.equals(endName)).findFirst().orElse(null);
    mapPanel.findAndDisplayShortestPath(startNode, endNode);
});
```

---

### **2. `MapPanel`**

- **Role**:
  - Handles rendering of the map, nodes, and paths.
  - Provides zoom, drag, and viewport control for map navigation.

- **Key Functionalities**:
  1. **Map Rendering**:
     - Displays the map as a background image.
  2. **Node Visualization**:
     - Highlights selected start and end nodes.
  3. **Path Visualization**:
     - Distinguishes Mudang paths (red) from pedestrian paths (blue).
  4. **Zoom and Drag**:
     - Enables interactive map navigation using mouse events.

- **Core Code**:
```java
// Draw the map background
if (backgroundImage != null) {
    g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
}

// Draw nodes
for (Node node : nodes) {
    if (highlightedNodes.contains(node)) {
        g2.setColor(Color.YELLOW); // Highlighted nodes
    } else {
        g2.setColor(new Color(220, 20, 60)); // Default node color
    }
    g2.fillOval(node.x - 15, node.y - 15, 30, 30);
}

// Draw paths
for (PathSegment segment : shortestPath) {
    g2.setColor(segment.isMudang ? Color.RED : Color.BLUE);
    g2.drawLine(segment.from.x, segment.from.y, segment.to.x, segment.to.y);
}
```

---

### **3. `Dijkstra`**

- **Role**:
  - Implements the Dijkstra algorithm for finding the shortest path.
  - Supports both Mudang and pedestrian paths.

- **Key Functionalities**:
  1. **Pathfinding**:
     - Calculates the shortest path between two nodes.
  2. **Mudang Path Integration**:
     - Differentiates between Mudang paths and pedestrian paths.
  3. **Directional Edge Handling**:
     - Considers the direction of paths.

- **Core Code**:
```java
for (Edge edge : edges) {
    if (edge.from.equals(current)) {
        int newCost = distances.get(current) + edge.cost;
        if (newCost < distances.getOrDefault(edge.to, Integer.MAX_VALUE)) {
            distances.put(edge.to, newCost);
            previousNodes.put(edge.to, current);
            priorityQueue.add(edge.to);
        }
    }
}
```

---

### **4. `PathInfo`**

- **Role**:
  - Analyzes the results of the Dijkstra algorithm.
  - Summarizes total distance and path details.

- **Key Functionalities**:
  1. **Summary Statistics**:
     - Calculates total distance, number of Mudang segments, and pedestrian segments.
  2. **Detailed Information**:
     - Lists nodes and distances for each segment of the path.

- **Core Code**:
```java
int totalDistance = 0;
int mudangCount = 0;
int pedestrianCount = 0;

for (PathSegment segment : path) {
    totalDistance += segment.cost;
    if (segment.isMudang) {
        mudangCount++;
    } else {
        pedestrianCount++;
    }
}
return new PathInfo(totalDistance, mudangCount, pedestrianCount);
```

---

### **5. `Node`**

- **Role**:
  - Represents a node (point) on the map.

- **Key Functionalities**:
  1. **Attributes**:
     - Stores the name and coordinates (`x`, `y`) of the node.
  2. **Equality Check**:
     - Ensures nodes are uniquely identifiable.

- **Core Code**:
```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Node node = (Node) obj;
    return x == node.x && y == node.y && Objects.equals(name, node.name);
}

@Override
public int hashCode() {
    return Objects.hash(name, x, y);
}
```

---

### **6. `Edge`**

- **Role**:
  - Represents a connection (path) between two nodes.

- **Key Functionalities**:
  1. **Attributes**:
     - Stores the start node, end node, cost, and whether it is a Mudang path.
  2. **Bidirectional Edges**:
     - Handles both directions for a path if needed.

- **Core Code**:
```java
class Edge {
    Node from;
    Node to;
    int cost;
    boolean isMudang;

    public Edge(Node from, Node to, int cost, boolean isMudang) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.isMudang = isMudang;
    }
}
```

---

### **7. `Mudang`**

- **Role**:
  - Represents specific Mudang (shuttle) paths, a subset of `Edge`.

- **Key Functionalities**:
  - Extends `Edge` to indicate that the path belongs to a specific type (Mudang).

---

### Summary of Class Responsibilities

| **Class**          | **Role**                                                                 |
|---------------------|-------------------------------------------------------------------------|
| **`ControlPanel`**  | Manages GUI components for user input and displays pathfinding results. |
| **`MapPanel`**      | Handles map rendering, node/path visualization, and interactive navigation. |
| **`Dijkstra`**      | Implements the shortest pathfinding algorithm with Mudang path support. |
| **`PathInfo`**      | Analyzes and summarizes path details based on Dijkstra's output.       |
| **`Node`**          | Represents individual points on the map.                              |
| **`Edge`**          | Represents connections between nodes, with Mudang path differentiation. |
| **`Mudang`**        | Specialized `Edge` for shuttle paths.                                  |

