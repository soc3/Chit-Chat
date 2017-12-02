/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.user;

/**
 *
 * @author sushant oberoi
 */
import java.util.ArrayList;
import java.util.LinkedList;

public class UserNetwork {

    // A user define class to represent a graph.
    // A graph is an array of adjacency lists.
    // Size of array will be V (number of vertices 
    // in graph)
    private UserNetwork() {

    }
    static Graph graph;

    public static void initialize() {
        graph = new Graph(100000);
    }

    static class Graph {

        int V;
        LinkedList<Integer> adjListArray[];

        // constructor 
        Graph(int V) {
            this.V = V;

            // define the size of array as 
            // number of vertices
            adjListArray = new LinkedList[V];

            // Create a new list for each vertex
            // such that adjacent nodes can be stored
            for (int i = 0; i < V; i++) {
                adjListArray[i] = new LinkedList<>();
            }
        }
    }

    // Adds an edge to an undirected graph
    public static void addEdge(int src, int dest) {
        // Add an edge from src to dest. 
        System.out.println("adding edge " + src + " " + dest);
        graph.adjListArray[src].addFirst(dest);

        // Since graph is undirected, add an edge from dest
        // to src also
        graph.adjListArray[dest].addFirst(src);
    }

    // A utility function to print the adjacency list 
    // representation of graph
    static void printGraph() {
        for (int v = 0; v < graph.V; v++) {
            System.out.println("Adjacency list of vertex " + v);
            System.out.print("head");
            for (Integer pCrawl : graph.adjListArray[v]) {
                System.out.print(" -> " + pCrawl);
            }
            System.out.println("\n");
        }
    }
    static int source ;
    static void dfs(int[] vis, ArrayList<Integer> list, int s) {
        vis[s] = 1;
        if(s != source){
            list.add(s);
        }
        for (Integer pCrawl : graph.adjListArray[s]) {
            if(vis[pCrawl] == 0)
                dfs(vis, list, pCrawl);
        }
    }
    static void print(ArrayList<Integer> ids){
        System.out.println("printint ids:");
        for(int i=0; i < ids.size(); i++){
            System.out.println(ids.get(i));
        }
    }
    static ArrayList<Integer> getFriends(int v) {
        source = v;
        int[] vis = new int[100000];
        for(int i=0; i<100000; i++){
            vis[i] = 0;
        }
        ArrayList<Integer> ids = new ArrayList<Integer>();
        dfs(vis, ids, v);
        print(ids);
        return ids;
    }

    // Driver program to test above functions
}
