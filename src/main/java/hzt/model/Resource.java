package hzt.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Resource implements Comparable<Resource> {

    private final String name;
    private final String pathToResource;

    public Resource(String name) {
        this(name, "");
    }

    public Resource(String name, String pathToResource) {
        this.name = name;
        this.pathToResource = pathToResource;
    }

    public String getPathToResource() {
        return pathToResource;
    }

    @Override
    public int compareTo(@NotNull Resource o) {
        return name.compareTo(o.name);
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

    @Override
    public String toString() {
        return name;
    }

}
