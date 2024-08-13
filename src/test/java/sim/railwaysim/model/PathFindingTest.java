package sim.railwaysim.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

public class PathFindingTest {
    private static Graph graph;
    private static LinesSingleton linesSingleton;

    private static PathFinding pathFinding;
    @BeforeAll
    public static void start(){
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

    @Test
    public void testNotConnectedLines(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);

        graph.addVertex(lnl);
        graph.addVertex(pnl);
        graph.addVertex(prhHl);

        graph.addEdge(new Edge(lnl, prhHl));
        Stop stop = pathFinding.findPathOnce(lnl, pnl, linesSingleton, graph);

        assertNull(stop, "Stop should be null there is no edge");
    }
    @Test
    public void testNoLine(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);

        graph.addVertex(lnl);
        graph.addVertex(pnl);
        graph.addVertex(prhHl);

        graph.addEdge(new Edge(lnl, prhHl));
        graph.addEdge(new Edge(pnl, prhHl));

        Stop stop = pathFinding.findPathOnce(lnl, pnl, linesSingleton, graph);

        assertNull(stop, "Stop shuld be null, there is no connected line");
    }

    @Test
    public void testNotInGraph(){
        StationModel lnl = new StationModel("Lysa Nad Labem", UUID.randomUUID().toString(), 108, 300);
        StationModel pnl = new StationModel("Prerov Nad Labem", UUID.randomUUID().toString(), 108, 365);
        StationModel prhHl = new StationModel("Praha Hl.n", UUID.randomUUID().toString(), 50, 287);

        graph.addVertex(lnl);
        graph.addVertex(pnl);

        graph.addEdge(new Edge(lnl, prhHl));
        graph.addEdge(new Edge(pnl, prhHl));

        Stop stop = pathFinding.findPathOnce(lnl, pnl, linesSingleton, graph);

        assertNull(stop, "Stop shuld be null, there is no station in graph");
    }

}
