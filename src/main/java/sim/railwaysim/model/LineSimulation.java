package sim.railwaysim.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class will be called asynchronously in simulation class to simulate every line and trains on it.
 */
public class LineSimulation implements Runnable{
    ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger logger = LogManager.getLogger(LineSimulation.class);
    private final LineModel line;
    private double trainsSpeed = 0.15;
    private HashSet<TrainModel> trainsOnStations = new HashSet<>();
    private List<TrainMovement> tm = new CopyOnWriteArrayList<>();
    private class TrainMovement{
        private TrainModel train;
        private StationModel next;
        private StationModel prev;
        private final double boundary = 2.0;

        public TrainMovement(TrainModel train, StationModel next) {
            this.train = train;
            this.next = next;
            setPrev(new StationModel("Depot", "0", 0.0, 0.0));
        }

        /**
         * Move function, takes coords of the station and needed speed
         * @param speed speed of movement
         */
        public void move(double speed) {
            double deltaX = next.getX() - train.getX();
            double deltaY = next.getY() - train.getY();
            double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            double angleRadians = Math.atan2(deltaY, deltaX);
            double normDeltaX = deltaX / length;
            double normDeltaY = deltaY / length;
            synchronized (train){
                train.moveX(normDeltaX * speed);
                train.moveY(normDeltaY * speed);
                train.setAngle(Math.toDegrees(angleRadians));
            }

        }

        /**
         * Checks if train has arrived at the stations
         * @return true if arrived, false otherwise
         */
        public boolean isAtStation() {
            double distance = Math.sqrt(Math.pow(train.getX() - next.getX(), 2) + Math.pow(train.getY() - next.getY(), 2));
            return distance <= boundary;
        }

        public TrainModel getTrain() {
            return train;
        }

        public StationModel getNext() {
            return next;
        }

        public StationModel getPrev() {
            return prev;
        }

        public void setNext(StationModel next) {
            this.next = next;
        }

        public void setPrev(StationModel prev) {
            this.prev = prev;
        }

        /**
         * Finding next station at given line
         * @param sl LinkedList of stations on the line
         */
        public void findNext(LinkedList<StationModel> sl){
            if(sl.getLast() == next && sl.get(sl.indexOf(sl.getLast())-1) == prev){
                setNext(prev);
                setPrev(sl.getLast());
                //Needed to move forward and backward on the line, here it starts moving backward
                train.changeDirection();
                return;
            }
            if(sl.getFirst() == next && sl.get(sl.indexOf(sl.getFirst())+1) == prev){
                setNext(prev);
                setPrev(sl.getFirst());
                //here it starts moving forward
                train.changeDirection();
                return;
            }
            setPrev(next);
            setNext(sl.get(sl.indexOf(next)+train.getDirection()));
        }
    }

    public LineSimulation(LineModel line) {
        this.line = line;
        assert line!=null;
    }

    /**
     * Simulates movement of trains on the line
     */
    private void simulate(){
        while(true){
            for(TrainMovement trainM : tm){
                //If train is currently at the station then it skipped in movement
                if(trainsOnStations.contains(trainM.getTrain())){
                    continue;
                }
                //Checks if trains has arrived at the stations
                if(trainM.isAtStation()){
                    trainsOnStations.add(trainM.getTrain());
                    //Starts routine to disembark/board passengers to/fom station
                    executorService.submit(() -> new StationTrainHandler(trainM.getNext(), trainM.getTrain(), this));
                    continue;
                }
                trainM.move(trainsSpeed);
                logger.debug("Train coords: " + trainM.getTrain().getX() + " " + trainM.getTrain().getY());
                logger.debug("Moving to " + trainM.getNext().getName() + ". At coords : "
                        + trainM.getNext().getX() + " " + trainM.getNext().getY());
                if(!trainM.getTrain().getPassengers().isEmpty()){
                    logger.debug("Train moved from " + trainM.getPrev().getName() +
                            " towards station " + trainM.getNext().getName());
                    logger.debug("I've got " + trainM.getTrain().getPassengers().size() + " passengers on me!");
                }
            }
            addsDeletions();
            try {
                Thread.sleep((20));
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
                try {
                    stopService();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                return;
            }
        }
    }
    private void deleteTrain(TrainMovement trainMovement){
        tm.remove(trainMovement);
    }
    private void addTrain(TrainModel trainModel){
        tm.add(new TrainMovement(trainModel, line.getStart()));
    }

    /**
     * Checks if trains has been added/deleted on/from line and deletes objects of TrainMovement that controls
     * movement of the train on the line
     */
    private void addsDeletions(){
        if(line.getTrains().size() > tm.size()){
            boolean add;
            for(TrainModel trainModel : line.getTrains()){
                add = true;
                for (TrainMovement trainMovement : tm){
                    if(trainMovement.getTrain().equals(trainModel)){
                        add = false;
                        break;
                    }
                }
                if(add){
                    addTrain(trainModel);
                }
            }
        }
        if(line.getTrains().size() < tm.size()){
            boolean delete;
            for(TrainMovement trainMovement : tm){
                delete = true;
                for (TrainModel trainModel : line.getTrains()){
                    if(trainMovement.getTrain().equals(trainModel)){
                        delete = false;
                        break;
                    }
                }
                if(delete){
                    deleteTrain(trainMovement);
                }
            }
        }

    }

    /**
     * Updates info in TrainMovement to change next and previous station s
     * @param trainModel
     */
    public void updateTrain(TrainModel trainModel){
        for(TrainMovement t : tm){
            if(t.getTrain() == trainModel){
                t.findNext(line.getStations());
                break;
            }
        }
    }

    /**
     * Removes train from waiting list
     * @param trainModel TrainModel to remove
     */
    public void freeTrain(TrainModel trainModel){
        trainsOnStations.remove(trainModel);
    }

    public LineModel getLine() {
        return line;
    }

    @Override
    public void run() {
        simulate();
    }
    /**
     * Service that is called when application is closing to close executor service properly.
     */
    private void stopService() throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(4, TimeUnit.SECONDS)) {
            System.err.println("Executor did not terminate in the specified time.");
            List<Runnable> droppedTasks = executorService.shutdownNow();
            System.err.println("Dropped " + droppedTasks.size() + " tasks");
        }
    }

    public void changeSpeed() {
        this.trainsSpeed = (trainsSpeed + 0.5) % 1.5;
    }
}
