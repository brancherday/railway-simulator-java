package sim.railwaysim.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sim.railwaysim.model.LineModel;
import sim.railwaysim.model.LinesSingleton;
import sim.railwaysim.model.TrainModel;
import sim.railwaysim.view.TrainView;

/**
 * Class is used to communicate between train model and train view classes, takes information about train and
 * provides it to train view.
 */
public class TrainController {
    private TrainView trainView;
    private LinesSingleton ls;
    private Pane pane;
    private final Button addTrainButton = new Button("Add Train");
    private final Button deleteTrainButton = new Button("Delete Train");

    /**
     * When called will initialize buttons to add and delete train
     * @param trainView TrainView object
     * @param pane Pane
     */
    public TrainController(TrainView trainView, Pane pane) {
        this.trainView = trainView;
        this.pane = pane;
        attachEventHandlers();
    }
    private void attachEventHandlers(){
        addTrainButton.setOnAction(event->{
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Adding train");
            dialog.setHeaderText("Input color of the line:");

            dialog.showAndWait().ifPresent(response -> {
                if(!addTrain(response)){
                    Alert aler = new Alert(Alert.AlertType.ERROR);
                    aler.setContentText("There is no line with such color: " + response + ". Choose another!");
                    aler.showAndWait();
                }
            });
        });

        deleteTrainButton.setOnAction(event->{
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Deleting train");
            dialog.setHeaderText("Input color of the line:");

            dialog.showAndWait().ifPresent(response -> {
                if(!deleteTrain(response)){
                    Alert aler = new Alert(Alert.AlertType.ERROR);
                    aler.setContentText("There is no line with such color: " + response + ". Or there are no trains on the line");
                    aler.showAndWait();
                }
            });
        });
    }

    /**
     * Will start animation of trains, call only when simulation has started
     */
    public void render(){
        ls = LinesSingleton.getInstance();
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                for(LineModel line : ls.getLines()){
                    for(TrainModel train : line.getTrains()){
                        Platform.runLater(()->{
                            synchronized (train){
                                trainView.update(train, train.getX(), train.getY(), train.getAngle());
                            }
                        });
                    }
                }
            }
        }.start();
    }

    /**
     * Will add train to simulation via button
     * @param color Color of line that passed through dialog
     * @return true if line exists, false otherwise
     */
    private boolean addTrain(String color){
        boolean ret = false;
        if(color.equalsIgnoreCase("all")){
            for (LineModel lineModel : ls.getLines()){
                TrainModel trainModel = lineModel.addTrain();
                trainView.addTrainRect(trainModel, Color.valueOf(lineModel.getColor()), trainModel.getX(), trainModel.getY());
            }
            return true;
        }
        for(LineModel lineModel : ls.getLines()){
            if(lineModel.getColor().equalsIgnoreCase(color)){
                ret = true;
                TrainModel trainModel = lineModel.addTrain();
                trainView.addTrainRect(trainModel, Color.valueOf(lineModel.getColor()), trainModel.getX(), trainModel.getY());
                break;
            }
        }
        return ret;
    }

    /**
     * Deletes train from line of given color
     * @param color Color of line to delete via button
     * @return true if line exists, false otherwise
     */
    public boolean deleteTrain(String color){
        boolean ret = false;
        for (LineModel lineModel: ls.getLines()){
            if(lineModel.getColor().equalsIgnoreCase(color)){
                if(!lineModel.getTrains().isEmpty()){
                    ret = true;
                    trainView.deleteTrainRect(lineModel.deleteTrain());
                }
                break;
            }
        }
        return ret;
    }

    public Button getAddTrainButton() {
        return addTrainButton;
    }

    public Button getDeleteTrainButton() {
        return deleteTrainButton;
    }
}
