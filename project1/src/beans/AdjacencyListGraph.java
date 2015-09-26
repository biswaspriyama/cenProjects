package beans;

import enums.Configurations;

import java.math.BigInteger;
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
        adj[j].add(i);
    }
    public boolean hasEdge(int i, int j) {
        return adj[i].contains(j);
    }
//    public List<Integer> adjacentTo(int n){
//        return adj[n];
//    }

    public int getDistanceSum(AdjacencyListGraph G, int s) {

        int sumDistance=0;
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
                    sumDistance=sumDistance+dist.get(w);
                }
            }
        }
        return sumDistance;
    }

    public float[] getAlldegrees(AdjacencyListGraph G){
        float[] allDegrees = new float[G.size];
        for (int i=0; i<allDegrees.length;i++)
            allDegrees[i]=G.adj[i].size();

        return allDegrees;
    }

    public float getCharacteristicPathLength(AdjacencyListGraph G) {

        int size = Configurations.actualDatasize;
        //int pathLength=0;
        BigInteger pathLength=BigInteger.ZERO;
        int sumDist=0;
        for(int i=0;i< adj.length;i++)
        {
            sumDist=getDistanceSum(G, i);
            //pathLength=pathLength+sumDist;
            pathLength=pathLength.add(BigInteger.valueOf(sumDist));
            //System.out.println(i);
        }
        System.out.println(pathLength);
        return (0);
        //return (2*edgeCount/(Configurations.actualDatasize*(Configurations.actualDatasize-1)));
    }

    public int getClusteringCoefficientSingleNode(AdjacencyListGraph G, int node){


        int clusterEdgeCount = G.adj[node].size();
        int vertexCount = clusterEdgeCount;

        for(int i=0;i< G.adj[node].size();i++)
        {
            for(int j=i+1; j < G.adj[node].size();j++)
            {
                int a = G.adj[node].get(i);
                int b = G.adj[node].get(j);
                if(G.hasEdge(a,b))
                    clusterEdgeCount++;
            }
        }
        int coefficient = 2*clusterEdgeCount/(vertexCount*(vertexCount-1));
        return coefficient;
    }

    public float getClusteringCoefficientAllNodes(AdjacencyListGraph G){
        int size = Configurations.actualDatasize;
        float clusteringCoeff=0;
        int singleCluster=0;

        for(int i=0;i< adj.length;i++)
        {
            singleCluster=getClusteringCoefficientSingleNode(G, i);
            clusteringCoeff+=singleCluster;

        }

        clusteringCoeff = clusteringCoeff/(float)size;
        return clusteringCoeff/Configurations.actualDatasize;

    }


}