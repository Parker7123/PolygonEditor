module pl.edu.pw.mini.gk_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.edu.pw.mini.gk_1 to javafx.fxml;
    exports pl.edu.pw.mini.gk_1;
}