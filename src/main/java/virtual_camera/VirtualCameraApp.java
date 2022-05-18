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
    private List<Figure2D> sortedFigures;

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
        sortedFigures = new LinkedList<>();

        transformation = new Transformation(figure2DS);

        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        setUpKeyListener();
        canvas.requestFocus();
        //draw();
        drawWithFill2();

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

    private void drawWithFill2() {
        transformation.projection();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);

        List<Figure2D> painted = new LinkedList<>();
        List<Figure2D> cycle = new LinkedList<>();
        painted.addAll(figure2DS);
        Figure2D topaint = figure2DS.get(0);
        painted.remove(topaint);
        Figure2D restore = null;
        boolean canDraw = true;
        int nPainted = 0;
        System.out.println("START");
        while (nPainted != figure2DS.size()) {
            for (Figure2D f :
                    painted) {
                if (topaint.intersect(f)) {
                    int res = topaint.checkIfIsInFrontOf(f);
                    int res2 = f.checkIfIsInFrontOf(topaint);
                    if (res == res2) {
                        if(!(topaint.getMinZ() < f.getMinZ())) {
                            res = -1;
                        } else {
                            res = 1;
                        }
                    } else if (res == 0) {
                        res = -res2;
                    }

                    if (res == 1) {
                        restore = topaint;
                        topaint = f;
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
                gc.setFill(topaint.getColor());
                gc.fillPolygon(topaint.getXp(), topaint.getYp(), topaint.getXp().length);
                gc.strokePolygon(topaint.getXp(), topaint.getYp(), topaint.getXp().length);
                if (painted.size() != 0) {
                    topaint = painted.get(0);
                    painted.remove(topaint);
                }
            } else {
                if(!cycle.contains(restore)) {
                    cycle.add(restore);
                    painted.add(restore);
                    painted.remove(topaint);
                } else {
                    double max = Double.MIN_VALUE;
                    painted.add(restore);

                    for (Figure2D fig: cycle) {
                        double dist = fig.checkMaxDistanceFromObservator();
                        if( dist > max) {
                            max = fig.checkMaxDistanceFromObservator();
                            topaint = fig;
                        } else if (dist == max && fig.checkIfMinXIsLowerThanOther(topaint)){
                            topaint = fig;
                        }
                    }

                    painted.remove(topaint);
                    cycle.clear();
                    nPainted++;
                    gc.setFill(topaint.getColor());
                    gc.fillPolygon(topaint.getXp(), topaint.getYp(), topaint.getXp().length);
                    gc.strokePolygon(topaint.getXp(), topaint.getYp(), topaint.getXp().length);
                    if (painted.size() != 0) {
                        topaint = painted.get(0);
                        painted.remove(topaint);
                    }

                }
            }
        }
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

    private void drawWithFill() {
        transformation.projection();
        gc.setFill(Color.BLACK);
        sortPolygons();
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        for (Figure2D r : sortedFigures) {
            gc.setFill(r.getColor());
            gc.fillPolygon(r.getXp(), r.getYp(), r.getXp().length);
            gc.strokePolygon(r.getXp(), r.getYp(), r.getXp().length);
        }
    }

    private void sortPolygons() {
        sortedFigures.clear();
        sortedFigures.add(figure2DS.get(0));
        List<Figure2D> copySorted = new LinkedList<>();
        Map<Figure2D, Integer> sortedMap = new HashMap<>();
        sortedMap.put(figure2DS.get(0), 0);
        copySorted.add(figure2DS.get(0));
        for (int i = 1; i < figure2DS.size(); i++) {
            int index = 0;
            int prev = 1;
            for (Figure2D f : sortedFigures) {
                if (f.intersect(figure2DS.get(i))) {
                    int res = figure2DS.get(i).checkIfIsInFrontOf(f);
                    if (res == 0) {
                        res = -f.checkIfIsInFrontOf(figure2DS.get(i));
                    }
                    if (res == 1 && prev == 1) {
                        index = copySorted.indexOf(f) + 1;
                    } else if (res == 1 && prev == -1) {
                        copySorted.remove(f);
                        copySorted.add(index, f);
                        index++;
                        System.out.println("HERE");
                    } else if (res == -1 && prev == 1) {
                        break;
                        //continue;
                    } else if (res == -1 && prev == -1) {

                    } else {
                        //nie wiadomo
                        System.out.println("Nie wiadomo");
                    }
                    prev = res;
                }
            }
            //sortedFigures.add(index, figure2DS.get(i));
            copySorted.add(index, figure2DS.get(i));
            sortedFigures.clear();
            sortedFigures.addAll(copySorted);
            for (Figure2D f : sortedFigures) {
                System.out.print(f.getId() + " ");
            }
            System.out.println();
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
                drawWithFill2();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}