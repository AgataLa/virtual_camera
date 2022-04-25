module com.example.virtual_camera {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens virtual_camera to javafx.fxml;
    exports virtual_camera;
}