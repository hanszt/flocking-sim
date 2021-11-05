package hzt.service

import hzt.model.Resource
import hzt.utils.FxKtUtils.collectAndThen
import hzt.utils.FxKtUtils.firstLetterUpperCase
import hzt.utils.FxKtUtils.onNewValue
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.apache.logging.log4j.LogManager
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.stream.Collectors.toCollection

class ThemeService : IThemeService {

    private val styleSheet: StringProperty = SimpleStringProperty()
    private val currentTheme: ObjectProperty<Resource> = SimpleObjectProperty()
    private val themes: Set<Resource>

    init {
        themes = scanForThemeStyleSheets()
        currentTheme.onNewValue(::loadThemeStyleSheet)
    }

    private fun scanForThemeStyleSheets(): Set<Resource> {
        return Optional.ofNullable(javaClass.getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR))
            .map(URL::getFile)
            .map(::File)
            .filter(File::isDirectory)
            .map(File::listFiles)
            .stream()
            .flatMap(Arrays::stream)
            .map(::toThemeResource)
            .collectAndThen((toCollection(::TreeSet)), ::checkNotEmpty)
    }

    private fun loadThemeStyleSheet(theme: Resource) {
        Optional.ofNullable(theme.url)
            .map(URL::toExternalForm)
            .ifPresentOrElse(styleSheet::set) { LOGGER.error("stylesheet of $theme could not be loaded...") }
    }

    override fun styleSheetProperty(): StringProperty {
        return styleSheet
    }

    override fun currentThemeProperty(): ObjectProperty<Resource> {
        return currentTheme
    }

    override fun getThemes(): Set<Resource> {
        return Collections.unmodifiableSet(themes)
    }

    companion object {
        private const val RELATIVE_STYLE_SHEET_RESOURCE_DIR = "/css"
        private val LOGGER = LogManager.getLogger(ThemeService::class.java)
        private val DEFAULT_THEME = Resource("Light",
            ThemeService::class.java.getResource("$RELATIVE_STYLE_SHEET_RESOURCE_DIR/style-light.css")
        )

        private fun checkNotEmpty(set: MutableSet<Resource>): Set<Resource> {
            if (set.isEmpty()) {
                LOGGER.error("{} not found...", RELATIVE_STYLE_SHEET_RESOURCE_DIR)
            }
            set.add(DEFAULT_THEME)
            return set
        }

        private fun toThemeResource(file: File): Resource {
            return try {
                Resource(extractThemeName(file.name), file.toURI().toURL())
            } catch (e: MalformedURLException) {
                throw IllegalStateException(e)
            }
        }

        private fun extractThemeName(fileName: String): String {
            return fileName
                .replace('-', ' ')
                .replace(".css", "")
                .replace("style-", "")
                .firstLetterUpperCase()
        }
    }

}
