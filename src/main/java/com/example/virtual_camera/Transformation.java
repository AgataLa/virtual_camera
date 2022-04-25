package com.example.virtual_camera;

import java.util.List;

import static com.example.virtual_camera.VirtualCameraApp.middle_h;
import static com.example.virtual_camera.VirtualCameraApp.middle_w;

public class Transformation {
    private double dist;
    private List<Rectangle> rectangles;

    public Transformation(List<Rectangle> rectangles) {
        dist = 200;
        this.rectangles = rectangles;
    }

    public void projection() {
        Point2D point2D;
        double x, y;
        for (Rectangle rect : rectangles) {
            rect.getPoints2D().clear();
            for (Point3D point3D : rect.getPoints3D()) {
                x = point3D.getX() * dist / point3D.getZ();
                y = point3D.getY() * dist / point3D.getZ();
                point2D = new Point2D(x + middle_w, y + middle_h);
                rect.getPoints2D().add(point2D);
            }
        }
    }

    public void translate(double delta, String axis) {
        for (Rectangle rect : rectangles) {
            for (Point3D point3D : rect.getPoints3D()) {
                switch (axis) {
                    case "x":
                        point3D.setX(point3D.getX() + delta);
                        break;
                    case "y":
                        point3D.setY(point3D.getY() + delta);
                        break;
                    case "z":
                        point3D.setZ(point3D.getZ() + delta);
                        break;
                }
            }
        }
    }

    public void rotate(double angle, String axis) {
        double x, y, z;
        angle = Math.toRadians(angle);
        for (Rectangle rect : rectangles) {
            for (Point3D point3D : rect.getPoints3D()) {
                x = point3D.getX();
                y = point3D.getY();
                z = point3D.getZ();
                switch (axis) {
                    case "x":
                        point3D.setY(y * Math.cos(angle) - z * Math.sin(angle));
                        point3D.setZ(y * Math.sin(angle) + z * Math.cos(angle));
                        break;
                    case "y":
                        point3D.setX(x * Math.cos(angle) + z * Math.sin(angle));
                        point3D.setZ(-x * Math.sin(angle) + z * Math.cos(angle));
                        break;
                    case "z":
                        point3D.setX(x * Math.cos(angle) - y * Math.sin(angle));
                        point3D.setY(x * Math.sin(angle) + y * Math.cos(angle));
                        break;
                }
            }
        }
    }

    public void zoom(double delta) {
        dist += delta;
        if (dist < 0) {
            dist = 0;
        }
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public void changeRectangles(List<Rectangle> rects) {
        rectangles.clear();
        rectangles.addAll(rects);
    }
}
