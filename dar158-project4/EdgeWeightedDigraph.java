/**
 * ****************************************************************************
 * Compilation: javac EdgeWeightedDigraph.java Execution: java
 * EdgeWeightedDigraph digraph.txt Dependencies: Bag.java DirectedEdge.java Data
 * files: http://algs4.cs.princeton.edu/44st/tinyEWD.txt
 * http://algs4.cs.princeton.edu/44st/mediumEWD.txt
 * http://algs4.cs.princeton.edu/44st/largeEWD.txt
 *
 * An edge-weighted digraph, implemented using adjacency lists.
 *
 *****************************************************************************
 */

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * The {@code EdgeWeightedDigraph} class represents a edge-weighted digraph of
 * vertices named 0 through <em>V</em> - 1, where each directed edge is of type
 * {@link DirectedDiWeightEdge} and has a real-valued distance. It supports the
 * following two primary operations: add a directed edge to the digraph and
 * iterate over all of edges incident from a given vertex. It also provides
 * methods for returning the number of vertices <em>V</em> and the number of
 * edges <em>E</em>. Parallel edges and self-loops are permitted.
 * <p>
 * This implementation uses an adjacency-lists representation, which is a
 * vertex-indexed array of @link{Bag} objects. All operations take constant time
 * (in the worst case) except iterating over the edges incident from a given
 * vertex, which takes time proportional to the number of such edges.
 * <p>
 * For additional documentation, see
 * <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class EdgeWeightedDigraph {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;                // number of vertices in this digraph
    private int E;                      // number of edges in this digraph
    private Bag<DirectedDiWeightEdge>[] adj;    // adj[v] = adjacency list for vertex v
    private int[] indegree;             // indegree[v] = indegree of vertex v

    /**
     * Initializes an empty edge-weighted digraph with {@code V} vertices and 0
     * edges.
     *
     * @param V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public EdgeWeightedDigraph(int V) {
        if (V < 0) {
            throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        }
        this.V = V;
        this.E = 0;
        this.indegree = new int[V];
        adj = (Bag<DirectedDiWeightEdge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<DirectedDiWeightEdge>();
        }
    }

    /**
     * Returns the number of vertices in this edge-weighted digraph.
     *
     * @return the number of vertices in this edge-weighted digraph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in this edge-weighted digraph.
     *
     * @return the number of edges in this edge-weighted digraph
     */
    public int E() {
        return E;
    }

    // throw an IndexOutOfBoundsException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V) {
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
        }
    }

    /**
     * Adds the directed edge {@code e} to this edge-weighted digraph.
     *
     * @param e the edge
     * @throws IndexOutOfBoundsException unless endpoints of edge are between
     * {@code 0} and {@code V-1}
     */
    public void addEdge(DirectedDiWeightEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        indegree[w]++;
        E++;
    }
    
    public void removeEdge(DirectedDiWeightEdge e){
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].remove(e);
        adj[w].remove(e);
    }

    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     * @throws IndexOutOfBoundsException unless {@code 0 <= v < V}
     */
    public Iterable<DirectedDiWeightEdge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the number of directed edges incident from vertex {@code v}. This
     * is known as the <em>outdegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the outdegree of vertex {@code v}
     * @throws IndexOutOfBoundsException unless {@code 0 <= v < V}
     */
    public int outdegree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns the number of directed edges incident to vertex {@code v}. This
     * is known as the <em>indegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the indegree of vertex {@code v}
     * @throws IndexOutOfBoundsException unless {@code 0 <= v < V}
     */
    public int indegree(int v) {
        validateVertex(v);
        return indegree[v];
    }

    /**
     * Returns all directed edges in this edge-weighted digraph. To iterate over
     * the edges in this edge-weighted digraph, use foreach notation:
     * {@code for (DirectedDiWeightEdge e : G.edges())}.
     *
     * @return all edges in this edge-weighted digraph, as an iterable
     */
    public Iterable<DirectedDiWeightEdge> edges() {
        Bag<DirectedDiWeightEdge> list = new Bag<DirectedDiWeightEdge>();
        for (int v = 0; v < V; v++) {
            for (DirectedDiWeightEdge e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Returns a string representation of this edge-weighted digraph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of
     * edges <em>E</em>, followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 1; v < V; v++) {
            s.append(v + ": ");
            for (DirectedDiWeightEdge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    /**
     * Unit tests the {@code EdgeWeightedDigraph} data type.
     *
     * @param args the command-line arguments
     */
//    public static void main(String[] args) {
//        DirectedDiWeightEdge e = new DirectedDiWeightEdge(0, 1, 50, 60);
//        EdgeWeightedDigraph g = new EdgeWeightedDigraph(2);
//        g.addEdge(e);
//        System.out.println(g);
//    }

    /**
     * city -> city. Distance: 1000 Price: $100.00
     *
     */
    public String showEntireList(HashMap<Integer, String> city) {
        StringBuilder s = new StringBuilder();
        NumberFormat currencyFormatter
                = NumberFormat.getCurrencyInstance(Locale.getDefault());

        for (int v = 0; v < V; v++) {
            for (DirectedDiWeightEdge e : adj[v]) {
                s.append(city.get(e.from()) + " -> " + city.get(e.to()));
                s.append(". Distance: " + e.distance());
                s.append(". Price: " + currencyFormatter.format(e.price()));
                s.append(NEWLINE);
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

}

/**
 * ****************************************************************************
 * Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 * This file is part of algs4.jar, which accompanies the textbook
 *
 * Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne, Addison-Wesley
 * Professional, 2011, ISBN 0-321-57351-X. http://algs4.cs.princeton.edu
 *
 *
 * algs4.jar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * algs4.jar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * algs4.jar. If not, see http://www.gnu.org/licenses.
 * ****************************************************************************
 */
