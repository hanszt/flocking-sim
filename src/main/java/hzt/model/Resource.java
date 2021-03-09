package hzt.model;

import java.util.Objects;

public class Resource {

    private final String name;
    private final String fileName;
    private final String pathToResource;

    public Resource(String name, String fileName) {
        this(name, fileName, "");
    }

    public Resource(String name, String fileName, String pathToResource) {
        this.name = name;
        this.fileName = fileName;
        this.pathToResource = pathToResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPathToResource() {
        return pathToResource;
    }

    @Override
    public String toString() {
        return name;
    }
}
