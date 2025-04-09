module com.example.demo.government_tax_department_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demo.government_tax_department_system to javafx.fxml;
    exports com.example.demo.government_tax_department_system;
}