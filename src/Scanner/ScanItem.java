package Scanner;

import java.time.LocalDateTime;

public class ScanItem {

    private String fileName;
    private String path;
    private Long size;
    private LocalDateTime date;
    private boolean isFolder;
    private Long counterOfItems;

    public ScanItem() {
    }

    public ScanItem(String fileName, String path, Long size, LocalDateTime date, boolean isFolder, Long counterOfItems) {
        this.fileName = fileName;
        this.path = path;
        this.size = size;
        this.date = date;
        this.isFolder = isFolder;
        this.counterOfItems = counterOfItems;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public Long getCounterOfItems() {
        return counterOfItems;
    }

    public void setCounterOfItems(Long counterOfItems) {
        this.counterOfItems = counterOfItems;
    }
}
