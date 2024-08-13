package sim.railwaysim.view;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sim.railwaysim.model.WorldStatistics;

public class Statistics {
    private AnchorPane anchorPane;
    public Statistics(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    /**
     * This method is used to set statistics listeners
     */
    public void setStat(){
        Text trains = new Text("Trains: " + WorldStatistics.trainsProperty().intValue());
        trains.setFill(Color.DARKGREEN);
        WorldStatistics.trainsProperty().addListener((observableValue, oldValue, newValue) ->
                Platform.runLater(()-> trains.setText("Trains: " + newValue.intValue()))
        );


        Text pa = new Text("Passengers arrived: " + WorldStatistics.sizePAProperty().intValue());
        pa.setFill(Color.DARKGREEN);
        WorldStatistics.sizePAProperty().addListener((observableValue, oldValue, newValue) ->
                Platform.runLater(()-> pa.setText("Passengers arrived: " + newValue.intValue()))
        );

        Text stations = new Text("Stations: " + WorldStatistics.stationsProperty().intValue());
        stations.setFill(Color.DARKGREEN);
        WorldStatistics.stationsProperty().addListener((observableValue, oldValue, newValue) ->
                Platform.runLater(()-> stations.setText("Stations: " + newValue.intValue()))
        );

        Text lines = new Text("Lines: " + WorldStatistics.linesProperty().intValue());
        lines.setFill(Color.DARKGREEN);
        WorldStatistics.linesProperty().addListener((observableValue, oldValue, newValue) ->
                Platform.runLater(()-> lines.setText("Lines: " + newValue.intValue()))
        );
        VBox st = new VBox(10, stations, lines, trains, pa);
        anchorPane.getChildren().add(st);
        AnchorPane.setTopAnchor(st, 10.0);
        AnchorPane.setLeftAnchor(st, 10.0);
    }
}
