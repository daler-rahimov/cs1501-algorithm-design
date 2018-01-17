/*
 * This class will find all the path for a given price. Given a dollar amount 
 * entered by the user, whose cost is less than or equal to that amount
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Daler Rahimov
 */
public class AllPathEdgeWeighted {

    private double maxPriceAmount;
    private double priceOcumilated;
    private boolean marked[];
    private Bag<DirectedDiWeightEdge>[] allPaths; // entire path from one vertax to others
    private EdgeWeightedDigraph G;
    private Stack<DirectedDiWeightEdge> pathStack;
    private HashMap<Integer, String> intToCityMap;

    public AllPathEdgeWeighted(EdgeWeightedDigraph graph, double priceAmount, HashMap<Integer, String> intToCityMap) {
        G = graph;
        this.intToCityMap = intToCityMap;
        this.maxPriceAmount = priceAmount;
        allPaths = (Bag<DirectedDiWeightEdge>[]) new Bag[G.V()];
        for (int i = 0; i < G.V(); i++) {
        pathStack = new Stack<>();
        priceOcumilated = 0;
        marked = new boolean[G.V()];

        allPaths(i, 0.0);
        }

    }

    private void allPaths(int v, double price) {
        boolean isPrinted = false;
        priceOcumilated += price;
        if (priceOcumilated > maxPriceAmount) {
            priceOcumilated -= price;
            return;
        }
        marked[v] = true;
        for (DirectedDiWeightEdge e : G.adj(v)) {
            if (!marked[e.to()]) {
                pathStack.push(e);//in each itoration puth the edge to the stack
                allPaths(e.to(), e.price());
                if (pathStack.size() != 1 && !isPrinted) {// if it is not the first call
                    isPrinted = true;
                    System.out.print("Cost: " + priceOcumilated + " Path : ");
                    //print from here to the start and pop this edge from the stack
                    Iterator<DirectedDiWeightEdge> iter = pathStack.iterator();
                    while (iter.hasNext()) {
                        DirectedDiWeightEdge ed = iter.next();
                        if (iter.hasNext()) {// if there is more to print 
                            System.out.print(" " + intToCityMap.get(ed.from()) + " " + String.format("%1$.0f", ed.price()) + " ");
                        } else {// if this is the last element 
                            System.out.print(" " + intToCityMap.get(ed.from()) + "\n");
                        }
                    }
                }
                pathStack.pop();

            } else {
                pathStack.push(e);//in each itoration puth the edge to the stack
                if (pathStack.size() != 1 && !isPrinted) {// if it is not the first call
                    isPrinted = true;
                    System.out.print("Cost: " + priceOcumilated + " Path : ");
                    //print from here to the start and pop this edge from the stack
                    Iterator<DirectedDiWeightEdge> iter = pathStack.iterator();
                    while (iter.hasNext()) {
                        DirectedDiWeightEdge ed = iter.next();
                        if (iter.hasNext()) {// if there is more to print 
                            System.out.print(" " + intToCityMap.get(ed.from()) + " " + String.format("%1$.0f", ed.price()) + " ");
                        } else {// if this is the last element 
                            System.out.print(" " + intToCityMap.get(ed.from()) + "\n");
                        }
                    }
                }
                pathStack.pop();
            }
        }
        priceOcumilated -= price;
        marked[v] = false;
    }
}
