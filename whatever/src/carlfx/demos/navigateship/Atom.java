package carlfx.demos.navigateship;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import javafx.animation.FadeTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.RadialGradientBuilder;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.util.Duration;

public class Atom extends Sprite {


    public Atom(double radius, Color fill, boolean gradientFill) {
        Circle sphere = CircleBuilder.create()
                .centerX(radius)
                .centerY(radius)
                .radius(radius)
                .fill(fill)
                .cache(true)
                .cacheHint(CacheHint.SPEED)
                .build();
        if (gradientFill) {
            RadialGradient rgrad = RadialGradientBuilder.create()
                    .centerX(sphere.getCenterX() - sphere.getRadius() / 3)
                    .centerY(sphere.getCenterY() - sphere.getRadius() / 3)
                    .radius(sphere.getRadius())
                    .proportional(false)
                    .stops(new Stop(0.0, Color.WHITE), new Stop(1.0, fill))
                    .build();

            sphere.setFill(rgrad);
        }

        node = sphere;
        collisionBounds = sphere;

    }


    @Override
    public void update() {
        node.setTranslateX(node.getTranslateX() + vX);
        node.setTranslateY(node.getTranslateY() + vY);
    }

    public Circle getAsCircle() {
        return (Circle) node;
    }

    public void implode(final GameWorld gameWorld) {
        vX = vY = 0;
        FadeTransitionBuilder.create()
                .node(node)
                .duration(Duration.millis(300))
                .fromValue(node.getOpacity())
                .toValue(0)
                .onFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                        isDead = true;
                        gameWorld.getSceneNodes().getChildren().remove(node);

                    }
                })
                .build()
                .play();
    }

    public void handleDeath(GameWorld gameWorld) {
        implode(gameWorld);
        super.handleDeath(gameWorld);
    }
}
