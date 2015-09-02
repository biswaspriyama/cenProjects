package beans;

import java.util.*;

/**
 * Created by yugarsi on 8/30/15.
 */
public class AdjacencyListGraph {

    List<Integer>[] adj;
    int size;
    public AdjacencyListGraph(int n) {
        size = n;
        adj = (List<Integer>[])new List[size];
        for (int i = 0; i < size ; i++)
            adj[i] = new ArrayList<Integer>();
    }
    public void setEdge(int i, int j) {
        adj[i].add(j);
    }
    public boolean hasEdge(int i, int j) {
        return adj[i].contains(j);
    }

}