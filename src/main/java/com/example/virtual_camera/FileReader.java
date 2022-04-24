package com.example.virtual_camera;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public List<Rectangle> loadRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();
        Rectangle rectangle;
        File file = new File("src/main/resources/data.txt");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(";");
                rectangle = new Rectangle();
                for (String s : points) {
                    String[] coor = s.split(",");
                    double x = Double.parseDouble(coor[0]);
                    double y = Double.parseDouble(coor[1]);
                    double z = Double.parseDouble(coor[2]);
                    rectangle.addPoint(new Point3D(x, y, z));
                }
                rectangles.add(rectangle);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return rectangles;
    }
}
