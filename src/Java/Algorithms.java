import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public final class Algorithms {

    static Map<String, Object> dsu(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] operations = Input.operations(input);

        List<List<Integer>> states = new ArrayList<>(operations.length);
        if (n == 0) {
            for (int i = 0; i < operations.length; i++) {
                states.add(List.of(0, 0));
            }
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("states", states);
            return res;
        }

        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        int components = n;
        int largest = 1;

        for (int[] op : operations) {
            int u = op[0];
            int v = op[1];

            int rootU = find(parent, u);
            int rootV = find(parent, v);

            if (rootU != rootV) {
                if (size[rootU] < size[rootV]) {
                    int tmp = rootU;
                    rootU = rootV;
                    rootV = tmp;
                }
                parent[rootV] = rootU;
                size[rootU] += size[rootV];
                if (size[rootU] > largest) {
                    largest = size[rootU];
                }
                components--;
            }

            states.add(List.of(components, largest));
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("states", states);
        return res;
    }

    private static int find(int[] parent, int i) {
        int root = i;
        while (root != parent[root]) {
            root = parent[root];
        }
        int curr = i;
        while (curr != root) {
            int nxt = parent[curr];
            parent[curr] = root;
            curr = nxt;
        }
        return root;
    }

    static Map<String, Object> kruskal(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        if (n == 0) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("cost", 0L);
            res.put("edge_ids", Collections.emptyList());
            res.put("components", 0);
            return res;
        }

        int m = edges.length;
        Integer[] order = new Integer[m];
        for (int i = 0; i < m; i++) {
            order[i] = i;
        }

        Arrays.sort(order, Comparator.comparingInt(idx -> edges[idx][2]));

        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        long totalCost = 0;
        List<Integer> edgeIds = new ArrayList<>();
        int components = n;

        for (int idx : order) {
            int u = edges[idx][0];
            int v = edges[idx][1];
            int w = edges[idx][2];

            int rootU = find(parent, u);
            int rootV = find(parent, v);

            if (rootU != rootV) {
                if (size[rootU] < size[rootV]) {
                    int tmp = rootU;
                    rootU = rootV;
                    rootV = tmp;
                }
                parent[rootV] = rootU;
                size[rootU] += size[rootV];
                components--;
                totalCost += w;
                edgeIds.add(idx);
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("cost", totalCost);
        res.put("edge_ids", edgeIds);
        res.put("components", components);
        return res;
    }

    static Map<String, Object> prim(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        if (n == 0) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("cost", 0L);
            res.put("edge_ids", Collections.emptyList());
            res.put("components", 0);
            return res;
        }

        List<List<int[]>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0];
            int v = edges[i][1];
            int w = edges[i][2];
            if (u != v) {
                adj.get(u).add(new int[]{v, w, i});
                adj.get(v).add(new int[]{u, w, i});
            }
        }

        boolean[] visited = new boolean[n];
        long totalCost = 0;
        List<Integer> edgeIds = new ArrayList<>();
        int components = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                continue;
            }
            components++;
            visited[i] = true;
            for (int[] edge : adj.get(i)) {
                pq.add(new int[]{edge[0], edge[1], edge[2]});
            }

            while (!pq.isEmpty()) {
                int[] top = pq.poll();
                int to = top[0];
                int weight = top[1];
                int edgeId = top[2];

                if (visited[to]) {
                    continue;
                }
                visited[to] = true;
                totalCost += weight;
                edgeIds.add(edgeId);

                for (int[] nextEdge : adj.get(to)) {
                    if (!visited[nextEdge[0]]) {
                        pq.add(new int[]{nextEdge[0], nextEdge[1], nextEdge[2]});
                    }
                }
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("cost", totalCost);
        res.put("edge_ids", edgeIds);
        res.put("components", components);
        return res;
    }

    static Map<String, Object> scc(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        if (n == 0) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("component", Collections.emptyList());
            res.put("condensation", Collections.emptyList());
            return res;
        }

        int[] head = new int[n];
        Arrays.fill(head, -1);
        int[] to = new int[edges.length];
        int[] next = new int[edges.length];
        for (int i = 0; i < edges.length; i++) {
            to[i] = edges[i][1];
            next[i] = head[edges[i][0]];
            head[edges[i][0]] = i;
        }

        int[] headRev = new int[n];
        Arrays.fill(headRev, -1);
        int[] toRev = new int[edges.length];
        int[] nextRev = new int[edges.length];
        for (int i = 0; i < edges.length; i++) {
            toRev[i] = edges[i][0];
            nextRev[i] = headRev[edges[i][1]];
            headRev[edges[i][1]] = i;
        }

        boolean[] visited = new boolean[n];
        int[] finishOrder = new int[n];
        int orderCount = 0;

        int[] vStack = new int[n];
        int[] edgeStack = new int[n];

        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                continue;
            }
            int ptr = 0;
            vStack[ptr] = i;
            edgeStack[ptr] = head[i];
            visited[i] = true;

            while (ptr >= 0) {
                int curr = vStack[ptr];
                int e = edgeStack[ptr];
                if (e != -1) {
                    edgeStack[ptr] = next[e];
                    int nxt = to[e];
                    if (!visited[nxt]) {
                        visited[nxt] = true;
                        ptr++;
                        vStack[ptr] = nxt;
                        edgeStack[ptr] = head[nxt];
                    }
                } else {
                    finishOrder[orderCount++] = curr;
                    ptr--;
                }
            }
        }

        int[] component = new int[n];
        Arrays.fill(component, -1);
        int sccCount = 0;

        for (int i = n - 1; i >= 0; i--) {
            int startNode = finishOrder[i];
            if (component[startNode] != -1) {
                continue;
            }
            int label = sccCount++;
            int ptr = 0;
            vStack[ptr] = startNode;
            edgeStack[ptr] = headRev[startNode];
            component[startNode] = label;

            while (ptr >= 0) {
                int curr = vStack[ptr];
                int e = edgeStack[ptr];
                if (e != -1) {
                    edgeStack[ptr] = nextRev[e];
                    int nxt = toRev[e];
                    if (component[nxt] == -1) {
                        component[nxt] = label;
                        ptr++;
                        vStack[ptr] = nxt;
                        edgeStack[ptr] = headRev[nxt];
                    }
                } else {
                    ptr--;
                }
            }
        }

        List<Integer> compList = new ArrayList<>(n);
        for (int c : component) {
            compList.add(c);
        }

        Set<Long> seenEdges = new HashSet<>();
        List<List<Integer>> condensation = new ArrayList<>();
        for (int[] e : edges) {
            int uComp = component[e[0]];
            int vComp = component[e[1]];
            if (uComp != vComp) {
                long key = (((long) uComp) << 32) | (vComp & 0xFFFFFFFFL);
                if (seenEdges.add(key)) {
                    condensation.add(List.of(uComp, vComp));
                }
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("component", compList);
        res.put("condensation", condensation);
        return res;
    }

    static Map<String, Object> topo(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        if (n == 0) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("acyclic", true);
            res.put("order", Collections.emptyList());
            return res;
        }

        int[] head = new int[n];
        Arrays.fill(head, -1);
        int[] to = new int[edges.length];
        int[] next = new int[edges.length];
        for (int i = 0; i < edges.length; i++) {
            to[i] = edges[i][1];
            next[i] = head[edges[i][0]];
            head[edges[i][0]] = i;
        }

        int[] state = new int[n];
        int[] order = new int[n];
        int orderCount = 0;

        int[] vStack = new int[n];
        int[] edgeStack = new int[n];

        for (int i = 0; i < n; i++) {
            if (state[i] != 0) {
                continue;
            }
            int ptr = 0;
            vStack[ptr] = i;
            edgeStack[ptr] = head[i];
            state[i] = 1;

            while (ptr >= 0) {
                int curr = vStack[ptr];
                int e = edgeStack[ptr];
                if (e != -1) {
                    edgeStack[ptr] = next[e];
                    int nxt = to[e];
                    if (state[nxt] == 1) {
                        Map<String, Object> res = new LinkedHashMap<>();
                        res.put("acyclic", false);
                        res.put("order", Collections.emptyList());
                        return res;
                    } else if (state[nxt] == 0) {
                        state[nxt] = 1;
                        ptr++;
                        vStack[ptr] = nxt;
                        edgeStack[ptr] = head[nxt];
                    }
                } else {
                    state[curr] = 2;
                    order[orderCount++] = curr;
                    ptr--;
                }
            }
        }

        List<Integer> revOrder = new ArrayList<>(n);
        for (int i = n - 1; i >= 0; i--) {
            revOrder.add(order[i]);
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("acyclic", true);
        res.put("order", revOrder);
        return res;
    }

    static Map<String, Object> dag(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int source = Input.source(input);
        int[][] edges = Input.edges(input);

        int[] head = new int[n];
        Arrays.fill(head, -1);
        int[] to = new int[edges.length];
        int[] next = new int[edges.length];
        for (int i = 0; i < edges.length; i++) {
            to[i] = edges[i][1];
            next[i] = head[edges[i][0]];
            head[edges[i][0]] = i;
        }

        int[] state = new int[n];
        int[] topoOrder = new int[n];
        int orderCount = 0;

        int[] vStack = new int[n];
        int[] edgeStack = new int[n];

        for (int i = 0; i < n; i++) {
            if (state[i] != 0) {
                continue;
            }
            int ptr = 0;
            vStack[ptr] = i;
            edgeStack[ptr] = head[i];
            state[i] = 1;

            while (ptr >= 0) {
                int curr = vStack[ptr];
                int e = edgeStack[ptr];
                if (e != -1) {
                    edgeStack[ptr] = next[e];
                    int nxt = to[e];
                    if (state[nxt] == 1) {
                        Map<String, Object> res = new LinkedHashMap<>();
                        res.put("acyclic", false);
                        res.put("distance", Collections.emptyList());
                        res.put("parent_edge", Collections.emptyList());
                        return res;
                    } else if (state[nxt] == 0) {
                        state[nxt] = 1;
                        ptr++;
                        vStack[ptr] = nxt;
                        edgeStack[ptr] = head[nxt];
                    }
                } else {
                    state[curr] = 2;
                    topoOrder[orderCount++] = curr;
                    ptr--;
                }
            }
        }

        Long[] distance = new Long[n];
        int[] parentEdge = new int[n];
        Arrays.fill(parentEdge, -1);

        distance[source] = 0L;

        for (int i = orderCount - 1; i >= 0; i--) {
            int u = topoOrder[i];
            if (distance[u] == null) {
                continue;
            }
            long d = distance[u];
            int e = head[u];
            while (e != -1) {
                int v = to[e];
                int w = edges[e][2];
                long newDist = d + w;
                if (distance[v] == null || newDist < distance[v]) {
                    distance[v] = newDist;
                    parentEdge[v] = e;
                }
                e = next[e];
            }
        }

        List<Object> distList = new ArrayList<>(n);
        distList.addAll(Arrays.asList(distance));

        List<Integer> parentList = new ArrayList<>(n);
        for (int p : parentEdge) {
            parentList.add(p);
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("acyclic", true);
        res.put("distance", distList);
        res.put("parent_edge", parentList);
        return res;
    }

    public static List<Integer> reconstructPath(int source, int target, int[] parentEdge, int[][] edges) {
        if (source == target) {
            List<Integer> path = new ArrayList<>();
            path.add(source);
            return path;
        }
        if (target < 0 || target >= parentEdge.length || parentEdge[target] == -1) {
            return Collections.emptyList();
        }

        ArrayDeque<Integer> path = new ArrayDeque<>();
        int curr = target;
        while (curr != source) {
            path.addFirst(curr);
            int e = parentEdge[curr];
            if (e == -1) {
                return Collections.emptyList();
            }
            curr = edges[e][0];
        }
        path.addFirst(source);
        return new ArrayList<>(path);
    }
}
