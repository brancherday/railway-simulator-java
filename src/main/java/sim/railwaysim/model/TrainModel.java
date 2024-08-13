package sim.railwaysim.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

/**
 * Train model class is used to represent train object that has information about passengers that are on that train,
 * about where to go next and its current coordinates.
 */
public class TrainModel {
    private ArrayList<Passenger> passengers;
    private double x;
    private double y;
    //Angle where the train is facing needed to draw it facing right direction
    private double angle;
    //Defines where train has to move at line linked list of stations. Maybe change it, but it will be hard
    //to get right direction at board class.
    private int direction;
    private transient IntegerProperty sizeIP = new SimpleIntegerProperty();

    public TrainModel(double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.direction = 1;
        passengers = new ArrayList<>();
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void moveX(double x) {
        this.x += x;
    }

    public void moveY(double y) {
        this.y += y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }

    /**
     * Adds passenger to the train
     * @param passenger Passenger object to add
     */
    public void addToPassengers(Passenger passenger){
        this.passengers.add(passenger);
        this.setSizeIP(passengers.size());
    }

    /**
     * Removes passenger from the train
     * @param passenger Passenger object to remove
     */
    public void removeFromPassengers(Passenger passenger){
        this.passengers.remove(passenger);
        this.setSizeIP(passengers.size());
    }

    public int getDirection() {
        return direction;
    }

    /**
     * Changes direction, either 1 or -1, defines which way on the line train is moving
     */
    public void changeDirection(){
        this.direction = (-1) * this.direction;
    }

    public IntegerProperty sizeIPProperty() {
        return sizeIP;
    }

    public void setSizeIP(int sizeIP) {
        this.sizeIP.set(sizeIP);
    }
}
