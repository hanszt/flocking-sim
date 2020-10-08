package hzt.controller;

import javafx.util.Duration;

public abstract class AppConstants {

    public static final String FXML_FILE_LOCATION = "/hzt/view/fxml/";
    public static final String TITLE = "2DDynamicsSim";
    public static final float STAGE_OPACITY = 0.8f;

    public enum Scene {

        MAIN_SCENE("mainScene.fxml", "Main Scene"),
        ABOUT_SCENE("aboutScene.fxml", "About Scene");

        private final String fxmlFileName;
        private final String englishDescription;

        Scene(String fxmlFileName, String englishDescription) {
            this.fxmlFileName = fxmlFileName;
            this.englishDescription = englishDescription;
        }

        public String getFxmlFileName() {
            return fxmlFileName;
        }

        public String getEnglishDescription() {
            return englishDescription;
        }
    }
}
