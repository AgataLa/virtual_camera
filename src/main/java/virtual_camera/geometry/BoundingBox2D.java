package virtual_camera.geometry;

import java.util.Arrays;

public class BoundingBox2D {
    private double xMin, xMax, yMin, yMax;

    public void update(double[] x, double[] y) {
        xMin = Arrays.stream(x).min().getAsDouble();
        yMin = Arrays.stream(y).min().getAsDouble();
        xMax = Arrays.stream(x).max().getAsDouble();
        yMax = Arrays.stream(y).max().getAsDouble();
    }

    public boolean intersect(BoundingBox2D other) {
        return !((other.xMin > xMax || other.xMax < xMin) || (other.yMax < yMin || other.yMin > yMax));
    }

    public double getxMin() {
        return xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public double getyMax() {
        return yMax;
    }

    @Override
    public String toString() {
        return "BoundingBox2D{" +
                "xMin=" + xMin +
                ", xMax=" + xMax +
                ", yMin=" + yMin +
                ", yMax=" + yMax +
                '}';
    }
}
