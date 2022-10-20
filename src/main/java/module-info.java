module pl.edu.pw.mini.gk_1 {
    requires javafx.controls;
    requires javafx.fxml;


    exports pl.edu.pw.mini.gk_1.app;
    opens pl.edu.pw.mini.gk_1.app to javafx.fxml;
}