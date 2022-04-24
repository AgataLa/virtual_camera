package com.example.virtual_camera;

import java.util.List;

import static com.example.virtual_camera.VirtualCameraApp.middle_h;
import static com.example.virtual_camera.VirtualCameraApp.middle_w;

public class Transformation {
    private double d;
    private List<Rectangle> rectangles;

    public Transformation(List<Rectangle> rectangles) {
        d = 200;
        this.rectangles = rectangles;
    }

    public void projection() {
        Point2D point2D;
        double x, y;
        for(Rectangle rect: rectangles) {
            rect.getPoints2D().clear();
            for(Point3D point3D: rect.getPoints3D()) {
                x = point3D.getX()*d/ point3D.getZ();
                y = point3D.getY()*d/ point3D.getZ();
                point2D = new Point2D(x + middle_w, y + middle_h);
                rect.getPoints2D().add(point2D);
            }
        }
    }

    public void translate(double delta, String axis) {
        for(Rectangle rect: rectangles) {
            for(Point3D point3D: rect.getPoints3D()) {
                switch (axis) {
                    case "x":
                        point3D.setX(point3D.getX() + delta);
                        break;
                    case "y":
                        point3D.setY(point3D.getY() +delta);
                        break;
                    case "z":
                        point3D.setZ(point3D.getZ() +delta);
                        break;
                }
            }
        }
    }

    public void rotate(double delta, String axis) {

    }

    public void zoom(double delta){

    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }
}
