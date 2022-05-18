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
import java.util.*;
import java.util.List;

public class VirtualCameraApp extends Application {
    private Dimension screenSize;
    public static int WIDTH;
    public static int HEIGHT;
    public static int middle_w;
    public static int middle_h;
    public static int DIST = 1000;
    private Canvas canvas;
    private GraphicsContext gc;
    private List<Figure2D> figure2DS;
    private final int deltaTranslate = 20;
    private final int deltaRotate = 2;
    private final int deltaZoom = 5;
    private Transformation transformation;
    private FileReader fileReader;
    private List<Figure2D> painted, cycle;

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
        painted = new LinkedList<>();
        cycle = new LinkedList<>();

        transformation = new Transformation(figure2DS);

        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        setUpKeyListener();
        canvas.requestFocus();
        //drawWithoutFill();
        drawWithFill();

        stage.show();
    }

    private void drawWithoutFill() {
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

            if (r.getPoints2D().size() == 3 && !third) {
                gc.stroke();
                gc.beginPath();
                gc.setStroke(Color.CHOCOLATE);
                third = true;
            } else if (r.getPoints2D().size() == 4 && third) {
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

    private void drawWithFill() {
        transformation.projection();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);

        painted.addAll(figure2DS);
        Figure2D toPaint = figure2DS.get(0);
        painted.remove(toPaint);
        Figure2D restore = null;
        boolean canDraw = true;
        int nPainted = 0;

        while (nPainted != figure2DS.size()) {
            for (Figure2D f :
                    painted) {
                if (toPaint.intersect(f)) {
                    int res = toPaint.checkIfIsInFrontOf(f);
                    int res2 = f.checkIfIsInFrontOf(toPaint);
                    if (res == res2) {
                        if(!(toPaint.getMinZ() < f.getMinZ())) {
                            res = -1;
                        } else {
                            res = 1;
                        }
                    } else if (res == 0) {
                        res = -res2;
                    }

                    if (res == 1) {
                        restore = toPaint;
                        toPaint = f;
                        canDraw = false;
                        break;
                    } else {
                        canDraw = true;
                    }
                } else {
                    canDraw = true;
                }
            }

            if (canDraw) {
                cycle.clear();
                nPainted++;
                fillPolygon(toPaint);
                if (painted.size() != 0) {
                    toPaint = painted.get(0);
                    painted.remove(toPaint);
                }
            } else {
                if(!cycle.contains(restore)) {
                    cycle.add(restore);
                    painted.add(restore);
                    painted.remove(toPaint);
                } else {
                    double max = Double.MIN_VALUE;
                    painted.add(restore);

                    for (Figure2D fig: cycle) {
                        double dist = fig.checkMaxDistanceFromObserver();
                        if( dist > max) {
                            max = fig.checkMaxDistanceFromObserver();
                            toPaint = fig;
                        } else if (dist == max && fig.checkIfMinXIsLowerThanOther(toPaint)){
                            toPaint = fig;
                        }
                    }

                    painted.remove(toPaint);
                    cycle.clear();
                    nPainted++;
                    fillPolygon(toPaint);

                    if (painted.size() != 0) {
                        toPaint = painted.get(0);
                        painted.remove(toPaint);
                    }

                }
            }
        }
        painted.clear();
        cycle.clear();
    }

    public void fillPolygon(Figure2D toPaint) {
        gc.setFill(toPaint.getColor());
        gc.fillPolygon(toPaint.getXp(), toPaint.getYp(), toPaint.getXp().length);
        gc.strokePolygon(toPaint.getXp(), toPaint.getYp(), toPaint.getXp().length);
    }

    public boolean isPointInPolygon(Point2D p, List<Point2D> polygon) {
        double minX = polygon.get(0).getX();
        double maxX = polygon.get(0).getX();
        double minY = polygon.get(0).getY();
        double maxY = polygon.get(0).getY();
        for (int i = 1; i < polygon.size(); i++) {
            Point2D q = polygon.get(i);
            minX = Math.min(q.getX(), minX);
            maxX = Math.max(q.getX(), maxX);
            minY = Math.min(q.getY(), minY);
            maxY = Math.max(q.getY(), maxY);
        }

        if (p.getX() < minX || p.getX() > maxX || p.getY() < minY || p.getY() > maxY) {
            return false;
        }

        boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            if ((polygon.get(i).getY() > p.getY()) != (polygon.get(j).getY() > p.getY()) &&
                    p.getX() < (polygon.get(j).getX() - polygon.get(i).getX()) * (p.getY() - polygon.get(i).getY()) /
                            (polygon.get(j).getY() - polygon.get(i).getY()) + polygon.get(i).getX()) {
                inside = !inside;
            }
        }

        return inside;
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
                drawWithFill();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
