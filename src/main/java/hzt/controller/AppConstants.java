package hzt.controller;

public class AppConstants {

    public AppConstants() {
    }

    public static final String FXML_FILE_LOCATION = "/hzt/view/fxml/";

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BRIGHT_RED = "\u001B[91m";

    public static final String TITLE = "Flocking Simulation";
    public static final String DOTTED_LINE = "----------------------------------------------------------------------------------------\n";
    public static final String CLOSING_MESSAGE = ANSI_BLUE +
            "See you next Time! :)" +
            ANSI_RESET;
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
