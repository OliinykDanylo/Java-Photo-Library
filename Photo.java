package Model;

import java.util.Date;
import java.util.List;

public class Photo {
    private String filePath;
    private String title;
    private List<String> tags;
    private Date date;
    private String location;

    public Photo(String filePath, String title, List<String> tags, Date date, String location) {
        this.filePath = filePath;
        this.title = title;
        this.tags = tags;
        this.date = date;
        this.location = location;
    }

    // Getters and setters
    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Photo:\n" +
                "filePath='" + filePath + '\n' +
                "title='" + title + '\n' +
                "tags=" + tags + '\n' +
                "date=" + date + '\n' +
                "location='" + location + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (getFilePath() != null ? !getFilePath().equals(photo.getFilePath()) : photo.getFilePath() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(photo.getTitle()) : photo.getTitle() != null) return false;
        if (getTags() != null ? !getTags().equals(photo.getTags()) : photo.getTags() != null) return false;
        if (getDate() != null ? !getDate().equals(photo.getDate()) : photo.getDate() != null) return false;
        return getLocation() != null ? getLocation().equals(photo.getLocation()) : photo.getLocation() == null;
    }

    @Override
    public int hashCode() {
        int result = getFilePath() != null ? getFilePath().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getTags() != null ? getTags().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        return result;
    }
}