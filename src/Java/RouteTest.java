import java.util.List;

public class RouteTest {
    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 3},
            {0, 2, 8},
            {1, 2, -7},
            {2, 3, 4},
            {0, 4, 2},
            {4, 2, -1}
        };
        int[] parentEdge = {-1, 0, 2, 3, -1, -1};

        List<Integer> pathTo3 = Algorithms.reconstructPath(0, 3, parentEdge, edges);
        System.out.println("Path to reachable (3): " + pathTo3);

        List<Integer> pathToSource = Algorithms.reconstructPath(0, 0, parentEdge, edges);
        System.out.println("Path to source (0): " + pathToSource);

        List<Integer> pathToUnreachable = Algorithms.reconstructPath(0, 5, parentEdge, edges);
        System.out.println("Path to unreachable (5): " + pathToUnreachable);

        int[][] tiedEdges = {
            {0, 1, 5},
            {0, 1, 5},
            {1, 2, 2}
        };
        int[] tiedParentEdge = {-1, 1, 2};
        List<Integer> pathTo2 = Algorithms.reconstructPath(0, 2, tiedParentEdge, tiedEdges);
        System.out.println("Path with tied/parallel edges (2): " + pathTo2);
    }
}
