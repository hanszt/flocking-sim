package hzt.service;

import hzt.model.Resource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class BackgroundService implements IBackgroundService {

    public static final Resource NO_PICTURE = new Resource("No picture", "");
    private static final String RELATIVE_BG_IMAGES_RESOURCE_DIR = "/images/backgrounds";

    private static final Logger LOGGER = LogManager.getLogger(BackgroundService.class);

    private final Set<Resource> resources;

    public BackgroundService() {
        this.resources = scanForResourceImages();
    }

    private Set<Resource> scanForResourceImages() {
        Set<Resource> set = new HashSet<>();
        set.add(NO_PICTURE);
        URL url = getClass().getResource(RELATIVE_BG_IMAGES_RESOURCE_DIR);
        if (url != null) {
            File styleDirectory = new File(url.getFile());
            if (styleDirectory.isDirectory()) {
                String[] fileNames = styleDirectory.list();
                for (String fileName : fileNames) {
                    String name = extractName(fileName);
                    set.add(new Resource(name, fileName, RELATIVE_BG_IMAGES_RESOURCE_DIR + "/" + fileName));
                }
            }
        } else LOGGER.error("Resource folder at " + RELATIVE_BG_IMAGES_RESOURCE_DIR + " not found...");
        return set;
    }

    private String extractName(String fileName) {
        String parsedName = fileName
                .replace('_', ' ')
                .replace('-', ' ')
                .replace(".jpg", "")
                .replace(".png", "");
        parsedName = parsedName.substring(0, 1).toUpperCase() + parsedName.substring(1).toLowerCase();
        return parsedName;
    }

    public Set<Resource> getResources() {
        return resources;
    }

}
