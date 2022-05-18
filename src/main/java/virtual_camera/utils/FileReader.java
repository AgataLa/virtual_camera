package virtual_camera.utils;

import javafx.scene.paint.Color;
import virtual_camera.geometry.Figure2D;
import virtual_camera.geometry.Point3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {
    private final List<Color> colorsFill;

    public FileReader() {
        colorsFill = Arrays.asList(Color.PALEVIOLETRED, Color.DEEPSKYBLUE, Color.FORESTGREEN, Color.DARKORANGE,
                Color.MEDIUMPURPLE, Color.DARKRED, Color.SIENNA, Color.DARKSALMON, Color.DODGERBLUE,
                Color.LIGHTCYAN, Color.YELLOWGREEN, Color.PLUM, Color.PEACHPUFF, Color.KHAKI);
    }

    public List<Figure2D> loadFigures2D() {
        List<Figure2D> figure2DS = new ArrayList<>();
        Figure2D figure2D;
        File file = new File("src/main/resources/data_fill.txt");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(";");
                figure2D = new Figure2D();
                for(int i = 0; i < lineSplit.length - 2; i++) {
                    String[] coor = lineSplit[i].split(",");
                    double x = Double.parseDouble(coor[0].trim());
                    double y = Double.parseDouble(coor[1].trim());
                    double z = Double.parseDouble(coor[2].trim());
                    figure2D.addPoint(new Point3D(x, y, z));
                }
                int color = Integer.parseInt(lineSplit[lineSplit.length-2].trim());
                String id = lineSplit[lineSplit.length-2].trim() + lineSplit[lineSplit.length-1].trim();
                figure2D.setColor(colorsFill.get(color % colorsFill.size()));
                figure2D.initXYArrays(lineSplit.length - 2);
                figure2D.determinePlane();
                figure2D.setId(id);
                figure2DS.add(figure2D);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return figure2DS;
    }
}
