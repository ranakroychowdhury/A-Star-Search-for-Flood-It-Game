import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Ranakrc on 06-Oct-17.
 */
public class Node {

    int[][] board;
    Node parent;
    int g, f, h;
    int color;


    public void declareBoard(int size) {
        board = new int[size][size];
    }


    public boolean isGoal(int size) {
        int boardColor = board[0][0];
        int i, j;
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (board[i][j] != boardColor)
                    return false;
            }
        }
        return true;
    }

    public void initializeBoard(int size) {

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++)
                board[i][j] = 0;
        }
    }

    public void printBoard(int size) {
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++)
                System.out.print(board[i][j] + " ");
            System.out.println();
        }
    }
}


class Game3 {

    public static int size, colors = 6, V;
    public static ArrayList<Node> minQueue = new ArrayList<>();
    public static ArrayList<Node>  exploredList = new ArrayList<>();

    //NON ADMISSIBLE HEURISTIC
    //returns false if any vertex is left unexplored, else returns true
    public static int testBoolean(boolean[] v) {

        for(int i=0; i<size*size; i++)
            if(!v[i]) return i;

        return -1;
    }

    //NON ADMISSIBLE HEURISTIC
    static void DFS(int[][] a, int i, int j, int point, boolean[] v){

        if (i < 0 || i >= size || j < 0 || j >= size)
            return;
        if(v[point])
            return;

        v[point] = true;

        //check the bottom box
        if(i>=0 && i<size - 1 && j>=0 && j<size && v[size*(i+1) + j] == false  && a[i][j] == a[i+1][j])
            DFS(a, i+1, j, size*(i+1) + j, v);

        //check the top box
        if(i>0 && i<size && j>=0 && j<size && v[size*(i-1) + j] == false  && a[i][j] == a[i-1][j])
            DFS(a, i-1, j, size*(i-1) + j, v);

        //check the right box
        if(i>=0 && i<size && j>=0 && j<size-1 && v[size*i + j + 1] == false  && a[i][j] == a[i][j+1])
            DFS(a, i, j+1, size*i + j + 1, v);

        //check the left box
        if(i>=0 && i<size && j>0 && j<size && v[size*i + j - 1] == false  && a[i][j] == a[i][j-1])
            DFS(a, i, j-1, size*i + j - 1, v);

    }


    //NON ADMISSIBLE HEURISTIC
    static int nonAdmissibleHeuristic(int[][] a) {
        int numClusters = 0;

        // Mark all the vertices as not visited(set as
        // false by default in java)
        boolean[] visited = new boolean[size*size];

        int point = testBoolean(visited);
        //test if any vertex hasn't been visited yet, then
        // Call the recursive function DFS
        while(point != -1) {
            int i = point/size, j = point - size*i;
            numClusters++;
            DFS(a, i, j, point, visited);
            point = testBoolean(visited);
        }

        return numClusters;
    }


