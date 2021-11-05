package hzt.service;

import hzt.model.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class BackgroundService implements IBackgroundService {

    public static final Resource NO_PICTURE = new Resource("No picture");
    private static final String RELATIVE_BG_IMAGES_RESOURCE_DIR = "/images/backgrounds";

    private static final Logger LOGGER = LogManager.getLogger(BackgroundService.class);

    private final Set<Resource> resources;

    public BackgroundService() {
        this.resources = scanForResourceImages();
    }

    private Set<Resource> scanForResourceImages() {
        Set<Resource> set = new TreeSet<>();
        set.add(NO_PICTURE);
        URL url = getClass().getResource(RELATIVE_BG_IMAGES_RESOURCE_DIR);
        if (url != null) {
            File styleDirectory = new File(url.getFile());
            if (styleDirectory.isDirectory()) {
                var files = styleDirectory.listFiles();
                for (var file : Objects.requireNonNull(files)) {
                    set.add(getResource(file));
                }
            }
        } else {
            LOGGER.error("Resource folder at " + RELATIVE_BG_IMAGES_RESOURCE_DIR + " not found...");
        }
        return set;
    }

    @NotNull
    private static Resource getResource(File file) {
        String name = extractName(file);
        try {
            return new Resource(name, file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String extractName(File file) {
        String parsedName = file.getName()
                .replace('_', ' ')
                .replace('-', ' ')
                .replace(".jpg", "")
                .replace(".png", "");
        return parsedName.substring(0, 1).toUpperCase() + parsedName.substring(1).toLowerCase();
    }

    public Set<Resource> getResources() {
        return Collections.unmodifiableSet(resources);
    }

}
