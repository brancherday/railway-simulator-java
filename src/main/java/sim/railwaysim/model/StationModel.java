package sim.railwaysim.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

import static sim.railwaysim.model.StationModel.statesStation.FREE;

/**
 * This class is used to represent station model that has info about passengers that are currently on the station.
 * Comparable to compare where this station in order to create line.
 */
public class StationModel implements Comparable<StationModel>{
    /**
     * Needed to ensure data safety
     */
    public enum statesStation{
        TAKEN,
        FREE
    }
    private final String id;
    private final String name;
    //Using transient to exclude from serialisation
    transient private ArrayList<Passenger> passengersWaiting;
    private final double x;
    private final double y;
    private transient IntegerProperty sizeIP = new SimpleIntegerProperty();
    private transient statesStation state;

    public StationModel(String name, String id, double x, double y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        sizeIP.set(0);
        passengersWaiting = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getId() {
        return id;
    }
    public IntegerProperty sizeIPProperty() {
        return sizeIP;
    }

    public ArrayList<Passenger> getPassengersWaiting() {
        return passengersWaiting;
    }

    /**
     * Needed to initialize when loaded
     */
    public void transientInitialization(){
        if(passengersWaiting == null){
            passengersWaiting = new ArrayList<>();
        }
        this.sizeIP = new SimpleIntegerProperty();
        this.sizeIP.set(0);
        state = FREE;
    }

    public statesStation getState() {
        return state;
    }

    /**
     * Set's state of the station, needed to ensure that only one train is currently on the station and changes
     * its attributes
     * @param state State to provide
     */
    public synchronized void setState(statesStation state) {
        this.state = state;
        if(state == FREE)
            this.notifyAll();
    }

    /**
     * Adds to passenger waiting list, has to be called only when station is TAKEN by that class
     * @param passenger passenger to add
     */
    public void addToPassengersWaiting(Passenger passenger){
        this.passengersWaiting.add(passenger);
        this.sizeIP.set(passengersWaiting.size());
    }
    /**
     * Removes passenger from waiting list, has to be called only when station is TAKEN by that class
     * @param passenger passenger to remove
     */
    public void removeFromPassengersWaiting(Passenger passenger){
        this.passengersWaiting.remove(passenger);
        this.sizeIP.set(passengersWaiting.size());
    }

    @Override
    public int compareTo(StationModel o) {
        //Needed to compare distance from the start
        return Double.compare((x * x + y * y), (o.x * o.x + o.y * o.y));
    }
}
