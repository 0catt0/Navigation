import java.util.*;

public class Dijkstra {

    public static List<Object> findShortestPath(Node start, Node end, List<Edge> edges, List<Mudang> mudangs) {
        HashMap<Node, Integer> distances = new HashMap<>();
        HashMap<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Edge edge : edges) {
            distances.put(edge.getStart(), Integer.MAX_VALUE);
            distances.put(edge.getEnd(), Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        priorityQueue.add(start);

        Set<Node> visited = new HashSet<>();

        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            for (Edge edge : edges) {
                if (edge.getStart().equals(current) || edge.getEnd().equals(current)) {
                    Node neighbor = edge.getStart().equals(current) ? edge.getEnd() : edge.getStart();
                    if (visited.contains(neighbor)) continue;

                    int newDistance = distances.get(current) + edge.getWeight();
                    if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, newDistance);
                        previousNodes.put(neighbor, current);
                        priorityQueue.add(neighbor);
                    }
                }
            }

            for (Mudang mudang : mudangs) {
                if (mudang.getStart().equals(current)) {
                    Node neighbor = mudang.getEnd();
                    if (visited.contains(neighbor)) continue;

                    int newDistance = distances.get(current) + mudang.getWeight();
                    if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, newDistance);
                        previousNodes.put(neighbor, current);
                        priorityQueue.add(neighbor);
                    }
                }
            }
        }

        List<Object> path = new ArrayList<>();
        Node step = end;

        if (!previousNodes.containsKey(end)) {
            System.out.println("경로가 존재하지 않습니다.");
            return Collections.emptyList();
        }

        while (previousNodes.containsKey(step)) {
            Node previous = previousNodes.get(step);

            for (Edge edge : edges) {
                if ((edge.getStart().equals(previous) && edge.getEnd().equals(step)) ||
                        (edge.getEnd().equals(previous) && edge.getStart().equals(step))) {
                    path.add(edge);
                    break;
                }
            }

            for (Mudang mudang : mudangs) {
                if (mudang.getStart().equals(previous) && mudang.getEnd().equals(step)) {
                    path.add(mudang);
                    break;
                }
            }

            step = previous;
        }

        Collections.reverse(path);
        return path;
    }

}
