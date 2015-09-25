package beans;

import java.util.*;

/**
 * Created by yugarsi on 8/30/15.
 */
public class AdjacencyListGraph {

    public List<Integer>[] adj;
    int size;
    int i;
    public AdjacencyListGraph(int n) {
        size = n;
        adj = (List<Integer>[])new List[size];
        for (i = 0; i < size ; i++)
            adj[i] = new ArrayList<Integer>();
    }
    public void setEdge(int i, int j) {
        adj[i].add(j);
    }
    public boolean hasEdge(int i, int j) {
        return adj[i].contains(j);
    }
//    public List<Integer> adjacentTo(int n){
//        return adj[n];
//    }
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

    public int getClusteringCoefficientSingleNode(AdjacencyListGraph G, int node){

        int edgeCount = G.adj[node].size();
        int temp = edgeCount;
        for(int i=0;i< G.adj[node].size();i++)
        {
            for(int j=i+1; j < G.adj[node].size();j++)
            {
                int a = G.adj[node].get(i);
                int b = G.adj[node].get(j);
                if(G.hasEdge(a,b))
                    edgeCount++;
            }
        }

//        if (temp != edgeCount)
//            System.out.print(temp+":"+edgeCount+"\n");
        return edgeCount;
    }




}