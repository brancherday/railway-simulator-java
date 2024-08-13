package sim.railwaysim.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


/**
 * This class is used by passenger to find its way to the destination, using A* firstly to find sequence
 * of stations that will be visited and then another algorithm to find stops.
 * Usage: create object of class and call findPath()
 */
public class PathFinding {
    public PathFinding() {
    }

    /** inside class needed only for A* algorithm
     * and path recreation
     */
    class EdgeInfo implements Comparable<EdgeInfo>{
        private Double cost;
        private StationModel parent;
        private Double heuristics;
        private Edge edge;

        public EdgeInfo(Double cost, StationModel parent, Double heuristics, Edge edge) {
            this.cost = cost;
            this.parent = parent;
            this.heuristics = heuristics;
            this.edge = edge;
        }

        public Double getCost() {
            return cost;
        }

        public StationModel getParent() {
            return parent;
        }

        public Double getHeuristics() {
            return heuristics;
        }

        public Edge getEdge() {
            return edge;
        }

        @Override
        public int compareTo(EdgeInfo other) {
            return Double.compare(this.cost + this.heuristics, other.cost + other.heuristics);
        }
    }

    /**
     * A* algorithm searching fastest approach from station to station
     * @param start start station
     * @param dst goal station
     * @param graph graph where these stations are
     * @return ArrayList with sequence of stations that are the shortest path from start to dst
     */
    private ArrayList<StationModel> aStar(StationModel start, StationModel dst, List<LineModel> lines, Graph graph){
        if(dst == start){
            return new ArrayList<>();
        }
        PriorityQueue<EdgeInfo> queue = new PriorityQueue<>();
        HashMap<StationModel, EdgeInfo> visited = new HashMap<>();
        StationModel current = start;
        StationModel parent;
        Double cost;
        visited.put(start, null);
        do{
            if(current != start){
                EdgeInfo currEdge = queue.poll();
                current = currEdge.getEdge().getTo();
                parent = currEdge.getEdge().getFrom();
                cost = currEdge.getCost();
                if(visited.get(current) == null || visited.get(current).getCost() > cost)
                    visited.put(current, new EdgeInfo(cost, parent, cost + currEdge.getHeuristics(), currEdge.getEdge()));
            }
            if(current == dst){
                return restorePath(visited, start, dst);
            }
            //Evaluating through every possible neighbour
            for (StationModel key: graph.getAdjacencyList().get(current).keySet()){
                Edge e = graph.edgeFinder(current, key);
                if(!visited.containsKey(e.getTo())){
                    Double heuristic = 0.0;
                    //At the start there is no current edge info, so it had to go with 0.0
                    if(visited.containsKey(current) && visited.get(current) != null){
                        heuristic = visited.get(current).getHeuristics();
                    }
                    queue.add(new EdgeInfo(e.getCost(), e.getFrom(), heuristic, e));


                }

            }
            current = null;
        }while (!queue.isEmpty());
        return null;
    }

    /**
     * Restores shortest path from visited map
     * @param visited HashMap with visited stations as keys and EdgeInfo as values
     * @param start starting station
     * @param dst goal station
     * @return ArrayList with sequence of stations that are the shortest path from start to dst
     */
    private ArrayList<StationModel> restorePath(HashMap<StationModel, EdgeInfo> visited,
                                                       StationModel start, StationModel dst){
        ArrayList<StationModel> stationsToVisit = new ArrayList<>();
        stationsToVisit.add(dst);
        EdgeInfo edgeInfo = visited.get(dst);
        StationModel parent = edgeInfo.getParent();
        stationsToVisit.add(0, parent);
        while (parent != start){
            edgeInfo = visited.get(parent);
            parent = edgeInfo.getParent();
            stationsToVisit.add(0, parent);
        }
        return stationsToVisit;
    }
    public ArrayList<Stop> findPath(StationModel start, StationModel dst, List<LineModel> lines, Graph graph){
        ArrayList<StationModel> stationsToVisit = aStar(start, dst, lines, graph);
        //TODO: catch here a possible error
        assert stationsToVisit != null;
        ArrayList<Stop> stops = new ArrayList<>();
        ArrayList<StationModel> seq = new ArrayList<>(stationsToVisit);
        ArrayList<StationModel> seqCp = new ArrayList<>();
        boolean hasFound = false;
        /*
        this cycle is work that way:
        first it has full sequence, tests if line has connection, if not
        it removes last station of the sequence and tests connection that contains fewer stations.
        It puts removed stations at the start of copy sequence for it to be the next sequence
        when the match has found, and then tests new sequence.
        It's needed to find each stop but counting from the
         */
        //TODO: Tests to cover when there is no path from start to dst and send it info back!
        while (true){
            /*
            Iterating through every line to find if it has a connection from start
            of the sequence to the end of the sequence
             */
            for (LineModel line : lines){
                if(line.getStations().containsAll(seq)){
                    //If stop has found then it's unnecessary to find other possible stops
                    //of the same length, because train will stop at needed station anyway
                    stops.add(new Stop(line, seq.get(1), seq.get(seq.size()-1)));
                    hasFound = true;
                    break;
                }
            }
            /*
            Adding last station of the sequence at the start of copy sequence, to get
            the next sequence to test. If seqCp contains only dst station, that means
            that final stop has been found and program can leave the cycle.
             */
            seqCp.add(0, seq.get(seq.size()-1));
            seq.remove(seq.size()-1);
            if(hasFound){
                hasFound = false;
                if(seqCp.get(0) == dst){
                    seq.clear();
                    break;
                }
                seq = new ArrayList<>(seqCp);
                seqCp.clear();
            }

        }
        return stops;
    }

    /**
     * Finds next Stop from start in order to get to destination
     * @param start Station where passenger currently at
     * @param dst Final destination
     * @param ls LinesSingleton object
     * @param graph Graph Object
     * @return next Stop
     */
    public Stop findPathOnce(StationModel start, StationModel dst, LinesSingleton ls, Graph graph){
        ArrayList<StationModel> stationsToVisit = aStar(start, dst, ls.getLines(), graph);
        if(stationsToVisit == null){
            return null;
        }
        Stop stop = null;
        ArrayList<StationModel> seq = new ArrayList<>(stationsToVisit);
        //Finds longest possible stop.
        //this cycle is work that way:
        //first it has full sequence, tests if line has connection, if not
        //it removes last station of the sequence and tests connection that contains fewer stations.
        while (seq.size() > 1 && stop == null){
            for (LineModel line : ls.getLines()) {
                if (line.getStations().containsAll(seq)) {
                    stop = new Stop(line, seq.get(1), seq.get(seq.size() - 1));
                    break;
                }
            }
            seq.remove(seq.size()-1);
        }
        return stop;
    }
}
