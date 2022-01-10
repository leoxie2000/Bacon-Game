import net.datastructures.Edge;

import java.util.*;

public class BFSLib {

    public BFSLib(){

    }
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
        //initializing
        AdjacencyMapGraph<V,E> pathtree = new AdjacencyMapGraph();
        Set<V> visited = new HashSet<>() ;
        V start = source;
        Queue<V> q = new LinkedList<>();
        q.add(source);
        visited.add(start);
        //conduct bfs search if queue not empty
        while(!q.isEmpty()){

            V init = q.poll();

            if(!pathtree.hasVertex(init)) pathtree.insertVertex(init);
            for(V neighbor: g.outNeighbors(init)){
                if(!visited.contains(neighbor)){
                    //push to queue if hasn't visited
                    visited.add(neighbor);
                    q.add(neighbor);

                    if(!pathtree.hasVertex(neighbor)){
                        pathtree.insertVertex(neighbor);
                    }

                    //insert the edge into the new graph
                    pathtree.insertDirected(neighbor,init,g.getLabel(neighbor,init));
                }

            }
        }

        return pathtree;

    };


    public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
        V init = v;
        List<V> path = new LinkedList<>();
        path.add(v);

        while(tree.outDegree(init) != 0){
            V parentVertex = null;

            //only one outneighbor for each vertex: the parent vertex
            for(V parent:tree.outNeighbors(init)){
                parentVertex = parent;
            }

            init = parentVertex;
            path.add(init);

        }
        return path;

    };
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
        Set<V> missingVertice = new HashSet<>();
        for(V v: graph.vertices()) {
            if(!subgraph.hasVertex(v)){
                missingVertice.add(v);
            }
        }
        return missingVertice;
    }


    private static int sum; // keeping track of the sum of edges

    public static <V,E> double averageSeparation(Graph<V,E> tree, V root){
        if(!tree.hasVertex(root)) System.out.println("root not found"); //boundary case


        int numberV = tree.numVertices();

        //recursively add to sum
        averageHelper(tree,root,0);

        double result = (double)sum/numberV;

        return result;

    }

    //helper method for finding the average separation
    public static <V,E> void averageHelper(Graph<V,E> tree, V root,int dist){
        int distance = dist;

        //as long as there's inneighbor, recurse with it and add to sum the current distance
        if(tree.inDegree(root)!=0){
            for(V v: tree.inNeighbors(root)){
                sum+=(distance+1);
                averageHelper(tree,v,distance+1);
            }
        }

    }


    public static <V,E> void main(String[] args) {
        Graph<String, String> relationships = new AdjacencyMapGraph<String, String>();
        relationships.insertVertex("Kevin Bacon");

        relationships.insertVertex("Alice");
        relationships.insertVertex("Bob");
        relationships.insertVertex("Charlie");
        relationships.insertVertex("Dartmouth");
        relationships.insertVertex("Nobody");
        relationships.insertVertex("Nobody's friend");
        relationships.insertDirected("Alice", "Kevin Bacon", "A Movie");
        relationships.insertDirected("Kevin Bacon", "Alice", "A Movie");
        relationships.insertUndirected("Alice", "Kevin Bacon", "E Movie");
        relationships.insertUndirected("Alice", "Charlie", "D Movie");
        relationships.insertUndirected("Charlie", "Bob", "C Movie");
        relationships.insertUndirected("Alice", "Bob", "A Movie");
        relationships.insertUndirected("Kevin Bacon", "Bob", "A Movie");
        relationships.insertUndirected("Dartmouth", "Charlie", "B Movie");
        relationships.insertUndirected("Nobody", "Nobody's friend", "F Movie");

        System.out.println("The graph:");
        System.out.println(relationships);

        BFSLib b = new BFSLib();
        Graph<String,String> bfsTree = bfs(relationships,"Kevin Bacon");
        System.out.println(bfsTree);
        System.out.println(missingVertices(relationships,bfsTree));
        System.out.println(averageSeparation(bfsTree,"Kevin Bacon"));





    }

}


