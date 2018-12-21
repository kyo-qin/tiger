package org.ota.tiger;

public class GraphTest2 {

    int n, m, minPath, edge[][], mark[];

    void deepthFirstSearch(int cur, int dst) {
        if (minPath < dst)
            return;
        if (cur == n) {
            if (minPath > dst)
                minPath = dst;
            return;
        } else {
            int i;
            for (i = 1; i <= n; i++) {
                if (edge[cur][i] != Integer.MAX_VALUE && edge[cur][i] != 0 && mark[i] == 0) {
                    mark[i] = 1;
                    deepthFirstSearch(i, dst + edge[cur][i]);
                    mark[i] = 0;
                }
            }
            return;
        }
    }
}
