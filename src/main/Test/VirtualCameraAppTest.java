import org.junit.jupiter.api.Test;
import virtual_camera.VirtualCameraApp;
import virtual_camera.geometry.Point2D;
import virtual_camera.geometry.Point3D;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VirtualCameraAppTest {
    @Test
    public void IsPointInPolygonTest() {
        VirtualCameraApp app = new VirtualCameraApp();
        Point2D p = new Point2D(5, 5);
        List<Point2D> polygon = new LinkedList<>();
        polygon.add(new Point2D(0,0));
        polygon.add(new Point2D(10,0));
        polygon.add(new Point2D(10,10));
        polygon.add(new Point2D(0,10));
        boolean result = app.isPointInPolygon(p, polygon);

        assertTrue(result);
    }

    @Test
    public void IsPointInPolygonBorderTest() {
        VirtualCameraApp app = new VirtualCameraApp();
        Point2D p = new Point2D(10, 0);
        List<Point2D> polygon = new LinkedList<>();
        polygon.add(new Point2D(0,0));
        polygon.add(new Point2D(10,0));
        polygon.add(new Point2D(10,10));
        polygon.add(new Point2D(0,10));
        boolean result = app.isPointInPolygon(p, polygon);

        assertTrue(result);
    }

    @Test
    public void IsNotPointInPolygonTest() {
        VirtualCameraApp app = new VirtualCameraApp();
        Point2D p = new Point2D(11, 0);
        List<Point2D> polygon = new LinkedList<>();
        polygon.add(new Point2D(0,0));
        polygon.add(new Point2D(10,0));
        polygon.add(new Point2D(10,10));
        polygon.add(new Point2D(0,10));
        boolean result = app.isPointInPolygon(p, polygon);

        assertFalse(result);
    }

    @Test
    public void IsPointInRownoleglobokTest() {
        VirtualCameraApp app = new VirtualCameraApp();
        Point2D p = new Point2D(14, 9);
        List<Point2D> polygon = new LinkedList<>();
        polygon.add(new Point2D(0,0));
        polygon.add(new Point2D(10,0));
        polygon.add(new Point2D(15,10));
        polygon.add(new Point2D(5,10));
        boolean result = app.isPointInPolygon(p, polygon);

        assertTrue(result);
    }
}