package hzt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class AboutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutService.class);
    private static final String RELATIVE_TEXT_RESOURCE_DIR = "/about";

    public List<AboutText> loadContent() {
        return Optional.ofNullable(getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR))
                .map(URL::getFile)
                .map(File::new)
                .filter(File::isDirectory)
                .map(File::listFiles).stream()
                .flatMap(Arrays::stream)
                .map(AboutService::toAboutText)
                .collect(collectingAndThen(toList(), AboutService::checkIfTextsLoaded));
    }

    private static List<AboutText> checkIfTextsLoaded(List<AboutText> aboutTexts) {
        if (aboutTexts.isEmpty()) {
            LOGGER.error("Could not load content from " + RELATIVE_TEXT_RESOURCE_DIR + "...");
            aboutTexts.add(new AboutText("no content", ""));
        }
        return aboutTexts;
    }

    private static AboutText toAboutText(File file) {
        String name = file.getName().replace(".txt", "").replace("_", " ");
        return new AboutText(name, loadTextContent(file));
    }

    private static String loadTextContent(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            LOGGER.error("File with path {} not found...", file.toPath(), e);
            return "";
        }
    }

    public record AboutText(String title, String text) {

        @Override
        public String toString() {
            return title;
        }
    }
}
