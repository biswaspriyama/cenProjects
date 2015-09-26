package modules;

import beans.AdjacencyListGraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yugarsi on 9/25/15.
 */
public class GraphParameters {

    public void getDistance(AdjacencyListGraph G, int s) {

        HashMap<Integer, Integer> dist=new HashMap <Integer, Integer>();
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(s);
        dist.put(s, 0);
        int sumDist=0;
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
        return edgeCount;
    }

    public void getGRandom(){





    }




    public static void smallWorld() {

        AdjacencyListGraph G = new AdjacencyListGraph(5);
        G.setEdge(0,1);
        G.setEdge(1,2);
        G.setEdge(2,3);
        G.setEdge(3,4);
        GraphParameters pf=new GraphParameters();
        //MysqlConnector sql = new MysqlConnector();
        //AdjacencyListGraph G = sql.readStringRows(MySqlConfig.tableName);

        for(int i=0; i < 5;i++)
            //G.getClusteringCoefficientSingleNode(G, i);
            pf.getDistance(G, i);

    }



}
