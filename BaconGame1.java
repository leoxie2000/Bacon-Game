import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class BaconGame1<V,E> {
    Graph<String, Set<String>> actorMovieGraph = new AdjacencyMapGraph<>();
    public String center;
    Map<String,Integer> averageSeperationMap = new HashMap<>();
    Map<String,String> IDActorMap = new HashMap<>();
    Map<String,String> IDMovieMap = new HashMap<>();
    Map<String, Set<String>> actorMovieMap = new HashMap<>();
    public int headcount = 0;
    ArrayList<String> commands = new ArrayList<>();

    public BaconGame1(){

    }

    //reading the actor file and fill in IDactor Map
    public void actorFileReading(String actorFile) throws Exception{
        BufferedReader f= new BufferedReader(new FileReader(actorFile));
        String line = f.readLine();

        try {
            while (line != null) {
                String[] allwords = line.split("\\|");
                IDActorMap.put(allwords[0], allwords[1]);
                headcount += 1;
                line = f.readLine();

            }
        }
        catch (Exception e){
            System.err.println(e);
        }
        finally {
            f.close();
        }

    }

    //reading the movie file and fill in IDmovie Map
    public void movieFileReading(String movieFile) throws Exception{
        BufferedReader f= new BufferedReader(new FileReader(movieFile));
        String line = f.readLine();

        try {
            while (line != null) {
                String[] allwords = line.split("\\|");
                IDMovieMap.put(allwords[0], allwords[1]);
                line = f.readLine();

            }
        }
        catch(Exception e){
            System.err.println(e);
        }
        finally {
            f.close();
        }

    }

    //building the main graph
    public void constructActorMovieGraph(String actorMovieFile) throws Exception{
        BufferedReader f= new BufferedReader(new FileReader(actorMovieFile));
        String line = f.readLine();

        try {
            while (line != null) {
                String[] allwords = line.split("\\|");
                String movieID = allwords[0];
                String actorID = allwords[1];
                String actorName = IDActorMap.get(actorID);
                String movieName = IDMovieMap.get(movieID);

                //inserting vertex into main graph
                if (!actorMovieGraph.hasVertex(actorName)) {
                    actorMovieGraph.insertVertex(actorName);
                }

                //building a map that maps moviename to actorname for later use
                if (!actorMovieMap.containsKey(movieName)) {
                    Set<String> actors = new HashSet<>();
                    actors.add(actorName);
                    actorMovieMap.put(movieName, actors);
                } else if (actorMovieMap.containsKey(movieName)) {
                    Set<String> actors = actorMovieMap.get(movieName);
                    actors.add(actorName);
                    actorMovieMap.put(movieName, actors);
                }

                line = f.readLine();

            }
        }
        catch (Exception e){
            System.err.println(e);
        }
        finally {
            f.close();
        }

        //loop through the map, pull two actors, and create an edge between them based on the movie
        for(String movie:actorMovieMap.keySet()){
            for(String actor1:actorMovieMap.get(movie)){
                for(String actor2:actorMovieMap.get(movie)){
                    if(actor1!=actor2){
                        Set<String> movies = new HashSet<>();
                        if(actorMovieGraph.hasEdge(actor1,actor2)){
                            movies = actorMovieGraph.getLabel(actor1,actor2);
                        }
                        else if(!actorMovieGraph.hasEdge(actor1,actor2)){
                            movies = new HashSet<>();
                        }
                        movies.add(movie);
                        actorMovieGraph.insertUndirected(actor1,actor2,movies);

                    }
                }
            }

        }

    }

    public void changeCenter(String name){
        if(actorMovieGraph.hasVertex(name))center = name;
    }


    //finding the average seperation based on the passed node
    public double findAverageSeperation(String rootnode){
        Graph<String,Set<String>> bfsTree = BFSLib.bfs(actorMovieGraph,rootnode);
        return BFSLib.averageSeparation(bfsTree,rootnode);
    }

    //finds shortest path from actor to center
    public List<String> findShortestPath(Graph<String,Set<String>> tree,String currentactor){
        return BFSLib.getPath(tree,currentactor);
    }

    //find out how many actors have path to the current center
    public int actorsHavePath(Graph<String,Set<String>> mainGraph, Graph<String,Set<String>> subGraph){
        int missing = BFSLib.missingVertices(mainGraph,subGraph).size();
        return mainGraph.numVertices()-missing;

    }

    //find the best bacons by how many degree each vertex has
    public List<String> FindBaconByDegree(Graph<String,Set<String>> g){
        List<String> actors = new ArrayList<>();
        for(String s: g.vertices()){
            actors.add(s);
        }
        actors.sort((String s1,String s2)->(int)(g.outDegree(s1)-g.outDegree(s2)));


        return actors;
    }

    // find the best bacons by each vertex's average separation
    public List<String> FindBaconBySeparation(Graph<String,Set<String>> g){
        List<String> actors = new ArrayList<>();
        for(String s: g.vertices()){
            actors.add(s);
        }
        actors.sort((String s1,String s2)->(int)(findAverageSeperation(s1)-findAverageSeperation(s2)));
        return actors;
    }


    // main game, recursively called to restart
    public void PlayGame(){

        //print the initialization
        System.out.println("Welcome to game, the current center is " + center);
        System.out.println("Choose one of the following commands");
        for(String s:commands) System.out.println(s);

        Scanner in = new Scanner(System.in);

            String line = in.nextLine();
            //Press C to change center
            if(line.equals("C")){
                System.out.println("Enter the name of the actor you want to be the center of universe");
                line=in.nextLine();
                if(actorMovieGraph.hasVertex(line)){
                    changeCenter(line);
                    System.out.println("The current center is now " + center);
                    PlayGame();
                }
                else{
                    System.out.println("Invalid name, Restarting the game");
                    PlayGame();
                }
            }
            //press A to find number of actors who have path to center
            if(line.equals("A")){
                int actors = actorsHavePath(actorMovieGraph,BFSLib.bfs(actorMovieGraph,center));
                System.out.println(actors+" actors have path to the current center: " +center);
                PlayGame();
            }

            //press P to find the path to current center from an actor
            if(line.equals("P")){
                System.out.println("Enter the actor that you want to find path to the current center");
                line = in.nextLine();
                if(actorMovieGraph.hasVertex(line)){

                    Graph<String,Set<String>> bfsTree = BFSLib.bfs(actorMovieGraph,center);
                    Set<String> missing = BFSLib.missingVertices(actorMovieGraph,bfsTree);
                    if(missing.contains(line)){
                        System.out.println("No path to current center");
                        PlayGame();
                    }
                    else {
                        System.out.println("Shortest path to " + center + " from " + line + " is " + findShortestPath(bfsTree, line));
                        PlayGame();
                    }
                }
                else{
                    System.out.println("Invalid name, try again");
                    PlayGame();
                }
            }

            //press F to find average separation of current center
            if(line.equals("F")){
                System.out.println(findAverageSeperation(center));
                PlayGame();
            }

            //press Q to quit
            if(line.equals("Q"))return;

            if(line.equals("degree")){
                List<String> universeList = FindBaconByDegree(actorMovieGraph);
                System.out.println("The Best Bacons by degree are");

                for(int i = universeList.size()-1; i >= universeList.size()-5;i--){
                    System.out.println(universeList.get(i));
                }
                PlayGame();

            }
            if(line.equals("separation")){
                Graph<String,Set<String>> bfsTree = BFSLib.bfs(actorMovieGraph,"Kevin Bacon");
                List<String> bfsList = FindBaconBySeparation(bfsTree);
                System.out.println("The Best Bacons by separation are");
                for(int i = bfsList.size()-1; i >= bfsList.size()-5;i--){
                    System.out.println(bfsList.get(i));
                }

                PlayGame();
            }

            else{
                System.out.println("wrong input, restarting");
                PlayGame();
            }


        }



    public static void main(String[] args) throws Exception{
        BaconGame1 game = new BaconGame1();
        game.actorFileReading("ps4/actors.txt");
        game.movieFileReading("ps4/movies.txt");
        game.constructActorMovieGraph("ps4/movie-actors.txt");

        //initializing
        game.center = "Kevin Bacon";
        game.commands.add("Enter C to change center of universe");
        game.commands.add("Enter A to find out how many actors have a path to the current center");
        game.commands.add("Enter P to find out the the shortest path to center from a specified actor");
        game.commands.add("Enter F to find the average path length over all actors who are connected by some path to the current center");
        game.commands.add("Enter Q to quit");
        game.commands.add("Enter degree to find new Bacon by average degree");
        game.commands.add("Enter separation to find new Bacon by average separation");

        game.PlayGame();



    }
}
