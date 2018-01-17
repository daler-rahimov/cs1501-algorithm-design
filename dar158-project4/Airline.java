
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Daler Rahimov
 */
public class Airline {

    private static EdgeWeightedDigraph fullGraph;
    private static HashMap<String, Integer> cityToIntMap;
    private static HashMap<Integer, String> intToCityMap;
    private static String fileName;
    private static Bag<DirectedDiWeightEdge> allEdges;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        allEdges = new Bag<>();
        //1.read the file and build the graph
        readFileNBuildGraph();

        while (true) {
            int option = getOption();

            switch (option) {

                case 1:
                    /* 3.i. Show the entire list of direct routes, distances and prices. 
                     This amounts to outputting the entire graph (again, no graphics) 
                     in a well-formatted way. Note that this operation does not require 
                     any sophisticated algorithms to implement.
                     */
                    showEntireList();
                    break;
                case 2:
                    /* 3.ii. Display (again, no graphics -- points and edges are ok) a minimum 
                     spanning tree for the service routes based on distances. This could be useful
                     for maintenance routes and/or shipping supplies from a central supply location
                     to all of the airports. If the route graph is not connected, this query should
                     identify and show a minimum spanning tree for each connected component of the 
                     graph.
                     */
                    displayMST();//   DISCONECTED GRAPHS NOT SUPPORTED 
                    break;
                case 3:
                    /* 3.iii.1 Shortest path based on total miles (one way) from the source
                     to the destination. Assuming distance and time are directly related, this 
                     could be useful to passengers who are in a hurry. It would also appeal to 
                     passengers who want to limit their carbon footprints.
                     */
                    shortestPathDistance();
                    break;
                case 4:
                    /* 3.iii.2 Shortest path based on price from the source to the destination. 
                     This option is a bit naïve, since, in the real world, prices are not necessarily 
                     additive for hops on a multi-city flight. However, to keep the algorithm fairly simple,
                     for this project, you should assume the prices ARE additive. Since distance and price 
                     do NOT always correspond, this could be useful to passengers who want to save money.
                     */
                    shortestPathPrice();
                    break;
                case 5:
                    /* 3.iii.3 Shortest path based on number of hops (individual segments) from 
                     the source to the destination. This option could be useful to passengers who 
                     prefer fewer segments for one reason or other (ex: traveling with small children).
                     */
                    shortestPathHops();
                    break;
                case 6:
                    /* 3.iv. Given a dollar amount entered by the user, print out all trips
                     whose cost is less than or equal to that amount. In this case, a trip can
                     contain an arbitrary number of hops (but it should not repeat any cities
                     – i.e. it cannot contain a cycle). This feature would be useful for the 
                     airline to print out weekly "super saver" fare advertisements. Be careful
                     to implement this option as efficiently as possible, since it has the 
                     possibility of having an exponential run-time (especially for long paths). 
                     Consider a recursive / backtracking / pruning approach.
                     */
                    printAllTrips();
                    break;
                case 7:
                    /*3.v Add a new route to the schedule. Assume that both cities already 
                     exist, and the user enters the vertices, distance, and price for the new 
                     route. Clearly, adding a new route to the schedule may affect the searches 
                     / algorithms indicated above.
                     */
                    promptNAddNewRoute();
                    break;
                case 8:
                    /*3.vi. Remove a route from the schedule. The user enters the vertices 
                     defining the route. Again, note that this may also affect the searches 
                     algorithms indicated above.
                     */
                    romoveRoute();
                    break;
                case 9:
                    /* 3.vii. Quit the program. Before quitting, your routes should be saved 
                     back to the file (the same file and format that they were read in from, but 
                     containing the possibly modified route information).
                     */
                    quit();
                    break;
            }
        }
    }

    /**
     * city -> city. Price: $100.00 Distance: 1000
     *
     */
    private static void showEntireList() {
        System.out.println("ENTIRE LIST OF DIRECT ROUTES"
                + "-----------------------");
        System.out.println(fullGraph.showEntireList(intToCityMap));
    }

    private static void readFileNBuildGraph() throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the filename containing all of the routes offered by the airline: ");
        fileName = scan.next();
        scan = new Scanner(new File(fileName));
