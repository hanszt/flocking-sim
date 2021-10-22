module FlockingSim2D {

    requires org.apache.logging.log4j;
    requires javafx.base;
    requires javafx.graphics;
    requires lombok;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.jetbrains.annotations;

    exports hzt.view to javafx.graphics;
    opens hzt.controller.scenes to javafx.fxml;
    opens hzt.controller.sub_pane to javafx.fxml;
}
