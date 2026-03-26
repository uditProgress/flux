import java.util.HashMap;
import java.util.Map;

/**
 * Standalone benchmark comparing default-capacity HashMap vs pre-sized HashMap
 * and inline-put vs temporary-map-putAll for the OptionsUtil hot path.
 *
 * Run:  javac benchmark/HashMapBenchmark.java && java -cp benchmark HashMapBenchmark
 */
public class HashMapBenchmark {

    private static final int ITERATIONS = 2_000_000;
    private static final int WARMUP     =   500_000;

    // Simulated 20-pair call (same scale as WriteDocumentParams.makeOptions)
    private static final String[] KEYS_AND_VALUES = {
        "k1",  "v1",  "k2",  "v2",  "k3",  "v3",  "k4",  "v4",
        "k5",  "v5",  "k6",  "v6",  "k7",  "v7",  "k8",  "v8",
        "k9",  "v9",  "k10", "v10", "k11", "v11", "k12", "v12",
        "k13", "v13", "k14", "v14", "k15", "v15", "k16", "v16",
        "k17", "v17", "k18", "v18", "k19", null,   "k20", ""
    };

    // ---- BEFORE (original code) ----

    static Map<String, String> makeOptions_before(String... kv) {
        Map<String, String> m = new HashMap<>();            // default cap 16
        for (int i = 0; i < kv.length; i += 2) {
            String v = kv[i + 1];
            if (v != null && !v.isEmpty()) m.put(kv[i], v);
        }
        return m;
    }

    static Map<String, String> addOptions_before(Map<String, String> m, String... kv) {
        m.putAll(makeOptions_before(kv));                   // temp map + putAll
        return m;
    }

    // ---- AFTER (optimized code) ----

    static Map<String, String> makeOptions_after(String... kv) {
        int pairs = kv.length / 2;
        Map<String, String> m = new HashMap<>((int)(pairs / 0.75) + 1);
        for (int i = 0; i < kv.length; i += 2) {
            String v = kv[i + 1];
            if (v != null && !v.isEmpty()) m.put(kv[i], v);
        }
        return m;
    }

    static Map<String, String> addOptions_after(Map<String, String> m, String... kv) {
        for (int i = 0; i < kv.length; i += 2) {           // inline puts
            String v = kv[i + 1];
            if (v != null && !v.isEmpty()) m.put(kv[i], v);
        }
        return m;
    }

    // ---- Benchmark harness ----

    public static void main(String[] args) {
        System.out.println("=== OptionsUtil micro-benchmark ===");
        System.out.printf("Iterations: %,d  |  Warmup: %,d%n%n", ITERATIONS, WARMUP);

        // ---------- makeOptions ----------
        // Warmup
        for (int i = 0; i < WARMUP; i++) { makeOptions_before(KEYS_AND_VALUES); }
        long t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) { makeOptions_before(KEYS_AND_VALUES); }
        long beforeMakeNs = System.nanoTime() - t0;

        for (int i = 0; i < WARMUP; i++) { makeOptions_after(KEYS_AND_VALUES); }
        t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) { makeOptions_after(KEYS_AND_VALUES); }
        long afterMakeNs = System.nanoTime() - t0;

        // ---------- addOptions ----------
        for (int i = 0; i < WARMUP; i++) { addOptions_before(new HashMap<>(), KEYS_AND_VALUES); }
        t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) { addOptions_before(new HashMap<>(), KEYS_AND_VALUES); }
        long beforeAddNs = System.nanoTime() - t0;

        for (int i = 0; i < WARMUP; i++) { addOptions_after(new HashMap<>(), KEYS_AND_VALUES); }
        t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) { addOptions_after(new HashMap<>(), KEYS_AND_VALUES); }
        long afterAddNs = System.nanoTime() - t0;

        // ---------- Results ----------
        System.out.println("--- makeOptions (20 pairs) ---");
        printResult("BEFORE (default cap)", beforeMakeNs);
        printResult("AFTER  (pre-sized) ", afterMakeNs);
        System.out.printf("  Speedup: %.1f%%%n%n",
            100.0 * (beforeMakeNs - afterMakeNs) / beforeMakeNs);

        System.out.println("--- addOptions (20 pairs) ---");
        printResult("BEFORE (temp map)  ", beforeAddNs);
        printResult("AFTER  (inline put)", afterAddNs);
        System.out.printf("  Speedup: %.1f%%%n%n",
            100.0 * (beforeAddNs - afterAddNs) / beforeAddNs);

        // Correctness check
        Map<String, String> a = makeOptions_before(KEYS_AND_VALUES);
        Map<String, String> b = makeOptions_after(KEYS_AND_VALUES);
        System.out.println("Correctness check: " + (a.equals(b) ? "PASS" : "FAIL"));
    }

    private static void printResult(String label, long totalNs) {
        double avgNs = (double) totalNs / ITERATIONS;
        System.out.printf("  %s  total=%,d ms  avg=%.1f ns/call%n",
            label, totalNs / 1_000_000, avgNs);
    }
}
