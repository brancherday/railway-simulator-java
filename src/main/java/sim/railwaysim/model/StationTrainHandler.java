package sim.railwaysim.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class that is called in other thread to emulate situation when train arrives at the station.
 *
 */
public class StationTrainHandler {
    private static final Logger logger = LogManager.getLogger(StationTrainHandler.class);
    private final StationModel station;
    private final TrainModel train;
    private final LineSimulation ln;

    /**
     * Only one constructor which supposed to be called in new thread.
     * @param station station where train arrived
     * @param train train which arrived
     * @param ln LineSimulation which represents line where train is moving.
     */
    public StationTrainHandler(StationModel station, TrainModel train, LineSimulation ln) {
        this.station = station;
        this.train = train;
        this.ln = ln;
        handle();
        ln.freeTrain(train);
    }

    /**
     * Private function which is called automatically when object of this class is created.
     * Emulates situation of tran arrival, blocks station when it's free then calls {@link StationTrainHandler#Disembark()}
     * and then {@link StationTrainHandler#Board()}
     */
    private void handle(){
        synchronized (station) {
            while (station.getState() == StationModel.statesStation.TAKEN) {
                try {
                    station.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.debug("Train arrived at the station " + station.getName() + " and setting state to taken");
            station.setState(StationModel.statesStation.TAKEN);
            logger.debug("Train arrived at the station " + station.getName() + " and state is taken");
        }
        int count = station.getPassengersWaiting().size();
        Random random = new Random();
        update();
        logger.debug("Train arrived at the station " + station.getName() + " and disembark");
        Disembark();
        //Simulating that it takes a time to disembark passengers
        try {
            Thread.sleep(random.nextInt(100*count + 50));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.debug("Train arrived at the station " + station.getName() + " and board");
        Board();
        //Simulating that it takes a time to board passengers
        try {
            Thread.sleep(random.nextInt(100*count + 50));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.debug("Train arrived at the station " + station.getName() + " and setting state to free");
        station.setState(StationModel.statesStation.FREE);
        logger.debug("Train arrived at the station " + station.getName() + " and state is free");
        logger.debug("Train left the station " + station.getName());

    }
    private void update(){
        ln.updateTrain(train);
    }

    /**
     * Checking if passengers has to get off at this station, if yes - getting off
     * Using when train has arrived at the station
     */
    private void Disembark(){
        ArrayList<Passenger> passengers_disembarked = new ArrayList<>();
        for(Passenger passenger : train.getPassengers()){
            if(passenger.getStopStation() == station){
                if(passenger.getDestination() == station){
                    passengers_disembarked.add(passenger);
                    logger.debug("i'm at my destination " + station.getName());
                    WorldStatistics.incrementPA();
                    continue;
                }
                //Adding passenger to station, setting his current station to this station, removing from train
                station.addToPassengersWaiting(passenger);
                passenger.setCurrStation(station);
                passengers_disembarked.add(passenger);
                logger.info("I disembarked at: " + station.getName());
            }
        }
        for (Passenger passenger : passengers_disembarked){
            train.removeFromPassengers(passenger);
        }
        logger.debug("Passengers disembarked from the trains to the station " + station.getName());
    }

    /**
     * Boards passengers from station to the train if needed
     */
    private void Board(){
        //iterating through each passenger and comparing their next stop values to trains values
        ArrayList<Passenger> passengers_boarded = new ArrayList<>();

        //Here is code iterates through every passenger
        for (Passenger passenger : station.getPassengersWaiting()){
            //Checks if lines has been deleted and then also calls pathfinder
            if(passenger.getNextStop() != null &&
                    !LinesSingleton.getInstance().getLines().contains(passenger.getNextStop().getLine())){
                passenger.setNextStop(null);
            }
            //Check if he has next stop, if not, then calls pathfinder to
            //find next stop to this passenger.
            if(passenger.getNextStop() == null){
                PathFinding pathFinding = new PathFinding();
                Stop stop = pathFinding.findPathOnce(passenger.getCurrStation(), passenger.getDestination(),
                        LinesSingleton.getInstance(), Graph.getInstance());
                passenger.setNextStop(stop);
            }
            //After that it decides it passenger has to board on the train.
            if
            (
            passenger.getNextStop()!=null &&
            passenger.getNextStop().getLine() == ln.getLine() &&
            passenger.getNextStop().getNextStation() ==
                    ln.getLine().getStations().get(ln.getLine().getStations().indexOf(station) + train.getDirection())
            )
            {
                train.addToPassengers(passenger);
                passenger.setStopStation(passenger.getNextStop().getLeave());
                passenger.setNextStop(null);
                passengers_boarded.add(passenger);
                logger.info("Passenger boarded!");
            }
        }
        for (Passenger passenger : passengers_boarded){
            station.removeFromPassengersWaiting(passenger);
        }
        logger.debug("Passangers boarded on the train from station " + station.getName());
    }
}
