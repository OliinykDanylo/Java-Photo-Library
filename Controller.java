package Controller;

import Model.Database;
import Model.Photo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Controller {
    private Database database;
    private Map<String, List<Photo>> albums;

    public Controller(Database database) {
        this.database = database;
        this.albums = new HashMap<>();
    }

    public void addPhoto(Photo photo) {
        database.addPhoto(photo);
    }

    public void removePhoto(Photo photo) {
        database.removePhoto(photo);
    }

    public List<Photo> getPhotos() {
        return database.getPhotos();
    }

    public void createAlbum(String albumName) {
        albums.putIfAbsent(albumName, new ArrayList<>());
    }

    public void addPhotoToAlbum(String albumName, Photo photo) {
        albums.get(albumName).add(photo);
    }

    public void removeAlbum(String albumName) {
        albums.remove(albumName);
    }

    public List<Photo> getPhotosFromAlbum(String albumName) {
        return albums.getOrDefault(albumName, new ArrayList<>());
    }

    public Map<String, List<Photo>> getAlbums() {
        return albums;
    }

    public void filterByDate(Date date, FilterCallback callback) {
        new Thread(() -> {
            List<Photo> result = database.getPhotos().stream()
                    .filter(photo -> date.equals(photo.getDate()))
                    .collect(Collectors.toList());
            try {
                Thread.sleep(1000); // for long-running task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onFilterComplete(result);
        }).start();
    }

    public void filterByLocation(String location, FilterCallback callback) {
        new Thread(() -> {
            Pattern pattern = Pattern.compile(location, Pattern.CASE_INSENSITIVE);
            List<Photo> result = database.getPhotos().stream()
                    .filter(photo -> {
                        Matcher matcher = pattern.matcher(photo.getLocation());
                        return matcher.find();
                    })
                    .collect(Collectors.toList());
            try {
                Thread.sleep(1000); // for long-running task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onFilterComplete(result);
        }).start();
    }

//    public void filterByTags(List<String> tags, boolean useAndLogic, FilterCallback callback) {
//        new Thread(() -> {
//            List<Photo> result;
//            if (useAndLogic) {
//                result = database.getPhotos().stream()
//                        .filter(photo -> photo.getTags() != null && tags.stream().allMatch(tag -> photo.getTags().contains(tag)))
//                        .collect(Collectors.toList());
//            } else {
//                result = database.getPhotos().stream()
//                        .filter(photo -> photo.getTags() != null && tags.stream().anyMatch(tag -> photo.getTags().contains(tag)))
//                        .collect(Collectors.toList());
//            }
//            try {
//                Thread.sleep(1000); // for long-running task
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            callback.onFilterComplete(result);
//        }).start();
//    }

    public void filterByTags(List<String> tags, boolean useAndLogic, FilterCallback callback) {
        new Thread(() -> {
            List<Photo> result;
            List<String> lowerCaseTags = tags.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            if (useAndLogic) {
                result = database.getPhotos().stream()
                        .filter(photo -> photo.getTags() != null &&
                                lowerCaseTags.stream().allMatch(tag ->
                                        photo.getTags().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(tag)))
                        .collect(Collectors.toList());
            } else {
                result = database.getPhotos().stream()
                        .filter(photo -> photo.getTags() != null &&
                                lowerCaseTags.stream().anyMatch(tag ->
                                        photo.getTags().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(tag)))
                        .collect(Collectors.toList());
            }

            try {
                Thread.sleep(1000); // for long-running task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onFilterComplete(result);
        }).start();
    }


    // interface for filtering
    public interface FilterCallback {
        void onFilterComplete(List<Photo> result);
    }
}