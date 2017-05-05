package carlfx.demos.navigateship;

import carlfx.gameengine.GameWorld;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The main driver of the game.
 *
 * @author cdea
 */
public class Part3_4_5 extends Application {

    GameWorld gameWorld = new TheExpanse(59, "Grory Tutorial Steps Follower Doohicky");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        gameWorld.initialize(primaryStage);

        gameWorld.beginGameLoop();

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Platform.exit();
        gameWorld.shutdown();
    }

}
