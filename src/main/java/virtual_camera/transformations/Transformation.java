package virtual_camera.transformations;

import virtual_camera.geometry.Figure2D;
import virtual_camera.geometry.Point2D;
import virtual_camera.geometry.Point3D;

import java.util.List;

import static virtual_camera.VirtualCameraApp.*;

public class Transformation {
    private double dist;
    private List<Figure2D> figure2DS;

    public Transformation(List<Figure2D> figure2DS) {
        dist = DIST;
        this.figure2DS = figure2DS;
    }

    public void projection() {
        Point2D point2D;
        double x, y;
        for (Figure2D fig : figure2DS) {
            fig.getPoints2D().clear();
            for (Point3D point3D : fig.getPoints3D()) {
                x = point3D.getX() * dist / (point3D.getZ() > 0 ? point3D.getZ() : 0.01);
                y = point3D.getY() * dist / (point3D.getZ() > 0 ? point3D.getZ() : 0.01);
                point2D = new Point2D(x + middle_w, -y + middle_h);
                fig.getPoints2D().add(point2D);
            }
        }
    }

    public void translate(double delta, String axis) {
        for (Figure2D fig : figure2DS) {
            for (Point3D point3D : fig.getPoints3D()) {
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
        for (Figure2D fig : figure2DS) {
            for (Point3D point3D : fig.getPoints3D()) {
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

    public void changeFigures(List<Figure2D> figs) {
        figure2DS.clear();
        figure2DS.addAll(figs);
    }
}
