package modules;

import beans.AdjacencyListGraph;
import beans.MysqlConnector;
import enums.Configurations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yugarsi on 9/25/15.
 */
public class GraphParameters {



    public void computeAllParameters(){

        MysqlConnector sql = new MysqlConnector();
        String[] tableNames = {"ice9years3","ice9years2","ice9years1","4weeklag","Graph_test"};
        for (String table : tableNames){
            AdjacencyListGraph G = sql.readStringRows(table);
//            float clCf = G.getClusteringCoefficientAllNodes(G);
//            System.out.print(table +"  Clustering coefficient:"+ clCf+"\n");


            double ptLen = G.getCharacteristicPathLength(G);
            System.out.print(table +"  CharacteristicPathLength:"+ ptLen+"\n");
//
            double[] lRandom = G.getGRandom(G);
//            System.out.print(table +"  Gamma Random:"+ lRandom[0]+"\n");
            System.out.print(table +"  L Random:"+ lRandom[1]+"\n");

        }


    }





//    public static void smallWorld() {
//
//        AdjacencyListGraph G = new AdjacencyListGraph(5);
//        G.setEdge(0,1);
//        G.setEdge(1,2);
//        G.setEdge(2,3);
//        G.setEdge(3,4);
//        GraphParameters pf=new GraphParameters();
//        //MysqlConnector sql = new MysqlConnector();
//        //AdjacencyListGraph G = sql.readStringRows(MySqlConfig.tableName);
//
//        for(int i=0; i < 5;i++)
//            //G.getClusteringCoefficientSingleNode(G, i);
//            pf.(G, i);
//
//    }



}
