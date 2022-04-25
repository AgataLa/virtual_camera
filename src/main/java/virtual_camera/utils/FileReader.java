package virtual_camera.utils;

import virtual_camera.geometry.Figure2D;
import virtual_camera.geometry.Point3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public List<Figure2D> loadFigures2D() {
        List<Figure2D> figure2DS = new ArrayList<>();
        Figure2D figure2D;
        File file = new File("src/main/resources/data.txt");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(";");
                figure2D = new Figure2D();
                for (String s : points) {
                    String[] coor = s.split(",");
                    double x = Double.parseDouble(coor[0]);
                    double y = Double.parseDouble(coor[1]);
                    double z = Double.parseDouble(coor[2]);
                    figure2D.addPoint(new Point3D(x, y, z));
                }
                figure2DS.add(figure2D);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return figure2DS;
    }
}
