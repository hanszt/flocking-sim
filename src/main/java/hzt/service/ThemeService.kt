package hzt.service

import hzt.model.Resource
import hzt.utils.firstCharUpperCase
import hzt.utils.onNewValue
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.slf4j.LoggerFactory
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class ThemeService : IThemeService {

    private val styleSheet: StringProperty = SimpleStringProperty()
    private val currentTheme: ObjectProperty<Resource> = SimpleObjectProperty()
    private val themes: Set<Resource>

    init {
        themes = scanForThemeStyleSheets()
        currentTheme.onNewValue(::loadThemeStyleSheet)
    }

    private fun scanForThemeStyleSheets(): Set<Resource> {
        return (javaClass.getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR)?.file
            ?.let(::File)
            ?.takeIf(File::isDirectory)
            ?.let(File::listFiles)
            ?.asSequence()
            ?.map(::toThemeResource)
            ?.toSortedSet() ?: sortedSetOf())
            .checkNotEmpty()
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
        return themes
    }

    companion object {
        private const val RELATIVE_STYLE_SHEET_RESOURCE_DIR = "/css"
        private val LOGGER = LoggerFactory.getLogger(ThemeService::class.java)
        private val DEFAULT_THEME = Resource("Light")

        private fun SortedSet<Resource>.checkNotEmpty(): Set<Resource> {
            if (this.isEmpty()) LOGGER.error("{} not found...", RELATIVE_STYLE_SHEET_RESOURCE_DIR)
            add(DEFAULT_THEME)
            return this
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
                .replace("style-", "")
                .replace('-', ' ')
                .replace(".css", "")
                .firstCharUpperCase()
        }
    }

}
