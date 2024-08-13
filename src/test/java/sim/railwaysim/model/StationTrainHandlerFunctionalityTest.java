package sim.railwaysim.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class StationTrainHandlerFunctionalityTest {
    private static Graph graph;
    private static LinesSingleton linesSingleton;
    private static PathFinding pathFinding;

    @BeforeAll
    public static void initialize(){
        graph = Graph.getInstance();
        linesSingleton = LinesSingleton.getInstance();
        pathFinding = new PathFinding();
    }

    @AfterEach
    public void clear(){
        graph.getStationsList().clear();
        graph.getAdjacencyList().clear();
        graph.getEdgesList().clear();

        linesSingleton.getLines().clear();
    }

    /**
     * Tests if passenger will disembark if according to condition it has to disembark
     */
    @Test
    public void testDisembark(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);
        StationModel kutM = new StationModel("Kutna hora mesto", UUID.randomUUID().toString(), 451, 350);

        graph.addVertex(lnl);
        graph.addVertex(pnl);
        graph.addVertex(prhHl);
        graph.addVertex(kutM);

        graph.addEdge(new Edge(prhHl, lnl));
        graph.addEdge(new Edge(prhHl, pnl));
        graph.addEdge(new Edge(kutM, lnl));

        HashSet<StationModel> set = new HashSet<>();
        set.add(kutM);
        set.add(lnl);
        LineModel green = new LineModel(set, "green");
        linesSingleton.getLines().add(green);

        set.clear();
        set.add(lnl);
        set.add(prhHl);
        LineModel red = new LineModel(set, "red");
        linesSingleton.getLines().add(red);

        Stop stop = pathFinding.findPathOnce(kutM, prhHl, linesSingleton, graph);

        Passenger passenger = new Passenger(kutM, prhHl);
        passenger.setNextStop(null);
        passenger.setStopStation(stop.getLeave());
        TrainModel train = green.addTrain();
        train.changeDirection();
        train.addToPassengers(passenger);

        StationTrainHandler stationTrainHandler = new StationTrainHandler(lnl, train, new LineSimulation(green));

        assertTrue(train.getPassengers().isEmpty() && lnl.getPassengersWaiting().size() == 1,
                "Passenger didn't disembark");
    }

    /**
     * Tests if passenger will board if it has to board according to conditions
     */
    @Test
    public void testBoard(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);
        StationModel kutM = new StationModel("Kutna hora mesto", UUID.randomUUID().toString(), 451, 350);

        graph.addVertex(lnl);
        graph.addVertex(pnl);
        graph.addVertex(prhHl);
        graph.addVertex(kutM);

        graph.addEdge(new Edge(prhHl, lnl));
        graph.addEdge(new Edge(prhHl, pnl));
        graph.addEdge(new Edge(kutM, lnl));

        HashSet<StationModel> set = new HashSet<>();
        set.add(kutM);
        set.add(lnl);
        LineModel green = new LineModel(set, "green");
        linesSingleton.getLines().add(green);

        set.clear();
        set.add(lnl);
        set.add(prhHl);
        LineModel red = new LineModel(set, "red");
        linesSingleton.getLines().add(red);

        Stop stop = pathFinding.findPathOnce(kutM, prhHl, linesSingleton, graph);

        Passenger passenger = new Passenger(kutM, prhHl);
        passenger.setNextStop(null);
        passenger.setStopStation(stop.getLeave());
        TrainModel train = green.addTrain();
        train.changeDirection();
        kutM.addToPassengersWaiting(passenger);

        StationTrainHandler stationTrainHandler = new StationTrainHandler(kutM, train, new LineSimulation(green));

        assertTrue(train.getPassengers().size() == 1 && kutM.getPassengersWaiting().isEmpty(),
                "Passenger didn't board");
    }
}
