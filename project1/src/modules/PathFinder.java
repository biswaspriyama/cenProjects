package modules;

/**
 * Created by yugarsi on 9/25/15.
 */

import beans.AdjacencyListGraph;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;




public class PathFinder {


    public void getDistance(AdjacencyListGraph G, int s) {

        HashMap <Integer, Integer> dist=new HashMap <Integer, Integer>();
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(s);
        dist.put(s, 0);

        while (!q.isEmpty()) {
            int v = q.poll();
            for (int w : G.adj[v]) {
                if (!dist.containsKey(w)) {
                    q.add(w);
                    dist.put(w, 1 + dist.get(v));
                }
            }
        }
    }

}
