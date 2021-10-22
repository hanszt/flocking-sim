package hzt.service;

import hzt.model.Resource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class ThemeService implements IThemeService {

    private static final String RELATIVE_STYLE_SHEET_RESOURCE_DIR = "/css";

    private static final Logger LOGGER = LogManager.getLogger(ThemeService.class);
    public static final Resource DEFAULT_THEME = new Resource("Light",
            RELATIVE_STYLE_SHEET_RESOURCE_DIR + "/style-light.css");

    private final StringProperty styleSheet = new SimpleStringProperty();
    private final ObjectProperty<Resource> currentTheme = new SimpleObjectProperty<>();

    private final Set<Resource> themes;

    public ThemeService() {
        this.themes = scanForThemeStyleSheets();
        currentTheme.addListener((observableValue, theme, newTheme) -> loadThemeStyleSheet(newTheme));
    }

    private Set<Resource> scanForThemeStyleSheets() {
        final var themeResources = Optional.ofNullable(getClass().getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR))
                .map(URL::getFile)
                .map(File::new)
                .filter(File::isDirectory)
                .map(File::list)
                .stream()
                .flatMap(Arrays::stream)
                .map(ThemeService::toThemeResource)
                .toList();

        if (themeResources.isEmpty()) {
            LOGGER.error("{} not found...", RELATIVE_STYLE_SHEET_RESOURCE_DIR);
        }
        Set<Resource> themeSet = new TreeSet<>(themeResources);
        themeSet.add(DEFAULT_THEME);
        return themeSet;
    }

    private static Resource toThemeResource(String fileName) {
        String themeName = extractThemeName(fileName);
        return new Resource(themeName, RELATIVE_STYLE_SHEET_RESOURCE_DIR + "/" + fileName);
    }

    private static String extractThemeName(String fileName) {
        String themeName = fileName
                .replace("style-", "")
                .replace('-', ' ')
                .replace(".css", "");
        themeName = themeName.substring(0, 1).toUpperCase() + themeName.substring(1).toLowerCase();
        return themeName;
    }

    private void loadThemeStyleSheet(Resource theme) {
            URL styleSheetUrl = getClass().getResource(theme.getPathToResource());
            if (styleSheetUrl != null) {
                LOGGER.debug(styleSheetUrl);
                this.styleSheet.set(styleSheetUrl.toExternalForm());
                LOGGER.debug(styleSheetUrl::toExternalForm);
            }else {
                LOGGER.error(() -> "stylesheet of " + theme.getPathToResource() + " could not be loaded...");
            }

    }

    @Override
    public StringProperty styleSheetProperty() {
        return styleSheet;
    }

    @Override
    public ObjectProperty<Resource> currentThemeProperty() {
        return currentTheme;
    }

    @Override
    public Set<Resource> getThemes() {
        return Collections.unmodifiableSet(themes);
    }

}
