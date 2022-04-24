module com.example.virtual_camera {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.virtual_camera to javafx.fxml;
    exports com.example.virtual_camera;
}