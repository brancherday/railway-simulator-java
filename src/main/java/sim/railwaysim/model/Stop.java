package sim.railwaysim.model;
/**
 * Data type to store next stop for passenger
 * Contains line to take train on, nextStation to know right way to go, and station to get off
 */
public class Stop {
    private LineModel line;
    private StationModel nextStation;
    private StationModel leave;

    public Stop(LineModel line, StationModel nextStation, StationModel leave) {
        this.line = line;
        this.nextStation = nextStation;
        this.leave = leave;
    }

    public LineModel getLine() {
        return line;
    }

    public StationModel getNextStation() {
        return nextStation;
    }

    public StationModel getLeave() {
        return leave;
    }
}
