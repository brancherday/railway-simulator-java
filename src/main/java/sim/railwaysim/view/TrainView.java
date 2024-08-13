package sim.railwaysim.view;

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sim.railwaysim.model.TrainModel;

import java.util.HashMap;

/**
 * Takes info from train controller and paints it
 */
public class TrainView {
    private HashMap<TrainModel, Rectangle> trainRect;
    private final Double width = 10.0;
    private final Double height = 6.0;
    private Pane pane;

    /**
     * Updates coordinates of rectangle to draw it where train currently is
     * @param trainModel TrainModel to draw
     * @param x coord
     * @param y coord
     * @param v angle of moving
     */
    public void update(TrainModel trainModel, Double x, Double y, Double v){
        Rectangle rect = trainRect.get(trainModel);
        if(rect == null){
            return;
        }
        rect.setX(x-width/2);
        rect.setY(y-height/2);
        rect.setRotate(v);
    }

    /**
     * Draws rectangle of the given train, with the given color and coordinates
     * @param trainModel Train to assign rectangle to
     * @param color Color of line to fill rectangle with
     * @param x coord
     * @param y coord
     */
    public void addTrainRect(TrainModel trainModel, Color color, Double x, Double y){
        Rectangle rect = new Rectangle(x-width/2, y-height/2, width, height);
        rect.setFill(color);
        trainRect.putIfAbsent(trainModel, rect);
        //Tooltip to show how many passengers are on the train
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Passengers 0");
        trainModel.sizeIPProperty().addListener((observable, oldValue, newValue)->
                Platform.runLater(()->tooltip.setText("Passengers " + newValue.intValue())));

        Tooltip.install(rect, tooltip);
        tooltip.setShowDelay(Duration.millis(10));
        pane.getChildren().add(rect);
    }

    /**
     * Deletes rectangle assigned to train
     * @param trainModel TrainModel object to delete
     */
    public void deleteTrainRect(TrainModel trainModel){
        Rectangle r = trainRect.remove(trainModel);
        assert r != null;
        Platform.runLater(()->
                pane.getChildren().remove(r));
        pane.requestLayout();
    }

    public TrainView(Pane pane) {
        trainRect = new HashMap<>();
        this.pane = pane;
    }
}