    //ADMISSIBLE HEURISTIC
    //Find the vertex with minimum distance value,
    //from the set of vertices not yet included in shortest path tree
    public static int minDistance(int dist[], Boolean sptSet[]) {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index=-1;

        for (int v = 0; v < V; v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }


    //ADMISSIBLE HEURISTIC
    //Find the longest of the shortest distances calculed by Djikstra
    public static int maxDist(int dist[]) {
        int max = Integer.MIN_VALUE;
        for(int i=0; i<size*size; i++){
            if(dist[i] > max)
                max = dist[i];
        }
        return max;
    }


    //ADMISSIBLE HEURISTIC
    //Funtion that implements Dijkstra's single source shortest path
    //algorithm for a graph represented using adjacency matrix
    //representation
    public static int dijkstra(int graph[][], int src) {

        int dist[] = new int[V]; // The output array. dist[i] will hold
        // the shortest distance from src to i

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        Boolean sptSet[] = new Boolean[V];

        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < V; i++)
        {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // Find shortest path for all vertices
        for (int count = 0; count < V-1; count++)
        {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 0; v < V; v++)

                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && graph[u][v]!=-1 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u]+graph[u][v] < dist[v])
                    dist[v] = dist[u] + graph[u][v];
        }

        // print the constructed distance array
        //printSolution(dist, V);

        //find Maximum distance
        int h = maxDist(dist);
        return h;
    }

    //ADMISSIBLE HEURISTIC
    //Function to calculte h via admissible heuristic
    public static int admissibleHeuristic(int[][] a) {
        int[][] graph = new int[size*size][size*size];

        //initialize Graph
        for(int i=0; i<size*size; i++) {
            for(int j=0; j<size*size; j++)
                graph[i][j] = -1;
        }

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++) {

                //compare with top element
                if(i > 0 && a[i][j] != a[i-1][j])
                    graph[size*i + j][size*(i-1) + j] = 1;
                else if(i > 0 && a[i][j] == a[i-1][j])
                    graph[size*i + j][size*(i-1) + j] = 0;

                //compare with down element
                if(i < size - 1 && a[i][j] != a[i+1][j])
                    graph[size*i + j][size*(i+1) + j] = 1;
                else if(i < size - 1 && a[i][j] == a[i+1][j])
                    graph[size*i + j][size*(i+1) + j] = 0;

                //compare with left element
                if(j > 0 && a[i][j] != a[i][j-1])
                    graph[size*i + j][size*i + j - 1] = 1;
                else if(j > 0 && a[i][j] == a[i][j-1])
                    graph[size*i + j][size*i + j - 1] = 0;

                //compare with right element
                if(j < size - 1 && a[i][j] != a[i][j+1])
                    graph[size*i + j][size*i + j + 1] = 1;
                else if(j < size - 1 && a[i][j] == a[i][j+1])
                    graph[size*i + j][size*i + j + 1] = 0;

                //others are all set to -1
                //as there are no edges with nodes other than the adjacent nodes in the matrix
            }

        }

        // ShortestPath t = new ShortestPath();
        int h = dijkstra(graph, 0);
        return h;
    }


    //compares two Nodes to check if they are equal or not
    //returns true if nodes are equal, false otherwise
    public static boolean matchMatrix(int[][] a, int[][] b) {

        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                if(a[i][j] != b[i][j])
                    return false;
            }
        }

        return true;
    }


    //finds whether a node is present in a list of nodes
    //returns the index if present, -1 otherwise
    public static int findNode(Node a, ArrayList<Node> arr) {

        for(int i=0; i<arr.size(); i++) {
            if (matchMatrix(a.board, arr.get(i).board)) //if the two nodes match return the index
                return i;
        }
        return -1; //none of the nodes in the list matched, so returns -1
    }


    //Get the node with the minimum cost from the open list
    public static Node getMin(ArrayList<Node> arr) {

        int index = 0, minCost = arr.get(0).f;
        for(int i=1; i<arr.size(); i++) {
            if(arr.get(i).f < minCost) {
                minCost = arr.get(i).f;
                index = i;
            }
        }

        return arr.remove(index);

    }


    //copies one board configuration to another
    public static void copyBoard(int[][] dst, int[][] src) {

        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++)
                dst[i][j] = src[i][j];
        }

    }


    //color the board with a given color
    public static void colorRegion(int i, int j, int[][] mat, int color, int scolor) {

        //invalid coordinates
        if(i<0 || i>=size || j<0 || j>=size)
            return;

        //do not color region whose color does not match with board[0][0]
        if(mat[i][j] != scolor)
            return;

        mat[i][j] = color;
        colorRegion(i+1, j, mat, color, scolor); // color the bottom region
        colorRegion(i-1, j, mat, color, scolor); // color the top region
        colorRegion(i, j+1, mat, color, scolor); // color the right region
        colorRegion(i, j-1, mat, color, scolor); // color the left region
    }


    //returns a List of the child nodes
    public static ArrayList<Node> getNeighbor(Node a) {
        ArrayList<Node> nb = new ArrayList<>();
        boolean[] found = new boolean[7];

        //find which colors are present on board
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                found[a.board[i][j]] = true;
            }
        }

        for(int i=1; i<7; i++){

            if(!found[i]) //color i is not on board
                continue;

            //do not color the board with the color that is already in region board[0][0]
            if(a.board[0][0] == i)
                continue;

            Node t = new Node();
            t.declareBoard(size);
            t.initializeBoard(size);
            //Node a will be modified multiple times by its children
            //So copy board a to t and then modify board t
            copyBoard(t.board, a.board);

            //color the child node with color i
            colorRegion(0, 0, t.board, i, t.board[0][0]);
            //t.size = size;
            t.color = i;
            t.parent = a;
            t.g = a.g + 1;

            //SWITCH
            t.h = nonAdmissibleHeuristic(t.board);
            //t.h = admissibleHeuristic(t.board);

            t.f = t.g + t.h;
            nb.add(t);
        }

        return nb;
    }


    //Main calls AStarSearch First
    public static Node AStarSearch() {

        Node min = minQueue.get(0);
        while(!min.isGoal(size)) {

            min = getMin(minQueue);
            if(min.isGoal(size))
                return min;

            exploredList.add(min);
            ArrayList<Node> neighborList = new ArrayList<>(getNeighbor(min));

            for(int j=0; j<neighborList.size(); j++) {

                //if the node has already been explored, it is in explored list
                //no need to expand it, continue
                int idx = findNode(neighborList.get(j), exploredList);
                //System.out.println(idx);
                if(idx != -1)
                    continue;

                else{

                    int index = findNode(neighborList.get(j), minQueue);
                    //if the node is already in open list but the new node has a lower cost
                    //update
                    if(index != -1 && neighborList.get(j).f < minQueue.get(index).f ) {
                        minQueue.remove(index);
                        minQueue.add(neighborList.get(j));
                    }
                    else if(index == -1)
                        minQueue.add(neighborList.get(j));
                }
            }
        }

        min=getMin(minQueue);
        return min;
    }


    //prints the solution
    public static void printSol(Node root, Node goal) throws FileNotFoundException, UnsupportedEncodingException {

        ArrayList<Node> chosenPath = new ArrayList<>();
        PrintWriter writer = new PrintWriter("output.txt",  "UTF-8");
        //climb from goal to root and save the nodes
        while (!matchMatrix(root.board, goal.board)) {
            chosenPath.add(goal);
            goal = goal.parent;
        }

        //print the colors chosen
        writer.print("Color chosen: ");
        for (int i = chosenPath.size() - 1; i >= 0; i--)
            writer.print(chosenPath.get(i).color + " ");
        writer.println();

        writer.println("Board Configuration: ");
        int j = 1;
        //print the corresponding board configuration
        for (int i = chosenPath.size() - 1; i >= 0; i--) {
            writer.println("Move: " + j);
            for (int bRow = 0; bRow < size; bRow++) {
                for (int bCol = 0; bCol < size; bCol++)
                    writer.print(chosenPath.get(i).board[bRow][bCol] + " ");
                writer.println();
            }
            j++;
        }

        writer.println("The total number of moves is " + chosenPath.size());
        writer.close();
    }



    public static void initializeRoot(int[][] board, int len) {

        size = len;
        V = size*size;
        Node root = new Node();
        //root.size = size;
        root.declareBoard(size);
        root.initializeBoard(size);
        copyBoard(root.board, board);
        root.color = root.board[0][0];
        root.g = 0;

        //SWITCH
        //root.h = admissibleHeuristic(root.board);
        root.h = nonAdmissibleHeuristic(root.board);

        root.f = root.g + root.h;
        minQueue.add(root); //push the root into the minQueue

        //root.printBoard();
        Node goal = AStarSearch();
        //goal.printBoard();
        try{
            printSol(root, goal);
        }catch(IOException e) {
            System.out.println("Exception " + e);
        }
    }


    public static void processInput(int[] size) {

        int i = 0;

        while (size[i] != 0) {
            int boardSize = size[i];
            i++;
            int[][] board = new int[boardSize][boardSize];
            int j, k;
            for (j = 0; j < boardSize; j++) {
                for (k = 0; k < boardSize; k++) {
                    board[j][k] = size[i];
                    i++;
                }
            }
            initializeRoot(board, boardSize);
        }
    }


    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {

        Scanner scanner = new Scanner(new File("input1.txt"));

        int[] size = new int[10000];
        int i = 0;
        while (scanner.hasNextInt()) {
            size[i] = scanner.nextInt();
            i++;
        }
        processInput(size);
    }
}

