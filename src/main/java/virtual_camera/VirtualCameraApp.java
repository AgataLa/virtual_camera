package virtual_camera;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import virtual_camera.geometry.Figure2D;
import virtual_camera.geometry.Point2D;
import virtual_camera.transformations.Transformation;
import virtual_camera.utils.FileReader;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class VirtualCameraApp extends Application {
    private Dimension screenSize;
    public static int WIDTH;
    public static int HEIGHT;
    public static int middle_w;
    public static int middle_h;
    public static int DIST = 300;
    private Canvas canvas;
    private GraphicsContext gc;
    private List<Figure2D> figure2DS;
    private double deltaTranslate = 20;
    private double deltaRotate = 2;
    private double deltaZoom = 5;
    private Transformation transformation;
    private FileReader fileReader;

    @Override
    public void start(Stage stage) throws IOException {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;
        middle_w = WIDTH * 3 / 8;
        middle_h = HEIGHT * 3 / 8;

        stage.setTitle("Virtual Camera");
        stage.setResizable(false);
        Pane root = new Pane();

        canvas = new Canvas(WIDTH * 3 / 4, HEIGHT * 3 / 4);
        gc = canvas.getGraphicsContext2D();

        fileReader = new FileReader();
        figure2DS = fileReader.loadFigures2D();

        transformation = new Transformation(figure2DS);

        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        setUpKeyListener();
        canvas.requestFocus();
        draw();

        stage.show();
    }

    private void draw() {
        transformation.projection();

        boolean third = false;
        gc.setFill(Color.ALICEBLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.MEDIUMSLATEBLUE);
        gc.beginPath();
        for (Figure2D r : figure2DS) {
            boolean first = true;
            double xstart = 0;
            double ystart = 0;

            if(r.getPoints2D().size() == 3 && !third) {
                gc.stroke();
                gc.beginPath();
                gc.setStroke(Color.CHOCOLATE);
                third = true;
            } else if(r.getPoints2D().size() == 4 && third) {
                gc.stroke();
                gc.beginPath();
                gc.setStroke(Color.GREEN);
            }

            for (Point2D p : r.getPoints2D()) {
                if (first) {
                    xstart = p.getX();
                    ystart = p.getY();
                    gc.moveTo(p.getX(), p.getY());
                    first = false;
                } else {
                    gc.lineTo(p.getX(), p.getY());
                }
            }

            gc.lineTo(xstart, ystart);
            gc.stroke();
        }
    }

    private void setUpKeyListener() {
        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case W:
                        transformation.translate(-deltaTranslate, "z"); //ruch - przód z
                        break;
                    case S:
                        transformation.translate(deltaTranslate, "z"); //ruch - tył z
                        break;
                    case A:
                        transformation.translate(deltaTranslate, "x"); //ruch - lewo x
                        break;
                    case D:
                        transformation.translate(-deltaTranslate, "x"); //ruch - prawo x
                        break;
                    case Q:
                        transformation.translate(-deltaTranslate, "y"); //ruch - góra y
                        break;
                    case Z:
                        transformation.translate(deltaTranslate, "y"); //ruch - dół y
                        break;
                    case R:
                        transformation.rotate(-deltaRotate, "x"); //obrót - przód x
                        break;
                    case F:
                        transformation.rotate(deltaRotate, "x"); //obrót - tył x
                        break;
                    case T:
                        transformation.rotate(deltaRotate, "y"); //obrót - lewo y
                        break;
                    case Y:
                        transformation.rotate(-deltaRotate, "y"); //obrót - prawo y
                        break;
                    case G:
                        transformation.rotate(deltaRotate, "z"); //obrót - lewo z
                        break;
                    case H:
                        transformation.rotate(-deltaRotate, "z"); //obrót - prawo z
                        break;
                    case E:
                        transformation.zoom(deltaZoom); //zoom in
                        break;
                    case C:
                        transformation.zoom(-deltaZoom); //zoom out
                        break;
                    case SPACE:
                        figure2DS = fileReader.loadFigures2D();
                        transformation.changeFigures(figure2DS);
                        transformation.setDist(DIST);
                }
                draw();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}