module com.example.virtual_camera {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens virtual_camera to javafx.fxml;
    exports virtual_camera;
    exports virtual_camera.geometry;
    opens virtual_camera.geometry to javafx.fxml;
    exports virtual_camera.transformations;
    opens virtual_camera.transformations to javafx.fxml;
    exports virtual_camera.utils;
    opens virtual_camera.utils to javafx.fxml;
}