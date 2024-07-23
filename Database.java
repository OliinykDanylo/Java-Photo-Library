package Model;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Photo> photos;

    public Database() {
        this.photos = new ArrayList<>();
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public List<Photo> getPhotos() {
        return new ArrayList<>(photos);
    }
}
