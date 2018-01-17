/******************************************************************************
 *  Compilation:  javac DirectedEdge.java
 *  Execution:    java DirectedEdge
 *  Dependencies: StdOut.java
 *
 *  Immutable weighted directed edge.
 *
 ******************************************************************************/


import java.util.HashMap;

/**
 *  The {@code DirectedDiWeightEdge} class represents a weighted edge in an 
 *  {@link EdgeWeightedDigraph}. Each edge consists of two integers
  (naming the two vertices) and a real-value distance. The data type
  provides methods for accessing the two endpoints of the directed edge and
  the distance.
  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

public class DirectedDiWeightEdge { 
    private final int v;
    private final int w;
    private final double distance;// price 
    private final double price;// distance 

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given {@code distance}.
     * @param v the tail vertex
     * @param w the head vertex
     * @param weight1 the distance of the directed edge
     * @throws IndexOutOfBoundsException if either {@code v} or {@code w}
     *    is a negative integer
     * @throws IllegalArgumentException if {@code distance} is {@code NaN}
     */
    public DirectedDiWeightEdge(int v, int w, double distance, double price ) {
        if (v < 0) throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
        if (w < 0) throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
        if (Double.isNaN(distance)) throw new IllegalArgumentException("Weight is NaN");
        if (Double.isNaN(price)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.distance = distance;
        this.price = price;
    }

    /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public int from() {
        return v;
    }

    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public int to() {
        return w;
    }
    
    /**
     * Returns the distance of the directed edge.
     * @return the distance of the directed edge
     */
    public double distance() {
        return distance;
    }
    
    public double price(){
        return price;
    }

    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    @Override
    public String toString() {
        return v + "->" + w + " " + String.format("%.2f", distance) +String.format("  %.2f", price) ;
    }
    
    public String cityDistanceToString(HashMap<Integer,String> cities){
        return cities.get(v) + ", " + cities.get(w)+ " : " + String.format("%.2f", distance) ;
    }
    
    @Override
    public boolean equals(Object o){
        DirectedDiWeightEdge e  = (DirectedDiWeightEdge)o;
        return (e.from() == v && e.to()==w);
    }
    public String toStringToCityNDistance(HashMap<Integer, String> cities) {
        return String.format("%.2f", distance) + " "+ cities.get(v);
    }
    public String toStringToCityNPrice(HashMap<Integer, String> cities) {
        return String.format("%.2f", price) + " " + cities.get(v);
    }
    /**
     * Unit tests the {@code DirectedDoubleEdge} data type.
     *
     * @param args the command-line arguments
     */
//    public static void main(String[] args) {
//        DirectedDiWeightEdge e = new DirectedDiWeightEdge(12, 34, 50, 60);
//        System.out.println(e);
//    }
}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
