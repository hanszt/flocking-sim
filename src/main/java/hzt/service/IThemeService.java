package hzt.service;

import hzt.model.Resource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.Set;

public interface IThemeService {

    Resource DEFAULT_THEME = new Resource("Light");

    Set<Resource> getThemes();

    ObjectProperty<Resource> currentThemeProperty();

    StringProperty styleSheetProperty();

}
