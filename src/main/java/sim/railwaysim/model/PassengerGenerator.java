package sim.railwaysim.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class PassengerGenerator {
    private final int timeToSpawn = 2000; //in milliseconds
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Graph graph;
    private LinesSingleton lines;
    private PathFinding pf;
    private volatile boolean running = true;
    private static final Logger logger = LogManager.getLogger(PassengerGenerator.class);
    /**
     * Will randomly spawn passengers on random stations.
     */
    private void spawn() {
        Random random = new Random();

        StationModel from;
        StationModel to;
        //Check to ensure that passenger will not spawn on his destination
        do {
            int indexFrom = random.nextInt(graph.getStationsList().size());
            int indexTo = random.nextInt(graph.getStationsList().size());
            from = graph.getStationsList().get(indexFrom);
            to = graph.getStationsList().get(indexTo);
        } while (from.equals(to));
        Passenger passenger = new Passenger(from, to);
        Stop stop = pf.findPathOnce(from, to, lines, graph);
        passenger.setNextStop(stop);
        //Adding passenger to station
        synchronized (from) {
            while (from.getState() == StationModel.statesStation.TAKEN) {
                try {
                    logger.debug("passenger generator waiting to passenger at" + from);
                    from.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.debug("spawning passenger at station " + from.getName() + " And changing state to TAKEN");
            from.setState(StationModel.statesStation.TAKEN);
        }
        from.addToPassengersWaiting(passenger);
        logger.debug("spawning passenger at station " + from.getName() + " And changing state to FREE");
        from.setState(StationModel.statesStation.FREE);
        logger.debug("spawning passenger at station " + from.getName() + " And changing state is freed");


    }
    /**
     * Starting generator that spawns passengers on random station in some peroid of time
     */
    public void generator(){
        executorService.submit(() -> {
            while (running) {
                Future<?> future = executorService.submit(this::spawn);
                try {
                    Thread.sleep(timeToSpawn);
                } catch (InterruptedException e) {
                    running = false;
                    future.cancel(true);
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public PassengerGenerator() {
        this.graph = Graph.getInstance();
        this.lines = LinesSingleton.getInstance();
        this.pf = new PathFinding();
    }

    /**
     * Service that is called when application is closing to close executor service properly.
     */
    public void stopService(){
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
                List<Runnable> droppedTasks = executorService.shutdownNow();
                System.err.println("Dropped " + droppedTasks.size() + " tasks");
            }
        } catch (InterruptedException e) {
            System.err.println("Termination interrupted");
        }
    }
}
