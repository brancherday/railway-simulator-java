package sim.railwaysim.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import sim.railwaysim.model.Edge;
import sim.railwaysim.model.Graph;
import sim.railwaysim.model.StationModel;

import java.util.HashMap;

/**
 * Takes info from station controller and paints it
 */
public class StationView {
    private HashMap<Circle, StationModel> circleStationMap;
    private ObservableList<Circle> selectedStations;

    public StationView() {
        this.selectedStations = FXCollections.observableArrayList();
        this.circleStationMap = new HashMap<>();
    }

    /**
     * Draws edges and stations when loading game from file
     * @param pane
     */
    public void render(Pane pane){
        for(Edge edge : Graph.getInstance().getEdgesList()){
            drawEdge(pane, edge);
        }
        for(StationModel stationModel: Graph.getInstance().getStationsList()){
            drawStation(pane, stationModel);
        }

    }

    /**
     * Draws station circle on a given pane for a given station
     * @param pane Pane object to draw on
     * @param stationModel StationModel of the station to draw
     */
    public void drawStation(Pane pane, StationModel stationModel){
        Circle circle = new Circle(4);
        circle.setCenterX(stationModel.getX());
        circle.setCenterY(stationModel.getY());
        circle.setStrokeWidth(0);
        circle.setOnMouseClicked(this::select);
        Text capacity = new Text((String.valueOf(stationModel.sizeIPProperty().intValue())));
        capacity.setFill(Color.DARKGREEN);
        capacity.setX(stationModel.getX()-4);
        capacity.setY(stationModel.getY()+16);
        Text name = new Text(stationModel.getName());
        name.setFill(Color.DARKGREEN);
        name.setX(stationModel.getX());
        name.setY(stationModel.getY()-6);
        stationModel.sizeIPProperty().addListener((observable, oldValue, newValue)->
                Platform.runLater(()->capacity.setText(String.valueOf(newValue.intValue()))
                ));
        pane.getChildren().addAll(circle, capacity, name);
        circleStationMap.putIfAbsent(circle,stationModel);
    }

    /**
     * Draws Line as the edge from two stations
     * @param pane Pane to draw on
     * @param edge Edge of two stations
     */
    public void drawEdge(Pane pane, Edge edge){
        Line line = new Line();
        StationModel from = edge.getFrom();
        StationModel to = edge.getTo();
        line.setStartX(from.getX());
        line.setStartY(from.getY());
        line.setEndX(to.getX());
        line.setEndY(to.getY());
        pane.getChildren().addAll(line);
    }

    /**
     * Adds station to selected when clicked on
     * @param mouseEvent
     */
    private void select(MouseEvent mouseEvent){
        Circle circle = (Circle) mouseEvent.getSource();

        if(selectedStations.contains(circle)){
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(0);
            selectedStations.remove(circle);
        }
        else {
            circle.setStroke(Color.RED);
            circle.setStrokeWidth(2);
            selectedStations.add(circle);
        }
    }

    public HashMap<Circle, StationModel> getCircleStationMap() {
        return circleStationMap;
    }

    public ObservableList<Circle> getSelectedStations() {
        return selectedStations;
    }
}
