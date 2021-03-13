package sample;


import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Snake implements Iterable<Rectangle> {

    LinkedList<Rectangle> snakeSegments;
    int direction;
    private Pane surface;
    Color paint;
    private final int resolution;
    int lives = 3;
    boolean initialized = false;
    int steps = 18;


    public Snake(Pane surface, Color paint, int resolution, int offsetX, int offsetY) {
        this.snakeSegments = new LinkedList<>();
        this.direction = 0;
        this.surface = surface;
        this.paint = paint;
        this.resolution = resolution;

        addBodySegment();
        addBodySegment();


        snakeSegments.getFirst().setX(resolution - resolution / 20 * offsetX);
        snakeSegments.get(0).setX(resolution - resolution / 20 * offsetX);

        snakeSegments.getFirst().setY(resolution - resolution / 20 * offsetY);
        snakeSegments.get(0).setY(resolution - resolution / 20 * offsetY);

    }

    public Rectangle getTail() {
        return this.snakeSegments.getLast();
    }

    public Rectangle getHead() {
        return this.snakeSegments.getFirst();
    }

    public void setDirection(int direction) {
        switch (direction) {
            case 0 -> {
                if (this.direction % 2 != 0) this.direction = 0;
            }
            case 2 -> {
                if (this.direction % 2 != 0) this.direction = 2;
            }

            case 1 -> {
                if (this.direction % 2 == 0) this.direction = 1;
            }

            case 3 -> {
                if (this.direction % 2 == 0) this.direction = 3;
            }

        }

    }

    public void step() {

        Rectangle lastSegment = snakeSegments.getLast();
        Rectangle firstSegment = snakeSegments.getFirst();

        double firstX = firstSegment.getX();
        double firstY = firstSegment.getY();
        lastSegment.setX(firstX);
        lastSegment.setY(firstY);


        if (!lastSegment.isVisible())
            lastSegment.setVisible(true);

        switch (direction) {

            case 3 -> lastSegment.setX(firstX >= resolution / 20 ? (firstSegment.getX() - resolution / 20) : resolution - resolution / 20);
            case 1 -> lastSegment.setX((firstX + resolution / 20) % resolution);
            case 2 -> lastSegment.setY((firstY + resolution / 20) % resolution);
            case 0 -> lastSegment.setY(firstY >= resolution / 20 ? (firstSegment.getY() - resolution / 20) : resolution - resolution / 20);
        }

        snakeSegments.remove(lastSegment);
        snakeSegments.addFirst(lastSegment);

        if (initialized) {
            if (checkCollision()) {
                for (Rectangle r : snakeSegments)

                    lives--;
                this.reset();
                //lighterAfterDamage();

            } else
                for (Rectangle r : snakeSegments)
                    r.setFill(paint);

        } else
            initialized = true;

    }

    public void addBodySegment() {
        Rectangle segment = new Rectangle(resolution / 20, resolution / 20, paint);

        if (!snakeSegments.isEmpty())
            segment.setVisible(false);

        surface.getChildren().add(segment);
        snakeSegments.addLast(segment);

    }

    public boolean checkCollision() {
        List<Pair<Integer, Integer>> positions = new LinkedList<>();
        for (Rectangle collider : snakeSegments) {
            for (Pair<Integer, Integer> position : positions) {
                if (position.getKey() == collider.getX() && position.getValue() == collider.getY()) {
                    return true;
                }
            }

            positions.add(new Pair<>((int) collider.getX(), (int) collider.getY()));

        }

        return false;
    }


    public boolean collidesWithRectangle(Rectangle r) {
        for (Rectangle rectangle : snakeSegments) {
            if (rectangle.getY() == r.getY() && rectangle.getX() == r.getX()) {
                r.setFill(Color.PINK);
                return true;
            }
        }
        return false;
    }

    public void reset() {
        for (Rectangle r : snakeSegments)
            r.setVisible(false);
        this.snakeSegments = new LinkedList<>();

        addBodySegment();
        addBodySegment();
        initialized = false;
    }


    @Override
    public Iterator<Rectangle> iterator() {
        return snakeSegments.iterator();
    }


    public void lighterAfterDamage() {
        Color c = Color.rgb((int) paint.getRed(),
                (int) paint.getGreen(),
                (int) paint.getBlue(),
                paint.getBrightness() - 0.1);
        paint = c;

        for (Rectangle r : snakeSegments)
            r.setFill(Color.PINK);
    }

}
