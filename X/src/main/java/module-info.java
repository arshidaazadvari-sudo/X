module com.example.ap_final_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ap_final_project to javafx.fxml;
    exports com.example.ap_final_project;
}