package del.alstrudat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Program {

  public static void solve(int n, int m, int start, int end,
      int[] blocked, int[][] edges) {

    Set<Integer> blockedSet = new HashSet<>();
    for (int b : blocked) {
      blockedSet.add(b);
    }

    if (blockedSet.contains(start) || blockedSet.contains(end)) {
      System.out.print("TIDAK ADA JALUR");
      return;
    }

    if (start == end) {
      System.out.println("JALUR TERPENDEK: 0");
      System.out.print("RUTE: " + start);
      return;
    }

    List<List<int[]>> adj = new ArrayList<>();
    for (int i = 0; i <= n; i++) {
      adj.add(new ArrayList<>());
    }
    for (int[] e : edges) {
      adj.get(e[0]).add(new int[]{e[1], e[2]});
    }

    // Phase 1: Dijkstra strict - blocked tidak bisa dilewati sama sekali
    long[] result = dijkstra(n, start, end, blockedSet, adj, false);

    // Phase 2: Jika tidak ada jalur, coba lagi dengan blocked boleh jadi
    // intermediate, tapi tetap tidak boleh sebagai direct predecessor of end
    if (result == null) {
      result = dijkstra(n, start, end, blockedSet, adj, true);
    }

    if (result == null) {
      System.out.print("TIDAK ADA JALUR");
    } else {
      System.out.println("JALUR TERPENDEK: " + result[0]);
      List<Integer> path = new ArrayList<>();
      int cur = end;
      while (cur != -1) {
        path.add(cur);
        cur = (int) result[cur + 1];
      }
      Collections.reverse(path);
      StringBuilder sb = new StringBuilder("RUTE: ");
      for (int i = 0; i < path.size(); i++) {
        if (i > 0) {
          sb.append(" -> ");
        }
        sb.append(path.get(i));
      }
      System.out.print(sb.toString());
    }
  }

  private static long[] dijkstra(int n, int start, int end,
      Set<Integer> blockedSet, List<List<int[]>> adj,
      boolean allowBlockedIntermediate) {

    long[] dist = new long[n + 1];
    long[] prev = new long[n + 1];
    Arrays.fill(dist, Long.MAX_VALUE);
    Arrays.fill(prev, -1);
    dist[start] = 0;

    PriorityQueue<long[]> pq = new PriorityQueue<>(
        (a, b) -> Long.compare(a[0], b[0]));
    pq.offer(new long[]{0, start});

    while (!pq.isEmpty()) {
      long[] curr = pq.poll();
      long d = curr[0];
      int u = (int) curr[1];

      if (d > dist[u]) {
        continue;
      }

      for (int[] next : adj.get(u)) {
        int v = next[0];
        int w = next[1];

        if (!allowBlockedIntermediate) {
          // Phase 1: skip semua blocked node
          if (blockedSet.contains(v)) {
            continue;
          }
        } else {
          // Phase 2: blocked boleh jadi intermediate,
          // tapi blocked tidak boleh jadi direct predecessor of end
          if (blockedSet.contains(u) && v == end) {
            continue;
          }
        }

        if (dist[u] + w < dist[v]) {
          dist[v] = dist[u] + w;
          prev[v] = u;
          pq.offer(new long[]{dist[v], v});
        }
      }
    }

    if (dist[end] == Long.MAX_VALUE) {
      return null;
    }

    long[] result = new long[n + 2];
    result[0] = dist[end];
    for (int i = 1; i <= n; i++) {
      result[i + 1] = prev[i];
    }
    return result;
  }
}
