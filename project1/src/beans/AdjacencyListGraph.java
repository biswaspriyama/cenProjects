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
    public void setEdgeModified(int i, int j) {
        adj[i].add(j);
    }
    public boolean hasEdge(int i, int j) {
        return adj[i].contains(j);
    }
    public boolean noNeighbours(int v) {
        if (adj[v].size() == 0)
            return true;
        return false;
    }


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

    public double getCharacteristicPathLength(AdjacencyListGraph G) {

        int size = Configurations.actualDatasize;
        //int pathLength=0;
        double pathLength=0;
        double sumDist=0;
        int count = 0;
        for(int i=0;i< adj.length;i++)
        {
            if(!noNeighbours(i))
                sumDist=getDistanceSum(G, i);
                pathLength=pathLength+sumDist;
                //System.out.print(i +":"+ sumDist+"\n");
                count ++;

        }
        System.out.print("count:" + count+"\n");

        double charLength = 2*pathLength/(count*(count-1));
        return (charLength);

    }

    public double[] getGRandom(AdjacencyListGraph G){
        double[] gRandom = new double[2];
        float totalVertices = Configurations.actualDatasize;
        float[] vertexDegree = G.getAlldegrees(G);
        float vertexSum = 0;
        for(int i=0;i<vertexDegree.length;i++){
            vertexSum += vertexDegree[i];
        }
        double meanVertexDegree = vertexSum/totalVertices;
        double gammaRandom = meanVertexDegree/totalVertices;
        gRandom[0] = gammaRandom;
        double LengthRandom = Math.log(totalVertices)/Math.log(meanVertexDegree);
        gRandom[1] = LengthRandom;

        return gRandom;

    }

    public float getClusteringCoefficientSingleNode(AdjacencyListGraph G, int node){

        float clusterEdgeCount = G.adj[node].size();
        float vertexCount = 1 + clusterEdgeCount;

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
        if (vertexCount <= 1)
            return 0;

        float coefficient = 2 * clusterEdgeCount / (vertexCount * (vertexCount - 1));
        //System.out.print(vertexCount+":"+coefficient+"\n");
        return coefficient;
    }

    public float getClusteringCoefficientAllNodes(AdjacencyListGraph G){
        int size = Configurations.actualDatasize;
        float clusteringCoeff=0;
        float singleCluster=0;

        int nonZeroVertex = 0;
        for(int i=0;i< adj.length;i++)
        {
            singleCluster=getClusteringCoefficientSingleNode(G, i);
            if (singleCluster != 0)
                nonZeroVertex ++;
            clusteringCoeff+=singleCluster;

        }

        clusteringCoeff = clusteringCoeff/size;
        return clusteringCoeff;

    }


}