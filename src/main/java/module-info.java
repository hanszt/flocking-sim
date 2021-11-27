import hzt.controller.FXMLController;
import hzt.service.IThemeService;

open module FlockingSim2D {

    requires org.apache.logging.log4j;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.jetbrains.annotations;
    requires kotlin.stdlib;

    exports hzt.view to javafx.graphics;

    //These 'uses' clauses are necessary for the serviceloader tests
    uses FXMLController;
    uses IThemeService;
}
