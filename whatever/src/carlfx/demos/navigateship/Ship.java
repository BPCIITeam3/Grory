package carlfx.demos.navigateship;

import carlfx.gameengine.Sprite;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Ship extends Sprite {

    private final static int TWO_PI_DEGREES = 360;

    private final static int NUM_DIRECTIONS = 32;

    private final static float UNIT_ANGLE_PER_FRAME = ((float) TWO_PI_DEGREES / NUM_DIRECTIONS);

    private final static int MILLIS_TURN_SHIP_180_DEGREES = 300;

    private final static float MILLIS_PER_FRAME = (float) MILLIS_TURN_SHIP_180_DEGREES / (NUM_DIRECTIONS / 2);

    private enum DIRECTION {
        CLOCKWISE, COUNTER_CLOCKWISE, NEITHER
    }

    private final static float THRUST_AMOUNT = 4.3f;

    private final static float MISSILE_THRUST_AMOUNT = 6.3F;

    private DIRECTION turnDirection = DIRECTION.NEITHER;

    private Vec u;

    private final List<RotatedShipImage> directionalShips = new ArrayList<>();

    private Timeline rotateShipTimeline;

    private int uIndex = 0;

    private int vIndex = 0;

    private final Circle stopArea = new Circle();

    private final Group flipBook = new Group();

    private KeyCode keyCode;

    private boolean shieldOn;

    private Circle shield;

    FadeTransition shieldFade;

    private Circle hitBounds;

    public Ship() {
        Image shipImage;
        shipImage = new Image(getClass().getClassLoader().getResource("grory.png").toExternalForm(), true);
        stopArea.setRadius(40);
        stopArea.setStroke(Color.ORANGE);
        RotatedShipImage prev = null;
        for (int i = 0; i < NUM_DIRECTIONS; i++) {
            RotatedShipImage imageView = new RotatedShipImage();
            imageView.setImage(shipImage);
            imageView.setRotate(-1 * i * UNIT_ANGLE_PER_FRAME);
            imageView.setCache(true);
            imageView.setCacheHint(CacheHint.SPEED);
            imageView.setManaged(false);

            imageView.prev = prev;
            imageView.setVisible(false);
            directionalShips.add(imageView);
            if (prev != null) {
                prev.next = imageView;
            }
            prev = imageView;
            flipBook.getChildren().add(imageView);
        }

        RotatedShipImage firstShip = directionalShips.get(0);
        firstShip.prev = prev;
        prev.next = firstShip;
        firstShip.setVisible(true);
        node = flipBook;
        flipBook.setTranslateX(200);
        flipBook.setTranslateY(300);
        flipBook.setCache(true);
        flipBook.setCacheHint(CacheHint.SPEED);
        flipBook.setManaged(false);
        flipBook.setAutoSizeChildren(false);
        initHitZone();

    }
    public void initHitZone() {
        // build hit zone
        if (hitBounds == null) {
            double hZoneCenterX = 55;
            double hZoneCenterY = 34;
            hitBounds = CircleBuilder.create()
                    .centerX(hZoneCenterX)
                    .centerY(hZoneCenterY)
                    .stroke(Color.PINK)
                    .fill(Color.RED)
                    .radius(15)
                    .opacity(0)
                    .build();
            flipBook.getChildren().add(hitBounds);
            collisionBounds = hitBounds;
        }

    }
    @Override
    public void update() {
        flipBook.setTranslateX(flipBook.getTranslateX() + vX);
        flipBook.setTranslateY(flipBook.getTranslateY() + vY);

        if (stopArea.contains(getCenterX(), getCenterY())) {
            vX = 0;
            vY = 0;
        }

    }


    private RotatedShipImage getCurrentShipImage() {
        return directionalShips.get(uIndex);
    }
    public double getCenterX() {
        RotatedShipImage shipImage = getCurrentShipImage();
        return node.getTranslateX() + (shipImage.getBoundsInLocal().getWidth() / 2);
    }
    public double getCenterY() {
        RotatedShipImage shipImage = getCurrentShipImage();
        return node.getTranslateY() + (shipImage.getBoundsInLocal().getHeight() / 2);
    }
    public void plotCourse(double screenX, double screenY, boolean thrust) {
        double sx = getCenterX();
        double sy = getCenterY();
        Vec v = new Vec(screenX, screenY, sx, sy);
        if (u == null) {
            u = new Vec(1, 0);
        }


        double atan2RadiansU = Math.atan2(u.y, u.x);
        double atan2DegreesU = Math.toDegrees(atan2RadiansU);

        double atan2RadiansV = Math.atan2(v.y, v.x);
        double atan2DegreesV = Math.toDegrees(atan2RadiansV);

        double angleBetweenUAndV = atan2DegreesV - atan2DegreesU;

        double absAngleBetweenUAndV = Math.abs(angleBetweenUAndV);
        boolean goOtherWay = false;
        if (absAngleBetweenUAndV > 180) {
            if (angleBetweenUAndV < 0) {
                turnDirection = DIRECTION.COUNTER_CLOCKWISE;
                goOtherWay = true;
            } else if (angleBetweenUAndV > 0) {
                turnDirection = DIRECTION.CLOCKWISE;
                goOtherWay = true;
            } else {
                turnDirection = Ship.DIRECTION.NEITHER;
            }
        } else {
            if (angleBetweenUAndV < 0) {
                turnDirection = Ship.DIRECTION.CLOCKWISE;
            } else if (angleBetweenUAndV > 0) {
                turnDirection = Ship.DIRECTION.COUNTER_CLOCKWISE;
            } else {
                turnDirection = Ship.DIRECTION.NEITHER;
            }
        }

        double degreesToMove = absAngleBetweenUAndV;
        if (goOtherWay) {
            degreesToMove = TWO_PI_DEGREES - absAngleBetweenUAndV;
        }
        uIndex = Math.round((float) (atan2DegreesU / UNIT_ANGLE_PER_FRAME));
        if (uIndex < 0) {
            uIndex = NUM_DIRECTIONS + uIndex;
        }
        vIndex = Math.round((float) (atan2DegreesV / UNIT_ANGLE_PER_FRAME));
        if (vIndex < 0) {
            vIndex = NUM_DIRECTIONS + vIndex;
        }
        String debugMsg = turnDirection +
                " U [m(" + u.mx + ", " + u.my + ")  => c(" + u.x + ", " + u.y + ")] " +
                " V [m(" + v.mx + ", " + v.my + ")  => c(" + v.x + ", " + v.y + ")] " +
                " start angle: " + atan2DegreesU +
                " end angle:" + atan2DegreesV +
                " Angle between: " + degreesToMove +
                " Start index: " + uIndex +
                " End index: " + vIndex;

        System.out.println(debugMsg);

        if (thrust) {
            vX = Math.cos(atan2RadiansV) * THRUST_AMOUNT;
            vY = -Math.sin(atan2RadiansV) * THRUST_AMOUNT;
        }
        turnShip();

        u = v;
    }

    private void turnShip() {

        final Duration oneFrameAmt = Duration.millis(MILLIS_PER_FRAME);
        RotatedShipImage startImage = directionalShips.get(uIndex);
        RotatedShipImage endImage = directionalShips.get(vIndex);
        List<KeyFrame> frames = new ArrayList<>();

        RotatedShipImage currImage = startImage;

        int i = 1;
        while (true) {

            final Node displayNode = currImage;

            KeyFrame oneFrame = new KeyFrame(oneFrameAmt.multiply(i),
                    new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(javafx.event.ActionEvent event) {
                            for (RotatedShipImage shipImg : directionalShips) {
                                shipImg.setVisible(false);
                            }
                            displayNode.setVisible(true);
                        }
                    });

            frames.add(oneFrame);

            if (currImage == endImage) {
                break;
            }
            if (turnDirection == DIRECTION.CLOCKWISE) {
                currImage = currImage.prev;
            }
            if (turnDirection == DIRECTION.COUNTER_CLOCKWISE) {
                currImage = currImage.next;
            }
            i++;
        }


        if (rotateShipTimeline != null) {
            rotateShipTimeline.stop();
            rotateShipTimeline.getKeyFrames().clear();
            rotateShipTimeline.getKeyFrames().addAll(frames);
        } else {
            rotateShipTimeline = TimelineBuilder.create()
                    .keyFrames(frames)
                    .build();

        }

        rotateShipTimeline.playFromStart();


    }
    public void applyTheBrakes(double screenX, double screenY) {
        stopArea.setCenterX(screenX);
        stopArea.setCenterY(screenY);
    }

    public Missile fire() {
        Missile m1;

        float slowDownAmt = 0;
        int scaleBeginningMissle;
        if (KeyCode.DIGIT2 == keyCode) {
            m1 = new Missile(9, Color.BLUE);
            slowDownAmt = 1.3f;
            scaleBeginningMissle = 11;
        } else {
            m1 = new Missile(Color.RED);
            scaleBeginningMissle = 8;
        }
        m1.vX = Math.cos(Math.toRadians(uIndex * UNIT_ANGLE_PER_FRAME)) * (MISSILE_THRUST_AMOUNT - slowDownAmt);
        m1.vY = -Math.sin(Math.toRadians(uIndex * UNIT_ANGLE_PER_FRAME)) * (MISSILE_THRUST_AMOUNT - slowDownAmt);
        RotatedShipImage shipImage = directionalShips.get(uIndex);

        double offsetX = (shipImage.getBoundsInLocal().getWidth() - m1.node.getBoundsInLocal().getWidth()) / 2;
        double offsetY = (shipImage.getBoundsInLocal().getHeight() - m1.node.getBoundsInLocal().getHeight()) / 2;
        m1.node.setTranslateX(node.getTranslateX() + (offsetX + (m1.vX * scaleBeginningMissle)));
        m1.node.setTranslateY(node.getTranslateY() + (offsetY + (m1.vY * scaleBeginningMissle)));
        return m1;
    }

    public void changeWeapon(KeyCode keyCode) {
        this.keyCode = keyCode;
    }

    public void shieldToggle() {


        if (shield == null) {
            RotatedShipImage shipImage = getCurrentShipImage();
            double x = shipImage.getBoundsInLocal().getWidth() / 2;
            double y = shipImage.getBoundsInLocal().getHeight() / 2;
            shield = CircleBuilder.create()
                    .radius(60)
                    .strokeWidth(5)
                    .stroke(Color.LIMEGREEN)
                    .centerX(x)
                    .centerY(y)
                    .opacity(.70)
                    .build();
            collisionBounds = shield;
            shieldFade = FadeTransitionBuilder.create()
                    .fromValue(1)
                    .toValue(.40)
                    .duration(Duration.millis(1000))
                    .cycleCount(12)
                    .autoReverse(true)
                    .node(shield)
                    .onFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            shieldOn = false;
                            flipBook.getChildren().remove(shield);
                            shieldFade.stop();
                            collisionBounds = hitBounds;
                        }
                    })
                    .build();
            shieldFade.playFromStart();

        }
        shieldOn = !shieldOn;
        if (shieldOn) {
            collisionBounds = shield;
            flipBook.getChildren().add(0, shield);
            shieldFade.playFromStart();
        } else {
            flipBook.getChildren().remove(shield);
            shieldFade.stop();
            collisionBounds = hitBounds;

        }


    }

}
