package hzt.model;

public class Theme {

    private final String name;
    private final String fileName;

    public Theme(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return name;
    }
}
