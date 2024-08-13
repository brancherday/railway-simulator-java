package sim.railwaysim.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.railwaysim.controller.SimulationController;
import sim.railwaysim.controller.StationController;
import sim.railwaysim.controller.TrainController;
import sim.railwaysim.model.Const;
import sim.railwaysim.model.Simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main javafx class that implements application
 */
public class Main extends Application {
    double scaleValue = 1.0;
    private static Logger logger;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    @Override
    public void start(Stage stage) throws Exception {

        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("Railway Simulator");

        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setPrefSize(Const.WORLD_WIDTH, Const.WORLD_HEIGHT);

        Simulation simulation = new Simulation();
        TrainView trainView = new TrainView(root);
        TrainController trainController = new TrainController(trainView, root);
        StationView stationView = new StationView();

        Menu menu = new Menu();
        menu.createMenu(stage);

        StationController stationController = new StationController(stationView, root);

        Pane zoomPane = new Pane(root);
        Scale scaleTransform = new Scale(scaleValue, scaleValue, 0, 0);
        zoomPane.getTransforms().add(scaleTransform);

        ScrollPane scrollPane = new ScrollPane(zoomPane);
        scrollPane.setPannable(true);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPickOnBounds(false);
        StackPane stackPane = new StackPane(scrollPane, anchorPane);

        Scene scene2 = new Scene(stackPane, Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
        SimulationController sc = new SimulationController(anchorPane,  simulation, trainController, stationController);
        //Zoom in/out
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() > 0) {
                    scaleValue -= 0.005;
                } else {
                    scaleValue += 0.005;
                }
                scaleValue = Math.max(0.4, scaleValue);
                scaleTransform.setX(scaleValue);
                scaleTransform.setY(scaleValue);
                event.consume();
            }
        });

        Statistics statistics = new Statistics(anchorPane);
        //Starting of the simulation
        menu.getStartButton().setOnAction(e ->{
            logger.info("Starting");
            statistics.setStat();
            stage.setScene(scene2);
            stationView.render(root);
            executorService.submit(simulation::simulate);
            trainController.render();
            stage.setOnCloseRequest(event -> {simulation.stopService();
                executorService.shutdownNow();});
        } );

        menu.getLoadButton().setOnAction(event -> {
            sc.load();
        });

        menu.getNewButton().setOnAction(event -> {sc.newGame();});


    }


    public static void main(String[] args){
        //Sets logging level via program argument
        if (args.length > 0) {
            String logLevel = args[0];
            System.setProperty("logLevel", logLevel);
        }
        logger = LogManager.getLogger(Main.class);
        launch();
    }
}
