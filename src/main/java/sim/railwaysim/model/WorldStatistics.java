package sim.railwaysim.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * This class is used to store world statistics
 */
public class WorldStatistics {
    private static IntegerProperty passengersArrived = new SimpleIntegerProperty(0);
    private static IntegerProperty trains = new SimpleIntegerProperty(0);
    private static IntegerProperty stations = new SimpleIntegerProperty(0);
    private static IntegerProperty lines = new SimpleIntegerProperty(0);

    public static IntegerProperty stationsProperty() {
        return stations;
    }

    public synchronized static void incrementStations() {
        WorldStatistics.stations.set(stations.get() + 1);
    }

    public static IntegerProperty linesProperty() {
        return lines;
    }

    public synchronized static void incrementLines() {
        WorldStatistics.lines.set(lines.get()+1);
    }
    public synchronized static void decrementLines() {
        WorldStatistics.lines.set(lines.get()-1);
    }

    public static IntegerProperty sizePAProperty() {
        return passengersArrived;
    }

    public synchronized static void incrementPA() {
        passengersArrived.set(passengersArrived.get() + 1);
    }

    public static IntegerProperty trainsProperty() {
        return trains;
    }

    public synchronized static void incrementTrains() {
        WorldStatistics.trains.set(trains.get() + 1);
    }
    public synchronized static void decrementTrains() {
        WorldStatistics.trains.set(trains.get() - 1);
    }
}
