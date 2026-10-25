import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BenchmarkRunner {
    public static void main(String[] args) {
        int[] sizes = {1000, 5000, 20000};
        int[] edgeCounts = {3000, 20000, 80000};
        Random rnd = new Random(42);

        System.out.println("Warmup...");
        for (int iter = 0; iter < 10; iter++) {
            runTrial(100, 300, rnd, false);
        }

        System.out.println("Starting Benchmark...");
        for (int s = 0; s < sizes.length; s++) {
            int n = sizes[s];
            int m = edgeCounts[s];
            System.out.println("Size: n = " + n + ", m = " + m);
            long[] kTimes = new long[5];
            long[] pTimes = new long[5];
            for (int t = 0; t < 5; t++) {
                long[][] res = runTrial(n, m, new Random(100 + t * 7), true);
                kTimes[t] = res[0][0];
                pTimes[t] = res[1][0];
                System.out.printf("  Trial %d: Kruskal = %.3f ms, Prim = %.3f ms%n",
                        t + 1, kTimes[t] / 1e6, pTimes[t] / 1e6);
            }
            Arrays.sort(kTimes);
            Arrays.sort(pTimes);
            System.out.printf("  Median Kruskal: %.3f ms%n", kTimes[2] / 1e6);
            System.out.printf("  Median Prim:    %.3f ms%n", pTimes[2] / 1e6);
        }
    }

    private static long[][] runTrial(int n, int m, Random rnd, boolean verify) {
        List<List<Long>> edges = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            edges.add(List.of(
                    (long) rnd.nextInt(n),
                    (long) rnd.nextInt(n),
                    (long) (rnd.nextInt(200000) - 100000)
            ));
        }

        Map<String, Object> reqK = new HashMap<>();
        reqK.put("task", "kruskal");
        reqK.put("n", (long) n);
        reqK.put("edges", edges);

        Map<String, Object> reqP = new HashMap<>();
        reqP.put("task", "prim");
        reqP.put("n", (long) n);
        reqP.put("edges", edges);

        long t0 = System.nanoTime();
        Map<String, Object> resK = Algorithms.kruskal(reqK);
        long t1 = System.nanoTime();

        long t2 = System.nanoTime();
        Map<String, Object> resP = Algorithms.prim(reqP);
        long t3 = System.nanoTime();

        if (verify) {
            long costK = (long) resK.get("cost");
            long costP = (long) resP.get("cost");
            int compK = (int) resK.get("components");
            int compP = (int) resP.get("components");
            if (costK != costP || compK != compP) {
                throw new IllegalStateException("Mismatch in MST cost or components!");
            }
        }

        return new long[][]{{t1 - t0}, {t3 - t2}};
    }
}
