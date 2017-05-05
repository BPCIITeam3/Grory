package carlfx.demos.navigateship;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Random;

public class TheExpanse extends GameWorld {

    Label mousePtLabel = new Label();
    Label mousePressPtLabel = new Label();

    TextField xCoordinate = new TextField("234");
    TextField yCoordinate = new TextField("200");
    Button moveShipButton = new Button("Rotate ship");

    Ship myShip = new Ship();

    public TheExpanse(int fps, String title) {
        super(fps, title);
    }

    @Override
    public void initialize(final Stage primaryStage) {
        primaryStage.setTitle(getWindowTitle());
        setSceneNodes(new Group());
        setGameSurface(new Scene(getSceneNodes(), 800, 600));
        getGameSurface().setFill(Color.BLACK);
        primaryStage.setScene(getGameSurface());
        setupInput(primaryStage);
        generateManySpheres(2);
        getSpriteManager().addSprites(myShip);
        getSceneNodes().getChildren().add(0, myShip.node);
        VBox stats = new VBox();

        HBox row1 = new HBox();
        mousePtLabel.setTextFill(Color.WHITE);
        row1.getChildren().add(mousePtLabel);
        HBox row2 = new HBox();
        mousePressPtLabel.setTextFill(Color.WHITE);
        row2.getChildren().add(mousePressPtLabel);

        stats.getChildren().add(row1);
        stats.getChildren().add(row2);
        HBox enterCoord1 = new HBox();
        enterCoord1.getChildren().add(xCoordinate);
        enterCoord1.getChildren().add(yCoordinate);
        enterCoord1.getChildren().add(moveShipButton);
        stats.getChildren().add(enterCoord1);
        moveShipButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                double x = Double.parseDouble(xCoordinate.getText());
                double y = Double.parseDouble(yCoordinate.getText());
                myShip.plotCourse(x, y, false);
            }
        });

    }

    private void setupInput(Stage primaryStage) {
        System.out.println("Ship's center is (" + myShip.getCenterX() + ", " + myShip.getCenterY() + ")");

        EventHandler fireOrMove = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePressPtLabel.setText("Mouse Press PT = (" + event.getX() + ", " + event.getY() + ")");
                if (event.getButton() == MouseButton.PRIMARY) {
                    myShip.plotCourse(event.getX(), event.getY(), false);
                    Missile m1 = myShip.fire();
                    getSpriteManager().addSprites(m1);

                    getSceneNodes().getChildren().add(0, m1.node);

                    if (getSpriteManager().getAllSprites().size() <= 1) {
                        generateManySpheres(30);
                    }
                    myShip.applyTheBrakes(event.getX(), event.getY());
                    myShip.plotCourse(event.getX(), event.getY(), true);
                }

            }
        };

        primaryStage.getScene().setOnMousePressed(fireOrMove);
        EventHandler changeWeapons = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.SPACE == event.getCode()) {
                    myShip.shieldToggle();
                    return;
                }
                myShip.changeWeapon(event.getCode());

            }
        };
        primaryStage.getScene().setOnKeyPressed(changeWeapons);

        EventHandler showMouseMove = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePtLabel.setText("Mouse PT = (" + event.getX() + ", " + event.getY() + ")");
            }
        };

        primaryStage.getScene().setOnMouseMoved(showMouseMove);
    }

    private void generateManySpheres(int numSpheres) {
        Random rnd = new Random();
        Scene gameSurface = getGameSurface();
        for (int i = 0; i < numSpheres; i++) {
            Color c = Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            Atom b = new Atom(rnd.nextInt(15) + 5, c, true);
            Circle circle = b.getAsCircle();
            b.vX = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);
            b.vY = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);
            double newX = rnd.nextInt((int) gameSurface.getWidth());
            if (newX > (gameSurface.getWidth() - (circle.getRadius() * 2))) {
                newX = gameSurface.getWidth() - (circle.getRadius() * 2);
            }
            double newY = rnd.nextInt((int) gameSurface.getHeight());
            if (newY > (gameSurface.getHeight() - (circle.getRadius() * 2))) {
                newY = gameSurface.getHeight() - (circle.getRadius() * 2);
            }

            circle.setTranslateX(newX);
            circle.setTranslateY(newY);
            circle.setVisible(true);
            circle.setId(b.toString());
            circle.setCache(true);
            circle.setCacheHint(CacheHint.SPEED);
            circle.setManaged(false);
            getSpriteManager().addSprites(b);
            getSceneNodes().getChildren().add(b.node);

        }
    }
    @Override
    protected void handleUpdate(Sprite sprite) {
        sprite.update();
        if (sprite instanceof Missile) {
            removeMissiles((Missile) sprite);
        } else {
            bounceOffWalls(sprite);
        }
    }
    private void bounceOffWalls(Sprite sprite) {
        Node displayNode;
        if (sprite instanceof Ship) {
            displayNode = sprite.node;
        } else {
            displayNode = sprite.node;
        }
        if (sprite.node.getTranslateX() > (getGameSurface().getWidth() - displayNode.getBoundsInParent().getWidth())
                || displayNode.getTranslateX() < 0) {
            sprite.vX = sprite.vX * -1;

        }
        if (sprite.node.getTranslateY() > getGameSurface().getHeight() - displayNode.getBoundsInParent().getHeight()
                || sprite.node.getTranslateY() < 0) {
            sprite.vY = sprite.vY * -1;
        }
    }
    private void removeMissiles(Missile missile) {
        if (missile.node.getTranslateX() > (getGameSurface().getWidth()
                - missile.node.getBoundsInParent().getWidth())
                || missile.node.getTranslateX() < 0) {

            getSpriteManager().addSpritesToBeRemoved(missile);
            getSceneNodes().getChildren().remove(missile.node);

        }
        if (missile.node.getTranslateY() > getGameSurface().getHeight()
                - missile.node.getBoundsInParent().getHeight()
                || missile.node.getTranslateY() < 0) {

            getSpriteManager().addSpritesToBeRemoved(missile);
            getSceneNodes().getChildren().remove(missile.node);
        }
    }
    @Override
    protected boolean handleCollision(Sprite spriteA, Sprite spriteB) {
        if (spriteA != spriteB) {
            if (spriteA.collide(spriteB)) {

                if (spriteA != myShip) {
                    spriteA.handleDeath(this);
                }
                if (spriteB != myShip) {
                    spriteB.handleDeath(this);
                }
            }
        }

        return false;
    }

}
