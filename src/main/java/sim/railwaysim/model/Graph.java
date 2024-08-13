package sim.railwaysim.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class used as graph data type. Provides storage for graph adjacency list, stations list and edges list.
 * Also provides functionality to build up graph as a structure.
 */
public class Graph {
    private static transient Graph graph;
    private ArrayList<StationModel> stationsList;
    private ArrayList<Edge> edgesList;
    private transient HashMap<StationModel, HashMap<StationModel, Double>> adjacencyList;
    private Graph() {
        this.adjacencyList = new HashMap<>();
        this.stationsList = new ArrayList<>();
        this.edgesList = new ArrayList<>();
    }

    /**
     * Returns instance of graph
     * @return Graph object
     */
    public static Graph getInstance(){
        if(graph == null){
            graph = new Graph();
        }
        return graph;
    }

    /**
     * Adds edge to edge list and to adjacency list
     * @param edge Edge object to add
     */
    public void addEdge(Edge edge){
        if (!edgesList.contains(edge)){
            edgesList.add(edge);
            edgesList.add(new Edge(edge.getTo(), edge.getFrom()));
        }
        adjacencyList.putIfAbsent(edge.getTo(), new HashMap<>());
        adjacencyList.putIfAbsent(edge.getFrom(), new HashMap<>());
        adjacencyList.get(edge.getFrom()).put(edge.getTo(), edge.getCost());
        adjacencyList.get(edge.getTo()).put(edge.getFrom(), edge.getCost());
    }

    /**
     * Needed to set singleton graph when loaded from file. Because constructor isn't called when deserializes.
     * @param g Loaded graph object
     */
    public void initializeDeserialization(Graph g){
        graph = g;
        WorldStatistics.stationsProperty().set(graph.stationsList.size());
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Adds station to stationlist
     * @param station StationModel object to add
     */
    public synchronized void addVertex(StationModel station){
        adjacencyList.putIfAbsent(station, new HashMap<>());
        if (!stationsList.contains(station)){
            stationsList.add(station);
        }
    }

    /**
     * Finds edge
     * @param from StationModel from
     * @param to StationModel to
     * @return returns edge if existed, otherwise null
     */
    public Edge edgeFinder(StationModel from, StationModel to){
        for(Edge edge : edgesList){
            if(edge.getFrom() == from && edge.getTo() == to){
                return edge;
            }
        }
        return null;
    }

    public ArrayList<Edge> getEdgesList() {
        return edgesList;
    }

    public ArrayList<StationModel> getStationsList() {
        return stationsList;
    }

    public HashMap<StationModel, HashMap<StationModel, Double>> getAdjacencyList() {
        return adjacencyList;
    }
}

