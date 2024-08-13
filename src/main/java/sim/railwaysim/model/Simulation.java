package sim.railwaysim.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Main simulation class is used to asynchronously call tasks for the simulation, to start and pause the simulation.
 */
public class Simulation{
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger logger = LogManager.getLogger(Simulation.class);
    private Graph graph;
    private LinesSingleton lines;
    private HashMap<LineSimulation, Future<?>> linesRunning;
    private PassengerGenerator pg;

    public Simulation() {
        linesRunning = new HashMap<>();
    }

    /**
     * Loads simulation from lines.json and stationsEdges.json
     * @throws IOException
     */
    public void loadSim() throws IOException {
        if(graph != null && lines != null){
            return;
        }
        graph = GraphIO.load("src/main/resources/stationsEdges.json");
        this.lines = LinesIO.load("src/main/resources/lines.json", graph);
        if (graph != null) {
            logger.info("Stations Loaded");
            for (StationModel station : graph.getStationsList()) {
                logger.debug(station.getName() + " at (" + station.getX() + ", " + station.getY() + ")");
            }
            logger.info("Edges Loaded");
            for (Edge edge : graph.getEdgesList()) {
                logger.debug("From " + edge.getFrom().getName() + " to " + edge.getTo().getName() + " costs " + edge.getCost());
            }
            logger.debug(graph.getEdgesList().size());
        }
        if( this.lines != null){
            logger.info("Lines Loaded");
            for (LineModel line: this.lines.getLines()) {
                logger.debug("Line color is: " + line.getColor());
                logger.debug("Line start is: " + line.getStart().getName());
                for(StationModel st : line.getStations()){
                    logger.debug("Station on this line is: " + st.getName());
                }
            }
        }

    }

    /**
     * Creates new simulation
     * @throws IOException
     */
    public void createNewSim() throws IOException{
        graph = Graph.getInstance();
        lines = LinesSingleton.getInstance();
    }

    /**
     * Saves simulation to lines.json, stationEdges.json
     * @throws IOException
     */
    public void safeSim() throws IOException{
        GraphIO.safe(graph, "src/main/resources/stationsEdges.json");
        LinesIO.safe(lines, "src/main/resources/lines.json");
    }

    /**
     * Starts simulation, starts passenger generator
     */
    public void simulate(){
        assert lines != null;
        for(LineModel lineModel : lines.getLines()){
            runLine(lineModel);
        }
        this.pg = new PassengerGenerator();
        this.pg.generator();
    }

    /**
     * Creates lineSimulation object and starts it
     * @param lineModel LineModel object to create simulation with
     */
    public void runLine(LineModel lineModel){
        LineSimulation ls = new LineSimulation(lineModel);
        linesRunning.put(ls, executorService.submit(ls));
        logger.info(ls.getLine().getColor());
        WorldStatistics.incrementLines();
        if(!lines.getLines().contains(lineModel)){
            lines.getLines().add(lineModel);
        }
    }

    /**
     * Deletes line and it's simulation of given color
     * @param color color of line to delete
     */
    public void killLineSim(String color){
        Future<?> lineSim = null;
        LineSimulation ls = null;
        for(Map.Entry<LineSimulation, Future<?>> s : linesRunning.entrySet()){
            if(s.getKey().getLine().getColor().equalsIgnoreCase(color)){
                ls = s.getKey();
                lineSim =  s.getValue();
            }
        }
        if(lineSim == null){
            return;
        }
        WorldStatistics.decrementLines();
        lineSim.cancel(true);
        lines.getLines().remove(ls.getLine());
    }

    /**
     * Stops line simulations and passenger generator, closes all executorServices
     */
    public void stopService(){
        for(Map.Entry<LineSimulation, Future<?>> s : linesRunning.entrySet()){
            s.getValue().cancel(true);
        }
        executorService.shutdownNow();
        this.pg.stopService();
    }

    /**
     * Change speed of every line in simulation
     */
    public void changeSpeed(){
        for(LineSimulation lineSimulation : linesRunning.keySet()){
            lineSimulation.changeSpeed();
        }
    }
}
