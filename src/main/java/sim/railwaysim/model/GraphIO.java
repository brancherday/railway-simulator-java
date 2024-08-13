package sim.railwaysim.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to save and load graph to/from json file
 */
public class GraphIO {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves graph to file at given file name
     * @param graph Graph object to safe
     * @param filename String of filename
     * @throws IOException if can't save
     */
    public static void safe(Graph graph, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(graph, writer);
        }
    }

    /**
     * Loads graph(Stations, Edges) from given file
     * @param filename File to load from
     * @return Graph object
     * @throws IOException if can't load
     */
    public static Graph load(String filename) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(Paths.get(filename).toFile())) {
            Graph graph = gson.fromJson(reader, Graph.class);
            graph.initializeDeserialization(graph);
            // Creating map with ids and station for faster usage
            Map<String, StationModel> stationMap = new HashMap<>();
            for (StationModel station : graph.getStationsList()) {
                stationMap.put(station.getId(), station);
                station.transientInitialization();
            }

            // creating new edges list to change the old one because after loading it
            //created new objects in memory, but we need objects that are the same as in stationList
            List<Edge> edges = new ArrayList<>();
            for (Edge edge : graph.getEdgesList()) {
                StationModel fromStation = stationMap.get(edge.getFrom().getId());
                StationModel toStation = stationMap.get(edge.getTo().getId());
                if (fromStation != null && toStation != null) {
                    edges.add(new Edge(fromStation, toStation)); //
                }
            }

            //Inserting edges that were created from existing stations into graph edgesList
            //and clearing it beforehand
            graph.getEdgesList().clear();
            for (Edge edge : edges){
                graph.addEdge(edge);
            }
            return graph;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
