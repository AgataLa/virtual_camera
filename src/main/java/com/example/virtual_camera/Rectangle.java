package com.example.virtual_camera;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {
    private List<Point3D> points3D;
    private List<Point2D> points2D;

    public Rectangle() {
        points3D = new ArrayList<>();
        points2D = new ArrayList<>();
    }

    public void addPoint(Point3D point) {
        points3D.add(point);
    }

    public List<Point3D> getPoints3D() {
        return points3D;
    }

    public List<Point2D> getPoints2D() {
        return points2D;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "points=" + points3D +
                '}';
    }
}
