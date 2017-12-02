/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.user;

import static groupon.user.UserNetwork.graph;
import java.util.LinkedList;

/**
 *
 * @author sushant oberoi
 */
public class GraphUse {

    int V;
    LinkedList<Integer> adjListArray[];

    // constructor 
    public GraphUse(int V) {
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

    public void addEdge(int src, int dest) {
        // Add an edge from src to dest. 
        System.out.println("adding edge " + src + " " + dest);
        graph.adjListArray[src].addFirst(dest);

        // Since graph is undirected, add an edge from dest
        // to src also
        graph.adjListArray[dest].addFirst(src);
    }
}
