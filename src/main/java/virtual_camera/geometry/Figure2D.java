package virtual_camera.geometry;

import javafx.scene.paint.Color;

import java.util.*;

import static virtual_camera.VirtualCameraApp.middle_w;

public class Figure2D {
    private List<Point3D> points3D;
    private List<Point2D> points2D;
    private Color color;
    private String id;
    private double[] xp, yp;
    private BoundingBox2D boundingBox2D;
    private double[] plane;

    public Figure2D() {
        points3D = new ArrayList<>();
        points2D = new ArrayList<>();
        boundingBox2D = new BoundingBox2D();
        plane = new double[4];
    }

    public void initXYArrays(int size) {
        xp = new double[size];
        yp = new double[size];
    }

    public void updateBoundingBox() {
        for(int i = 0; i < xp.length; i++) {
            xp[i] = points2D.get(i).getX();
            yp[i] = points2D.get(i).getY();
        }
        boundingBox2D.update(xp, yp);
    }

    public BoundingBox2D getBoundingBox2D() {
        return boundingBox2D;
    }

    public boolean intersect(Figure2D other) {
        return boundingBox2D.intersect(other.getBoundingBox2D());
    }

    public void determinePlane() {
        double[] a = new double[3];
        double[] b = new double[3];

        a[0] = points3D.get(1).getX() - points3D.get(0).getX();
        a[1] = points3D.get(1).getY() - points3D.get(0).getY();
        a[2] = points3D.get(1).getZ() - points3D.get(0).getZ();
        b[0] = points3D.get(2).getX() - points3D.get(1).getX();
        b[1] = points3D.get(2).getY() - points3D.get(1).getY();
        b[2] = points3D.get(2).getZ() - points3D.get(1).getZ();

        plane[0] = a[1]*b[2] - a[2]*b[1];
        plane[1] = a[2]*b[0] - a[0]*b[2];
        plane[2] = a[0]*b[1] - a[1]*b[0];
        plane[3] = -plane[0]*points3D.get(0).getX() - plane[1]*points3D.get(0).getY() - plane[2]*points3D.get(0).getZ();
    }

    public double checkMaxDistanceFromObserver() {
        double max = Integer.MIN_VALUE;
        double dist;
        for(Point3D p: points3D) {
            dist = Math.sqrt(Math.pow(p.getX(),2) + Math.pow(p.getY(),2) + Math.pow(p.getZ(),2));
            if(dist > max) {
                max = dist;
            }
        }
        return max;
    }

    public boolean checkIfMinXIsLowerThanOther(Figure2D other) {
        double otherXMin = Math.min(Math.abs(other.getBoundingBox2D().getxMax()-middle_w), Math.abs(other.getBoundingBox2D().getxMin()-middle_w));
        double thisXMin = Math.min(Math.abs(getBoundingBox2D().getxMax()-middle_w), Math.abs(getBoundingBox2D().getxMin()-middle_w));
        return thisXMin < otherXMin;
    }

    public int checkIfIsInFrontOf(Figure2D otherFigure) {
        double side;
        boolean isInFrontOf = false;
        boolean first = true;
        boolean commonPoint = false;

        if(isPlaneTheSame(otherFigure.plane)) {
            return -1;
        }

        boolean origin = plane[3] < 0;

        for(Point3D p: otherFigure.points3D) {
            for(Point3D p3d: points3D) {
                if(p3d.equals(p)) {
                    commonPoint = true;
                    break;
                }
                commonPoint = false;
            }
            if(!commonPoint) {
                side = plane[0] * p.getX() + plane[1] * p.getY() + plane[2] * p.getZ() + plane[3];

                if (side == 0) {
                    continue;
                }
                if (first) {
                    isInFrontOf = side < 0;
                    first = false;
                } else {
                    if (isInFrontOf != side < 0) {
                        return 0;
                    }
                }
            }
        }
        return origin == isInFrontOf ? -1 : 1;
    }

    public boolean isPlaneTheSame(double[] plane) {
        for(int i = 0; i < plane.length; i++) {
            if(!(Math.abs(plane[i] - this.plane[i]) < 0.00001d)) {
                return false;
            }
        }
        return true;
    }

    public double[] getSortedZ() {
        double[] zz = new double[points3D.size()];
        for(int i = 0; i < zz.length; i++) {
            zz[i] = points3D.get(i).getZ();
        }
        Arrays.sort(zz);
        return zz;
    }

    public double getMinZ() {
        return getSortedZ()[getSortedZ().length-1];
    }

    public void addPoint(Point3D point) {
        points3D.add(point);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public double[] getXp() {
        return xp;
    }

    public double[] getYp() {
        return yp;
    }

    public List<Point3D> getPoints3D() {
        return points3D;
    }

    public List<Point2D> getPoints2D() {
        return points2D;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Figure2D{" +
                "points=" + points3D +
                '}';
    }
}