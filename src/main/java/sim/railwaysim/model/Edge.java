package sim.railwaysim.model;

/**
 * This class is representing Edge data type, has 2 links to stations and cost.
 */
public class Edge {
    private final StationModel from;
    private final StationModel to;
    private final Double cost;

    public Edge(StationModel from, StationModel to) {
        this.from = from;
        this.to = to;
        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();
        this.cost = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public StationModel getFrom() {
        return from;
    }

    public StationModel getTo() {
        return to;
    }

    public Double getCost() {
        return cost;
    }
}