//        Scanner scan = new Scanner(new File("src/project4/tinyEDG.txt"));

        int numOfVer, v, w;
        String city;
        double price;
        double distance;
        DirectedDiWeightEdge edge1;
        DirectedDiWeightEdge edge2;

        String line = scan.nextLine();
        numOfVer = Integer.parseInt(line);
        fullGraph = new EdgeWeightedDigraph(numOfVer);
        cityToIntMap = new HashMap<>();
        intToCityMap = new HashMap<>();

        // get the cities 
        for (int i = 0; i < numOfVer; i++) {
            city = scan.nextLine();
            cityToIntMap.put(city, i);
            intToCityMap.put(i, city);
        }

        // get the edges
        while (scan.hasNext()) {
            v = scan.nextInt() - 1;
            w = scan.nextInt() - 1;
            distance = scan.nextDouble();
            price = scan.nextDouble();
            edge1 = new DirectedDiWeightEdge(v, w, distance, price);
            edge2 = new DirectedDiWeightEdge(w, v, distance, price);
            allEdges.add(edge1);
            fullGraph.addEdge(edge1);
            fullGraph.addEdge(edge2);
        }
    }

    private static void displayMST() {
        PrimMST mst = new PrimMST(fullGraph);
        System.out.println("\nMINIMUM SPANNING TREE\n" + "---------------------");
        System.out.println("The edges in the MST based on distance follow:");
        for (DirectedDiWeightEdge e : mst.edges()) {
            System.out.println(e.cityDistanceToString(intToCityMap));
        }
    }

    private static void shortestPathDistance() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("Enter source and target (e.g. Pittsburgh Erie)): ");
        String source = scanSys.next();
        String target = scanSys.next();

        if (!cityToIntMap.containsKey(source) || !cityToIntMap.containsKey(target)) {
            System.out.println("ONE OF THE CITIES ENTERED IS NOT IN THE GRAPH!!!");
            return;
        }
        int s = cityToIntMap.get(source);
        int t = cityToIntMap.get(target);
        DijkstraSP sp = new DijkstraSP(fullGraph, s, false);
        if (!sp.hasPathTo(t)) {
            System.out.println("THERE IS NO PATH FROM " + source + " to " + target);
            return;
        }
        System.out.println("\nSHORTEST DISTANCE PATH from " + source + " to " + target + "\n"
                + "----------------------------------------------\n"
                + "Shortest distance from " + source + " to " + target + " is " + sp.distTo(t) + "\n"
                + "Path with edges : ");
        System.out.print(target + " ");
        for (DirectedDiWeightEdge e : sp.pathTo(t)) {
            System.out.print(e.toStringToCityNDistance(intToCityMap) + " ");
        }
        System.out.println();
        System.out.println();

    }

    private static void shortestPathPrice() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("Enter source and target (e.g. Pittsburgh Erie)): ");
        String source = scanSys.next();
        String target = scanSys.next();

        if (!cityToIntMap.containsKey(source) || !cityToIntMap.containsKey(target)) {
            System.out.println("ONE OF THE CITIES ENTERED IS NOT IN THE GRAPH!!!");
            return;
        }
        int s = cityToIntMap.get(source);
        int t = cityToIntMap.get(target);
        DijkstraSP sp = new DijkstraSP(fullGraph, s, true);
        if (!sp.hasPathTo(t)) {
            System.out.println("THERE IS NO PATH FROM " + source + " to " + target);
            return;
        }
        System.out.println("\nSHORTEST COST PATH from " + source + " to " + target + "\n"
                + "----------------------------------------------\n"
                + "Shortest cost from " + source + " to " + target + " is " + sp.distTo(t) + "\n"
                + "Path with edges : ");
        System.out.print(target + " ");
        for (DirectedDiWeightEdge e : sp.pathTo(t)) {
            System.out.print(e.toStringToCityNPrice(intToCityMap) + " ");
        }
        System.out.println();

    }

    private static void shortestPathHops() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("Enter source and target (e.g. Pittsburgh Erie)): ");
        String source = scanSys.next();
        String target = scanSys.next();

        if (!cityToIntMap.containsKey(source) || !cityToIntMap.containsKey(target)) {
            System.out.println("ONE OF THE CITIES ENTERED IS NOT IN THE GRAPH!!!");
            return;
        }
        int s = cityToIntMap.get(source);
        int t = cityToIntMap.get(target);
        BreadthFirstPaths bfs = new BreadthFirstPaths(fullGraph, s);
        if (!bfs.hasPathTo(t)) {
            System.out.println("THERE IS NO PATH FROM " + source + " to " + target);
            return;
        }
        System.out.println("\nFEWEST HOPS from " + source + " to " + target + "\n"
                + "----------------------------------------------\n"
                + "Fewest hops from " + source + " to " + target + " is " + bfs.distTo(t) + "\n"
                + "Path (in reverse order): ");
        for (int e : bfs.pathTo(t)) {
            System.out.print(intToCityMap.get(e) + " ");
        }
        System.out.println();
    }

    private static void printAllTrips() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("Enter max price (e.g. 404.00 or 404)): ");
        double maxPrice = scanSys.nextDouble();

        System.out.println("\nALL PATHS OF COST " + maxPrice + " OR LESS\n"
                + "-----------------------------------------------------------------------\n"
                + "List of paths at most " + maxPrice + " in length:");
        AllPathEdgeWeighted allPath = new AllPathEdgeWeighted(fullGraph, maxPrice, intToCityMap);
        System.out.println();
    }

    private static void promptNAddNewRoute() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("No update only new route!\nEnter the new route (e.g. Pittsburgh Erie 100 200.00)): ");
        String city1 = scanSys.next();
        String city2 = scanSys.next();
        if (!cityToIntMap.containsKey(city2) || !cityToIntMap.containsKey(city1)) {
            System.out.println("One of the cities is not in the graph!");
            return;
        }
        double distance = scanSys.nextDouble();
        double price = scanSys.nextDouble();
        int v = cityToIntMap.get(city1);
        int w = cityToIntMap.get(city2);
        DirectedDiWeightEdge edge1 = new DirectedDiWeightEdge(v, w, distance, price);
        DirectedDiWeightEdge edge2 = new DirectedDiWeightEdge(w, v, distance, price);
        allEdges.add(edge1);
        fullGraph.addEdge(edge1);
        fullGraph.addEdge(edge2);
        System.out.println("New route has been added");
    }

    private static void romoveRoute() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("Enter the route to delete (e.g. Pittsburgh Erie)): ");
        String city1 = scanSys.next();
        String city2 = scanSys.next();
        if (!cityToIntMap.containsKey(city2) || !cityToIntMap.containsKey(city1)) {
            System.out.println("One of the cities is not in the graph!");
            return;
        }
        int v = cityToIntMap.get(city1);
        int w = cityToIntMap.get(city2);
        DirectedDiWeightEdge edge1 = new DirectedDiWeightEdge(v, w, 0, 0);
        DirectedDiWeightEdge edge2 = new DirectedDiWeightEdge(w, v, 0, 0);
        fullGraph.removeEdge(edge2);
        fullGraph.removeEdge(edge1);
        allEdges.remove(edge1);
        System.out.println("The route has been removed!");
    }

    private static void quit() throws FileNotFoundException, IOException {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(fullGraph.V());
            Iterator it = intToCityMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, String> pair = (Map.Entry) it.next();
                writer.println(pair.getValue());
                it.remove();
            }
            for (DirectedDiWeightEdge e : allEdges) {
                writer.println((e.from() + 1) + " " + (e.to() + 1) + " "
                        + String.format("%1$.0f", e.distance()) + " " + String.format("%1$.2f", e.price()));
            }
            writer.close();
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static int getOption() {
        Scanner scanSys = new Scanner(System.in);
        System.out.println("\n-----------------------");
        System.out.println("Options: "
                + "\n1 -> Show the entire list of direct routes, distances and prices."
                + "\n2 -> Display a minimum spanning tree for the service routes based on distances"
                + "\n3 -> Shortest path based on total miles (one way) from the source to the destination."
                + "\n4 -> Shortest path based on price from the source to the destination. "
                + "\n5 -> Shortest path based on number of hops (individual segments) from the source to the destination."
                + "\n6 -> Given a dollar amount entered print out all trips whose cost is less than or equal to that amount."
                + "\n7 -> Add a new route to the schedule. Assume that both cities already exist."
                + "\n8 -> Remove a route from the schedule."
                + "\n9 -> Quit the program.");
        System.out.print("Enter an option: ");
        int out = scanSys.nextInt();
        System.out.println("-----------------------");
        scanSys.nextLine();
        return out;

    }

}
