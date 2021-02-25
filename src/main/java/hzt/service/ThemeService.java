package hzt.service;

import hzt.model.Theme;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ThemeService {

    private static final String RELATIVE_STYLE_SHEET_RESOURCE_DIR = "../../css";

    private static final Logger LOGGER = LogManager.getLogger(ThemeService.class);
    public static final Theme DEFAULT_THEME = new Theme("Light", "style-light.css");

    private final StringProperty styleSheet = new SimpleStringProperty();
    private final ObjectProperty<Theme> currentTheme = new SimpleObjectProperty<>();

    private final Set<Theme> themes;

    public ThemeService() {
        this.themes = scanForThemeStyleSheets();
        currentTheme.addListener((observableValue, theme, newTheme) -> loadThemeStyleSheet(newTheme));
    }

    private Set<Theme> scanForThemeStyleSheets() {
        Set<Theme> themeSet = new HashSet<>(Set.of(DEFAULT_THEME));
        URL url = getClass().getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR);
        if (url != null) {
            File styleDirectory = new File(url.getFile());
            if (styleDirectory.isDirectory()) {
                String[] fileNames = styleDirectory.list();
                for (String fileName : fileNames) {
                    String themeName = extractThemeName(fileName);
                    themeSet.add(new Theme(themeName, fileName));
                }
            }
        } else LOGGER.error("Stylesheet resource folder not found...");
        return themeSet;
    }

    private String extractThemeName(String fileName) {
        String themeName = fileName
                .replace("style-", "")
                .replace('-', ' ')
                .replace(".css", "");
        themeName = themeName.substring(0, 1).toUpperCase() + themeName.substring(1).toLowerCase();
        return themeName;
    }

    private void loadThemeStyleSheet(Theme theme) {
        try {
            URL styleSheetUrl = getClass()
                    .getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR + "/" + theme.getFileName());
            LOGGER.debug(styleSheetUrl);
            this.styleSheet.set(styleSheetUrl.toExternalForm());
            LOGGER.debug(styleSheetUrl.toExternalForm());
        } catch (NullPointerException e) {
            LOGGER.error("stylesheet of " + theme.getName() + " could not be loaded...");
        }
    }

    public String getStyleSheet() {
        return styleSheet.get();
    }

    public ObjectProperty<Theme> currentThemeProperty() {
        return currentTheme;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

}
