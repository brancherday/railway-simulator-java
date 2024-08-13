package sim.railwaysim.model;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is used to represent line model, that has list of stations and trains that are on that line,
 * has its own color and provides interface to communicate with that properties.
 */
public class LineModel {
    private LinkedList<StationModel> stations;
    private transient List<TrainModel> trains;
    private final String color;
    private StationModel start;

    public LineModel(HashSet<StationModel> stationsList, String color) {
        this.trains = new CopyOnWriteArrayList<>();
        this.color = color;
        createLine(stationsList);
    }

    /** This method creates line. Choosing which station to put next
     * it uses distance between stations and presence in adjacency list in graph
     * @param stationsList HashSet with stations which has to be on the current line
     */
    public void createLine(HashSet<StationModel> stationsList){
        Graph g = Graph.getInstance();
        assert(g !=null);
        //PQ only needed to find the closest station to the start
        PriorityQueue<StationModel> pqSt = new PriorityQueue<>();
        this.trains = new CopyOnWriteArrayList<>();
        pqSt.addAll(stationsList);
        stations = new LinkedList<>();
        StationModel current = pqSt.poll();
        start = current;
        HashSet<StationModel> visited = new HashSet<>();
        //Checking which to put next according to adjacency list
        while (stations.size() != stationsList.size()){
            if(current == null){
                //Check to control if it's possible to connect all given stations
                if(pqSt.isEmpty()){
                    start = null;
                    break;
                }
                current = pqSt.poll();
                stations = new LinkedList<>();
                start = current;
                visited.clear();
            }
            stations.add(current);
            visited.add(current);
            HashMap<StationModel, Double> neighbors = g.getAdjacencyList().get(current);
            double minDistance = Double.MAX_VALUE;
            StationModel next = null;
            //Finding neighbours to find next station to add to LinkedList
            for (HashMap.Entry<StationModel, Double> entry : neighbors.entrySet()) {
                if (!visited.contains(entry.getKey()) && entry.getValue() < minDistance && stationsList.contains(entry.getKey())) {
                    next = entry.getKey();
                    minDistance = entry.getValue();
                }
            }
            current = next;
        }
    }
    public StationModel getStart() {
        return start;
    }

    public void setStart(StationModel start) {
        this.start = start;
    }

    public void setStations(LinkedList<StationModel> stations) {
        this.stations = stations;
    }

    public LinkedList<StationModel> getStations() {
        return stations;
    }

    public String getColor() {
        return color;
    }

    public List<TrainModel> getTrains() {
        return trains;
    }

    /**
     * Adds train to line
     * @return TrainModel object
     */
    public TrainModel addTrain(){
        TrainModel trainModel = new TrainModel(start.getX(), start.getY());
        this.trains.add(trainModel);
        WorldStatistics.incrementTrains();
        return trainModel;
    }

    /**
     * Deletes random train object from line
     * @return Deleted TrainObject
     */
    public TrainModel deleteTrain(){
        Random random = new Random();
        if(getTrains().isEmpty()){
            return null;
        }
        int trD = random.nextInt(getTrains().size());
        WorldStatistics.decrementTrains();
        return trains.remove(trD);
    }
}

