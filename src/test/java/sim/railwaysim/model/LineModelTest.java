package sim.railwaysim.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

public class LineModelTest {
    private static Graph graph;

    @BeforeAll
    public static void initialize(){
        graph = Graph.getInstance();
    }

    @AfterEach
    public void clear(){
        graph.getEdgesList().clear();
        graph.getStationsList().clear();
        graph.getAdjacencyList().clear();
    }
    @Test
    public void testUnconnectedStations(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);

        graph.addVertex(lnl);
        graph.addVertex(pnl);
        graph.addVertex(prhHl);

        HashSet<StationModel> set = new HashSet<>(graph.getStationsList());

        LineModel lineModel = new LineModel(set, "green");
        assertNull(lineModel.getStart(), "Line model should have start null");
    }

    @Test
    public void testConnectedStationsButLessStations(){
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

        HashSet<StationModel> set = new HashSet<>(graph.getStationsList());
        set.remove(lnl);
        set.remove(pnl);

        LineModel lineModel = new LineModel(set, "green");
        assertNull(lineModel.getStart(), "Line model should have start null");
    }

    /**
     * This test covers case when line have to cover three or more ways from one statipn
     */
    @Test
    public void testConnectedStationsAndFork(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);
        StationModel kutM = new StationModel("Kutna hora mesto", UUID.randomUUID().toString(), 451, 350);

        graph.addVertex(lnl);
        graph.addVertex(pnl);
        graph.addVertex(prhHl);
        graph.addVertex(kutM);

        graph.addEdge(new Edge(prhHl, lnl));
        graph.addEdge(new Edge(kutM, lnl));
        graph.addEdge(new Edge(pnl, lnl));

        HashSet<StationModel> set = new HashSet<>(graph.getStationsList());

        LineModel lineModel = new LineModel(set, "green");
        assertNull(lineModel.getStart(), "Line model should have start null");
    }

    @Test
    public void testDeleteTrainEmpty(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);

        graph.addVertex(lnl);
        graph.addVertex(pnl);

        graph.addEdge(new Edge(pnl, lnl));

        HashSet<StationModel> set = new HashSet<>(graph.getStationsList());

        LineModel lineModel = new LineModel(set, "green");
        assertNull(lineModel.deleteTrain(), "Line model should have start null");
    }

}
