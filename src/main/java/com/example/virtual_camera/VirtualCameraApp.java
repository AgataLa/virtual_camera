package com.example.virtual_camera;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;

public class VirtualCameraApp extends Application {
    private Dimension screenSize;
    public static int WIDTH;
    public static int HEIGHT;
    public static int middle_w;
    public static int middle_h;
    private MyAnimationTimer timer;
    private Canvas canvas;
    private GraphicsContext gc;
    private List<Rectangle> rectangles;
    private double d = 200;
    private double deltaTranslate = 20;
    private double deltaRotate = 10;
    private double deltaZoom = 10;
    private Transformation transformation;

    @Override
    public void start(Stage stage) throws IOException {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;
        stage.setTitle("Virtual Camera");
        stage.setResizable(false);
        Pane root = new Pane();
        middle_w = WIDTH * 3 / 8;
        middle_h = HEIGHT * 3 / 8;
        canvas = new Canvas(WIDTH * 3 / 4, HEIGHT * 3 / 4);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        FileReader fileReader = new FileReader();
        rectangles = fileReader.loadRectangles();
        transformation = new Transformation(rectangles);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch(keyEvent.getCode()) {
                    case W:
                        transformation.translate(deltaTranslate, "z");
                        break;
                    case S:
                        transformation.translate(-deltaTranslate, "z");
                        break;
                    case A:
                        transformation.translate(-deltaTranslate, "x");
                        break;
                    case D:
                        transformation.translate(deltaTranslate, "x");
                        break;
                    case Q:
                        transformation.translate(deltaTranslate, "y");
                        break;
                    case E:
                        transformation.translate(-deltaTranslate, "y");
                        break;
                    case R:
                        transformation.rotate(deltaRotate, "x"); //obrót - przód x
                        break;
                    case F:
                        transformation.rotate(-deltaRotate, "x"); //obrót - tył x
                        break;
                    case T:
                        transformation.rotate(deltaRotate, "y"); //obrót - lewo y
                        break;
                    case G:
                        transformation.rotate(-deltaRotate, "y"); //obrót - prawo y
                        break;
                    case Y:
                        transformation.rotate(deltaRotate, "z");//obrót - lewo z
                        break;
                    case H:
                        transformation.rotate(-deltaRotate, "z"); //obrót - prawo z
                        break;
                    case Z:
                        transformation.zoom(deltaZoom); //zoom in
                        break;
                    case X:
                        transformation.zoom(-deltaZoom); //zoom out
                        break;
                }
                draw();
            }
        });
        timer = new MyAnimationTimer();
        timer.start();
        canvas.requestFocus();
        stage.show();
    }



    private void draw() {
        transformation.projection();
        gc.setFill(Color.ALICEBLUE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.MEDIUMSLATEBLUE);
        gc.beginPath();
        for (Rectangle r : rectangles) {
            boolean first = true;
            double xstart = 0;
            double ystart = 0;
            for (Point2D p : r.getPoints2D()) {
                if(first) {
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

    private class MyAnimationTimer extends AnimationTimer {

        public MyAnimationTimer() {
        }

        @Override
        public void handle(long l) {
            draw();
        }

    }

//    private double[] multMatrixAndVector(double[][] matrix, double[] vector) {
//        double[] result = new double[4];
//        for(int i = 0; i < vector.length; i++) {
//            for(int j = 0; j < vector.length; j++) {
//                result[i] += matrix[i][j]*vector[j];
//            }
//        }
//        normalizeVector(result);
//
//        return result;
//    }
//
//    private void normalizeVector(double[] vector) {
//        double norm = vector[3];
//        for(int i = 0; i < vector.length; i++) {
//            vector[i] /= norm;
//        }
//    }

    public static void main(String[] args) {
        launch();
    }
}