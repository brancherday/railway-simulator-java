package sim.railwaysim.model;
/**
 * Class passenger is used to represent passenger in the simulation
 * it has info about current station where it currently is,
 * destination - where it wants to get to,
 * Stop he has to take in order to get to the next station to get to destination,
 * info about which station to leave if it's currently in the train
 */
public class Passenger {
    private StationModel currStation;
    private final StationModel destination;
    private StationModel stopStation;
    private Stop nextStop;
    public Stop getNextStop() {
        return nextStop;
    }

    public void setNextStop(Stop nextStop) {
        this.nextStop = nextStop;
    }

    public Passenger(StationModel currStation, StationModel destination) {
        this.currStation = currStation;
        this.destination = destination;
    }

    public StationModel getCurrStation() {
        return currStation;
    }

    public StationModel getDestination() {
        return destination;
    }


    public StationModel getStopStation() {
        return stopStation;
    }

    public void setStopStation(StationModel stopStation) {
        this.stopStation = stopStation;
    }

    public void setCurrStation(StationModel currStation) {
        this.currStation = currStation;
    }
}
