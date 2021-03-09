package hzt.service;

import hzt.model.Theme;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public interface IThemeService {

    Theme DEFAULT_THEME = new Theme("Light", "");
    Theme DARK_THEME = new Theme("Dark", "style-dark.css");

    Iterable<Theme> getThemes();

    ObjectProperty<Theme> currentThemeProperty();

    StringProperty styleSheetProperty();

}
