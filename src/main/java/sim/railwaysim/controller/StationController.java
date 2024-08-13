package sim.railwaysim.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import sim.railwaysim.model.Edge;
import sim.railwaysim.model.Graph;
import sim.railwaysim.model.StationModel;
import sim.railwaysim.model.WorldStatistics;
import sim.railwaysim.view.StationView;

import java.util.HashSet;
import java.util.UUID;

/**
 * Station controller is used to communicate between station model and station view.
 */
public class StationController {
    private StationView stationView;
    private final Button addStationButton = new Button("Add Station");
    private final Button addEdgeButton = new Button("Add Edge");
    private Pane pane;
    private boolean isClickActive = false;

    /**
     * When called will initialize buttons Add station and Add edge, and initialize ability to click on stations to
     * choose them
     * @param stationView StationView object
     * @param pane Pane
     */
    public StationController(StationView stationView, Pane pane) {
        this.pane = pane;
        this.stationView = stationView;
        attachEventHandlers();
    }

    private void attachEventHandlers(){
        addStationButton.setOnAction(event -> {
            isClickActive = !isClickActive;
            addStationButton.setText(isClickActive ? "Click on map" : "Add Station");
        });
        pane.setOnMouseClicked(mouseEvent -> {
            if(isClickActive){
                //System.out.println("Mouse clicked at: " + mouseEvent.getX() + ", " + mouseEvent.getY());
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Adding station");
                dialog.setHeaderText("Input name of the station: ");

                dialog.showAndWait().ifPresent(response ->{
                    addStation(mouseEvent.getX(), mouseEvent.getY(), response);
                });
            }

        });
        BooleanBinding isSelectedTwoStations = Bindings.createBooleanBinding(()->stationView.getSelectedStations().size() != 2, stationView.getSelectedStations());
        addEdgeButton.disableProperty().bind(isSelectedTwoStations);
        addEdgeButton.setOnAction(event -> addEdge());
    }

    private void addStation(Double x, Double y, String name){
        StationModel stationModel = new StationModel(name, UUID.randomUUID().toString(), x, y);
        Graph.getInstance().addVertex(stationModel);
        WorldStatistics.incrementStations();
        stationView.drawStation(pane, stationModel);
    }

    private void addEdge(){
        StationModel from = stationView.getCircleStationMap().get(stationView.getSelectedStations().get(0));
        StationModel to = stationView.getCircleStationMap().get(stationView.getSelectedStations().get(1));
        Edge edge = new Edge(from, to);
        Graph.getInstance().addEdge(edge);
        stationView.drawEdge(pane, edge);
    }

    /**
     * Needed to be called to get HashSet of stations that are currently selected
     * @return HashSet of stations selected
     */
    public HashSet<StationModel> getSelectedStations(){
        HashSet<StationModel> stations = new HashSet<>();
        for(Circle circle : stationView.getSelectedStations()){
            stations.add(stationView.getCircleStationMap().get(circle));
        }
        return stations;
    }
    public ObservableList<Circle> getSelectedCircles(){
        return stationView.getSelectedStations();
    }

    public Button getAddStationButton() {
        return addStationButton;
    }

    public Button getAddEdgeButton() {
        return addEdgeButton;
    }
}
