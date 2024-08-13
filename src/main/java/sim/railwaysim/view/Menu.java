package sim.railwaysim.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sim.railwaysim.model.Const;

/**
 * Graphical representation of menu
 */
public class Menu {

    private final Button loadButton = new Button("Load");
    private final Button newButton = new Button("New");
    private final Button startButton = new Button("Start");
    Label text = new Label("Railway Simulator");
    private final VBox view = new VBox(20, text, loadButton, newButton, startButton);

    /**
     * Creates main menu with buttons to load game, start new game and start simulation.
     * @param stage Stage to set menu on
     */
    public void createMenu(Stage stage) {
        setButtonStyle(loadButton);
        setButtonStyle(startButton);
        setButtonStyle(newButton);
        setLabelStyle(text);
        view.setBackground(new Background(new BackgroundFill(Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        view.setAlignment(Pos.CENTER);
        Scene scene = new Scene(view, Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    private void setButtonStyle(Button button) {
        button.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(to bottom, #FFA500, #FF4500);" +
                        "-fx-pref-width: 200px;" +
                        "-fx-pref-height: 50px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(to bottom, #FF4500, #FFA500);" +
                        "-fx-pref-width: 200px;" +
                        "-fx-pref-height: 50px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(to bottom, #FFA500, #FF4500);" +
                        "-fx-pref-width: 200px;" +
                        "-fx-pref-height: 50px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        ));

    }

    private void setLabelStyle(Label label) {
        label.setStyle(
                "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #FFA500;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 4, 0.0, 0, 1);" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-padding: 10;"
        );
    }

    public Button getLoadButton() {
        return loadButton;
    }

    public Button getNewButton() {
        return newButton;
    }

    public Button getStartButton() {
        return startButton;
    }
}
