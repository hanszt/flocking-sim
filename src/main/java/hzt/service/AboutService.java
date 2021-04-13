package hzt.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.String.format;

public class AboutService {

    private static final Logger LOGGER = LogManager.getLogger(AboutService.class);
    private static final String RELATIVE_TEXT_RESOURCE_DIR = "../../about";

    public List<AboutText> loadContent() {
        List<AboutText> aboutTexts = new ArrayList<>();
        try {
            File fileDir = new File(getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR).getFile());
            if (fileDir.isDirectory()) {
                for (String fileName : fileDir.list()) {
                    String name = fileName.replace(".txt", "").replace("_", " ");
                    AboutText aboutText = new AboutText(name, new SimpleStringProperty(loadTextContent(fileName)));
                    aboutTexts.add(aboutText);
                }
            }
        }catch (NullPointerException e) {
            LOGGER.error("about folder not found...");
        }
        if (aboutTexts.isEmpty()) aboutTexts.add(new AboutText("no content", new SimpleStringProperty()));
        return aboutTexts;
    }

    private String loadTextContent(String fileName) {
        StringBuilder sb = new StringBuilder();
        List<String> fileTextContent = readInputFileByLine(getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR + "/" + fileName).getFile());
        fileTextContent.forEach(str -> sb.append(str).append(format("%n")));
        return sb.toString();
    }

    private static List<String> readInputFileByLine(String path) {
        List<String> inputList = new ArrayList<>();
        File file = new File(path);
        try (Scanner input = new Scanner(file)) {
            while (input.hasNextLine()) {
                inputList.add(input.nextLine());
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(() -> "File with path " + path + " not found...");
        }
        return inputList;
    }

    public static class AboutText {

        private final String title;
        private final StringProperty text;

        public AboutText(String title, StringProperty text) {
            this.title = title;
            this.text = text;
        }

        public String getText() {
            return text.get();
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
