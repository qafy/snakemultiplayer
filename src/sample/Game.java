package sample;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Game extends Application {


    Rectangle apple;
    Pane root;
    Scene scene;
    AnimationTimer game;
    AnimationTimer gameClient;
    AnimationTimer titleScreen;
    LinkedList<Snake> snakes = new LinkedList<>();
    LinkedList<ImageView> images = new LinkedList<>();
    Host host;
    Client join;
    Snake greenTitleSnake;
    Snake blueTitleSnake;
    int appleSeed;
    Random randomizer;

    final int NORTH = 0;
    final int EAST = 1;
    final int SOUTH = 2;
    final int WEST = 3;

    int getAppleSeed = 4739157;

    final int RESOLUTION = 500;

    @Override
    public void start(Stage primaryStage) throws Exception {
        appleSeed = 500;
        randomizer = new Random(appleSeed);
        root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
        scene = new Scene(root, RESOLUTION, RESOLUTION);
        setTitleImage();
        TextField field = new TextField();
        field.setVisible(false);
        root.getChildren().add(field);
        snakes.add(new Snake(root, Color.GREEN, RESOLUTION, 2, 20));
        snakes.add(new Snake(root, Color.BLUE, RESOLUTION, 19, 20));

        for (Snake s : snakes)
            for (Rectangle r : s)
                r.setVisible(false);

        blueTitleSnake = new Snake(root, Color.BLUE, RESOLUTION, 2, 19);
        greenTitleSnake = new Snake(root, Color.GREEN, RESOLUTION, 19, 2);
        blueTitleSnake.addBodySegment();
        greenTitleSnake.addBodySegment();
        blueTitleSnake.direction = SOUTH;

        /*
        Thread t = new Thread(() -> {
           String soundFilename = "C:\\Users\\m\\IdeaProjects\\snake\\Playlist.wav";
            Media hit = new Media("C:/Users/m/IdeaProjects/snake/Playlist.wav");
        MediaPlayer mp = new MediaPlayer(m);
        mp.play();
        });
        t.start();

        snakes.add(new Snake(root, Color.RED, RESOLUTION, 10));
        snakes.add(new Snake(root, Color.MAGENTA, RESOLUTION, 12));
        snakes.add(new Snake(root, Color.GREEN, RESOLUTION, 13));
        */


        EventHandler<MouseEvent> titleHover = x -> {

            double yPos = x.getY();
            double xPos = x.getX();

            ImageView host = images.get(2);
            host.setVisible(!checkCursorHover(host, xPos, yPos));

            ImageView join = images.get(4);
            join.setVisible(!checkCursorHover(join, xPos, yPos));

        };

        EventHandler<KeyEvent> clientControl = z -> {

            switch (z.getCode()) {

                case UP -> Game.this.snakes.get(1).setDirection(NORTH);
                case RIGHT -> Game.this.snakes.get(1).setDirection(EAST);
                case DOWN -> Game.this.snakes.get(1).setDirection(SOUTH);
                case LEFT -> Game.this.snakes.get(1).setDirection(WEST);

            }
        };

        EventHandler<KeyEvent> hostControl = x -> {

            switch (x.getCode()) {

                case UP -> snakes.get(0).setDirection(NORTH);
                case DOWN -> snakes.get(0).setDirection(SOUTH);
                case RIGHT -> snakes.get(0).setDirection(EAST);
                case LEFT -> snakes.get(0).setDirection(WEST);

            }
        };

        EventHandler<MouseEvent> titleClick = x -> {
            double xPos = x.getX();
            double yPos = x.getY();

            ImageView host = images.get(2);
            ImageView join = images.get(3);
            if (checkCursorHover(host, xPos, yPos)) {
                for (ImageView v : images)
                    v.setVisible(false);
                images.get(5).setVisible(true);

                scene.setOnMouseMoved(n -> {
                });
                scene.setOnMouseClicked(o -> {});

                Game.this.host = new Host();
                Thread waitingToStartGame = new Thread(() -> {
                    while (!Game.this.host.isConnected()) {
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                        }
                    }
                    Platform.runLater(Game.this::startGame);
                    Platform.runLater(Game.this::addApple);
                    game.start();
                    Game.this.host.output.println(0);
                    /*
                    Thread directionFromClient = new Thread(() -> {

                        while (true) {

                            String si = null;
                            try {
                                si = Game.this.host.input.readLine();
                            } catch (Exception e) {
                            }


                            if (si != null)
                                snakes.get(1).setDirection(Integer.parseInt(si));
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    directionFromClient.start();
                    */
                });
                waitingToStartGame.start();


            } else if (checkCursorHover(join, xPos, yPos)) {
                for (ImageView v : images)
                    v.setVisible((false));


                scene.setOnMouseMoved(n -> {
                });
                scene.setOnMouseClicked(y -> {
                });
                Game.this.join = new Client("localhost", 8081);
                Thread waitingToStartGame = new Thread(() -> {
                    while (!Game.this.join.isConnected()) {
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                        }
                    }

                    Platform.runLater(Game.this::startGame);
                    Platform.runLater(Game.this::addApple);
                    gameClient.start();

                });
                waitingToStartGame.start();

                scene.setOnKeyPressed(clientControl);


            }


        };


        titleScreen = new AnimationTimer() {
            long lasttime = 0;

            @Override
            public void handle(long l) {
                if (l - lasttime >= 250_000_000L) {

                    walkAnimationTitleScreen(greenTitleSnake);
                    walkAnimationTitleScreen(blueTitleSnake);

                    lasttime = l;
                }
            }

        };

        game = new AnimationTimer() {
            private long lasttime = 0;
            private int direction = 0;

            @Override
            public void handle(long l) {
                try {

                    if (l - lasttime >= 250_000_000L && Game.this.host.input.ready()) {
                        direction = Integer.parseInt(Game.this.host.input.readLine());
                        Game.this.snakes.get(1).setDirection(direction);

                        for (Snake s : snakes) {

                            s.step();

                            if (s.lives == 0)
                                this.stop();
                            if (apple.getX() == s.getHead().getX() && apple.getY() == s.getHead().getY() && apple.isVisible()) {
                                s.addBodySegment();
                                addApple();
                            }
                            for (Snake s2 : snakes) {
                                if (s != s2) {
                                    if (s2.collidesWithRectangle(s.getHead())) {
                                        s.reset();
                                        s.lives--;
                                    }
                                }
                            }
                        }

                        StringBuilder toClient = new StringBuilder();
                        toClient.append(snakes.get(0).direction);
                        /*

                        for (Snake s : snakes) {
                            toClient.append(" ");
                            toClient.append("S");

                            for (Rectangle r : s) {
                                toClient.append(" ");
                                toClient.append(r.getX());
                                toClient.append(" ");
                                toClient.append(r.getY());

                            }
                        }
                        */
                        Game.this.host.output.println(toClient.toString());


                        lasttime = l;
                    }

                } catch (Exception e) {
                }
            }
        };

        gameClient = new AnimationTimer() {
            @Override
            public void handle(long l) {
                try {
                    if (Game.this.join.input.ready()) {
                        String fromHost = Game.this.join.input.readLine();
                        Game.this.snakes.get(0).setDirection(Integer.parseInt(fromHost));

                        /*
                        String[] firstSnake = fromHost[1].split(" ");


                        String[] secondSnake = fromHost[2].split(" ");

                        snakes.get(0).setDirection(Integer.parseInt(fromHost[0].trim()));


                        for(int i = snakes.get(0).snakeSegments.size(); i < firstSnake.length / 2; i++) {
                            snakes.get(0).addBodySegment();
                        }

                        int i = 1;

                        boolean copying = false;
                        for (Rectangle r : snakes.get(0)) {

                            r.setX(Double.parseDouble(firstSnake[i++]));
                            r.setY(Double.parseDouble(firstSnake[i++]));


                        }

                        Rectangle lastSegment = snakes.get(0).snakeSegments.getLast();
                        snakes.get(0).snakeSegments.remove(lastSegment);
                        snakes.get(0).snakeSegments.addFirst(lastSegment);

                        i = 1;

                        for (Rectangle r : snakes.get(1)) {
                            r.setX(Double.parseDouble(secondSnake[i++]));
                            r.setY(Double.parseDouble(secondSnake[i++]));
                        }


                        Rectangle lastSegment2 = snakes.get(1).snakeSegments.getLast();
                        snakes.get(1).snakeSegments.remove(lastSegment2);
                        snakes.get(1).snakeSegments.addFirst(lastSegment2);
                        */

                        for (Snake s : snakes) {

                            s.step();

                            if (s.lives == 0)
                                this.stop();
                            if (apple.getX() == s.getHead().getX() && apple.getY() == s.getHead().getY() && apple.isVisible()) {
                                addApple();
                                s.addBodySegment();

                            }
                            for (Snake s2 : snakes) {
                                if (s != s2) {
                                    if (s2.collidesWithRectangle(s.getHead())) {
                                        s.reset();
                                        s.lives--;
                                    }
                                }
                            }
                        }
                        Game.this.join.output.println(Game.this.snakes.get(1).direction);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        scene.setOnMouseMoved(titleHover);

        scene.setOnMouseClicked(titleClick);

        scene.setOnKeyPressed(hostControl);

        titleScreen.start();


        primaryStage.setTitle("Schnak");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void startGame() {
        for (Snake s : snakes)
            for (Rectangle r : s)
                r.setVisible(true);

        for (ImageView v : images)
            v.setVisible(false);


        titleScreen.stop();
        for (Rectangle r : greenTitleSnake)
            r.setVisible(false);
        for (Rectangle r : blueTitleSnake)
            r.setVisible(false);
    }

    public void startGameAsClient() {
        for (Snake s : snakes)
            for (Rectangle r : s)
                r.setVisible(true);

        for (ImageView v : images)
            v.setVisible(false);
    }


    public void addApple() {
        int x = 0;
        int y = 0;

        if (apple == null) {
            apple = new Rectangle(RESOLUTION / 20, RESOLUTION / 20, Color.RED);
            root.getChildren().add(apple);
        }
        if (!apple.isVisible())
            apple.setVisible(true);

        x = Math.abs(randomizer.nextInt() % 20) * RESOLUTION / 20;
        y = Math.abs(randomizer.nextInt() % 20) * RESOLUTION / 20;
        apple.setY(y);
        apple.setX(x);

    }

    public static void main(String[] args) {
        launch(args);
    }


    public static void walkAnimationTitleScreen(Snake s1) {
        if (--s1.steps != 0)
            s1.step();
        else {
            s1.steps = 18;
            s1.direction = (s1.direction + 1) % 4;
        }

    }


    public void setTitleImage() throws FileNotFoundException {
        final double scalefactor = 10;

        ImageView view;
        Image image = new Image(new FileInputStream("C:\\Users\\m\\Desktop\\Archiv\\Random Bilder\\schnack.bmp"));
        view = new ImageView(resize(image, 10));
        view.setSmooth(false);
        view.setX(RESOLUTION / 8 - RESOLUTION / 16);
        view.setY(RESOLUTION / 8);
        images.add(view);
        root.getChildren().add(view);

        ImageView hostview2;
        Image host2 = new Image(new FileInputStream("C:\\Users\\m\\Desktop\\Archiv\\Random Bilder\\host2.bmp"));
        hostview2 = new ImageView(resize(host2, 3));
        hostview2.setSmooth(false);
        hostview2.setX(RESOLUTION / 2 - RESOLUTION / 6);
        hostview2.setY(RESOLUTION / 2 + RESOLUTION / 20);
        images.add(hostview2);
        root.getChildren().add(hostview2);

        ImageView hostview;
        Image host = new Image(new FileInputStream("C:\\Users\\m\\Desktop\\Archiv\\Random Bilder\\host.bmp"));
        hostview = new ImageView(resize(host, 3));
        hostview.setSmooth(false);
        hostview.setX(RESOLUTION / 2 - RESOLUTION / 6);
        hostview.setY(RESOLUTION / 2 + RESOLUTION / 20);
        images.add(hostview);
        root.getChildren().add(hostview);

        ImageView joinview2;
        Image join2 = new Image(new FileInputStream("C:\\Users\\m\\Desktop\\Archiv\\Random Bilder\\join2.bmp"));
        joinview2 = new ImageView(resize(join2, 3));
        joinview2.setSmooth(false);
        joinview2.setX(RESOLUTION / 2 - RESOLUTION / 5);
        joinview2.setY(RESOLUTION / 2 + RESOLUTION / 5);
        images.add(joinview2);
        root.getChildren().add(joinview2);


        ImageView joinview;
        Image join = new Image(new FileInputStream("C:\\Users\\m\\Desktop\\Archiv\\Random Bilder\\join1.bmp"));
        joinview = new ImageView(resize(join, 3));
        joinview.setSmooth(false);
        joinview.setX(RESOLUTION / 2 - RESOLUTION / 5);
        joinview.setY(RESOLUTION / 2 + RESOLUTION / 5);
        images.add(joinview);
        root.getChildren().add(joinview);


        ImageView waitingforconnectionview;
        Image waitingforconnection = new Image(new FileInputStream("C:\\Users\\m\\Desktop\\Archiv\\Random Bilder\\waitingforconnection.bmp"));
        waitingforconnectionview = new ImageView(resize(waitingforconnection, 3));
        waitingforconnectionview.setSmooth(false);
        waitingforconnectionview.setX(RESOLUTION / 4 - RESOLUTION / 10);
        waitingforconnectionview.setY(RESOLUTION / 2 + RESOLUTION / 5);
        images.add(waitingforconnectionview);
        root.getChildren().add(waitingforconnectionview);
        waitingforconnectionview.setVisible(false);


    }


    public static Image resize(Image image, int scalefactor) {
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage(scalefactor * width, scalefactor * height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                for (int dy = 0; dy < scalefactor; dy++) {
                    for (int dx = 0; dx < scalefactor; dx++)
                        pixelWriter.setArgb(x * scalefactor + dx, y * scalefactor + dy, argb);
                }
            }
        }

        return writableImage;

    }

    public static boolean checkCursorHover(ImageView image, double xPos, double yPos) {


        double hostX = image.getX();
        double hostY = image.getY();

        double hostHeight = image.getImage().getHeight();
        double hostWidth = image.getImage().getWidth();

        return xPos >= hostX && xPos <= hostX + hostWidth && yPos >= hostY && yPos <= hostY + hostHeight - 50;


    }
}
