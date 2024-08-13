package sim.railwaysim.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import sim.railwaysim.model.LineModel;
import sim.railwaysim.model.LinesSingleton;
import sim.railwaysim.model.Simulation;

import java.io.IOException;

/**
 * Now it's basic controller to connect view of button with simulation.
 */
public class SimulationController {
    private Simulation model;
    private TrainController trainController;
    private StationController stationController;
    private final Button killLine = new Button("Delete Line");
    private final Button addLine = new Button("Add Line");
    private final Button speed = new Button("Speed");
    private final Button safe = new Button("Safe");

    /**
     * Main simulation class constructor. When called will initialize all buttons and set them to anchorpane
     * @param anchorPane AnchorPane object to set buttons on
     * @param model Simulation class object
     * @param trainController TrainController object
     * @param stationController StationController object
     */
    public SimulationController(AnchorPane anchorPane, Simulation model, TrainController trainController, StationController stationController) {
        this.model = model;
        this.trainController = trainController;
        this.stationController = stationController;
        attachEventHandlers(anchorPane);
    }

    private void attachEventHandlers(AnchorPane anchorPane) {
        killLine.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Deleting line");
            dialog.setHeaderText("Input color of line: ");

            dialog.showAndWait().ifPresent(response ->{
                if(checkIfColor(response)){
                    while(trainController.deleteTrain(response));
                    model.killLineSim(response);
                }

            });
        });
        //Bind to off button to add line when less than 2 stations selected
        BooleanBinding isSelectedTwoStations = Bindings.createBooleanBinding(()->stationController.getSelectedCircles().size() < 2, stationController.getSelectedCircles());
        addLine.disableProperty().bind(isSelectedTwoStations);

        addLine.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Adding line");
            dialog.setHeaderText("Input color of line: ");

            dialog.showAndWait().ifPresent(response ->{

                if(checkIfColor(response)){
                    //Check if line with this color already exists
                    boolean exists = false;
                    for(LineModel lineModel : LinesSingleton.getInstance().getLines()){
                        if(lineModel.getColor().equalsIgnoreCase(response)){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists){
                        LineModel lineModel = new LineModel(stationController.getSelectedStations(), response);
                        if(lineModel.getStart() == null){
                            Alert aler = new Alert(Alert.AlertType.ERROR);
                            aler.setContentText("Cannot create line of given color: " + response +
                                    ". Due to the fact that some stations aren't connected. Choose only connected stations");
                            aler.showAndWait();
                        }
                        else {
                            model.runLine(lineModel);
                        }

                    }

                }

            });
        });

        speed.setOnAction(event -> {
            model.changeSpeed();
        });
        //Button to safe simulation
        safe.setOnAction(event -> {
            try {
                model.safeSim();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        VBox box = new VBox(10, trainController.getAddTrainButton(), trainController.getDeleteTrainButton(),
                stationController.getAddStationButton(), stationController.getAddEdgeButton(), addLine, killLine, speed, safe);
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, new Insets(6))));
        box.setPadding(new Insets(10));
        setButtonStyle(addLine);
        setButtonStyle(killLine);
        setButtonStyle(trainController.getAddTrainButton());
        setButtonStyle(trainController.getDeleteTrainButton());
        setButtonStyle(stationController.getAddStationButton());
        setButtonStyle(stationController.getAddEdgeButton());
        setButtonStyle(speed);
        setButtonStyle(safe);

        anchorPane.getChildren().addAll(box);
        AnchorPane.setTopAnchor(box, 10.0);
        AnchorPane.setRightAnchor(box, 10.0);
    }

    /**
     * Loads last saved simulation
     */
    public void load() {
        try {
            model.loadSim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts new clear simulation
     */
    public void newGame(){
        try {
            model.createNewSim();
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    /**
     * Sets style and muse listeners of the buttons using css
     * @param button button to set style on
     */
    private void setButtonStyle(Button button) {
        button.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(to bottom, #FFA500, #FF4500);" +
                        "-fx-pref-width: 80px;" +
                        "-fx-pref-height: 16px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(to bottom, #FF4500, #FFA500);" +
                        "-fx-pref-width: 80px;" +
                        "-fx-pref-height: 16px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(to bottom, #FFA500, #FF4500);" +
                        "-fx-pref-width: 80px;" +
                        "-fx-pref-height: 16px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        ));
    }

    /**
     * Checks if provided string is a color
     * @param response string provided via text input
     * @return true if response is really a color, false otherwise
     */
    private boolean checkIfColor(String response){
        boolean i = true;
        try {
            Color.valueOf(response);
        }
        catch (Exception e){
            Alert aler = new Alert(Alert.AlertType.ERROR);
            aler.setContentText("There is no line with such color: " + response + ". Choose another!");
            i = false;
            aler.showAndWait();
        }
        return i;
    }

}
