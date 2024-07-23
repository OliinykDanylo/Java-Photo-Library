package GUI;

import Controller.Controller;
import Model.Photo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class PhotoDownloader extends JPanel {
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JLabel imageLabel;
    private Map<String, Photo> photoMap;
    private JButton closeButton;
    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 400;

    private Controller controller;
    private JPopupMenu popupMenu;
    private JMenuItem getInfoItem;
    private JMenu addToAlbumMenu;
    private JMenuItem deleteItem;
    private Photo selectedPhoto;

    public PhotoDownloader(Controller controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        photoMap = new HashMap<>();

        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeImage());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(imageLabel, BorderLayout.CENTER);
        rightPanel.add(closeButton, BorderLayout.SOUTH);

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = fileList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String fileName = listModel.getElementAt(index);
                        new Thread(() -> displayImage(photoMap.get(fileName))).start();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleContextMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleContextMenu(e);
            }

            private void handleContextMenu(MouseEvent e) {
                if (e.isPopupTrigger() || (e.isControlDown() && SwingUtilities.isRightMouseButton(e))) {
                    int index = fileList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        fileList.setSelectedIndex(index);
                        String fileName = listModel.getElementAt(index);
                        selectedPhoto = photoMap.get(fileName);
                        updateAlbumMenu();
                        popupMenu.show(fileList, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(fileList);
        JScrollPane imageScrollPane = new JScrollPane(rightPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, imageScrollPane);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        // Add a component listener to resize the image when the window is resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeImage();
            }
        });

        // Create the popup menu
        popupMenu = new JPopupMenu();
        getInfoItem = new JMenuItem("Get Info");
        getInfoItem.addActionListener(e -> showPhotoInfo(selectedPhoto));
        popupMenu.add(getInfoItem);

        deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deletePhoto());
        popupMenu.add(deleteItem);

        popupMenu.addSeparator();;

        addToAlbumMenu = new JMenu("Add to Album");
        popupMenu.add(addToAlbumMenu);
    }

    private void displayImage(Photo photo) {
        try {
            BufferedImage img = ImageIO.read(new File(photo.getFilePath()));
            if (img != null) {
                Image scaledImage = getScaledImage(img, IMAGE_WIDTH, IMAGE_HEIGHT);
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText(null);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading image: " + photo.getFilePath(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Error loading image: " + photo.getFilePath(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void resizeImage() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image originalImage = icon.getImage();
            imageLabel.setIcon(new ImageIcon(getScaledImage((BufferedImage) originalImage, IMAGE_WIDTH, IMAGE_HEIGHT)));
        }
    }

    private Image getScaledImage(BufferedImage img, int width, int height) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (imgWidth > width) {
            imgHeight = (imgHeight * width) / imgWidth;
            imgWidth = width;
        }
        if (imgHeight > height) {
            imgWidth = (imgWidth * height) / imgHeight;
            imgHeight = height;
        }
        Image scaledImage = img.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
        return scaledImage;
    }

    private void updateAlbumMenu() {
        addToAlbumMenu.removeAll();
        Map<String, List<Photo>> albums = controller.getAlbums();
        for (String albumName : albums.keySet()) {
            JMenuItem albumItem = new JMenuItem(albumName);
            albumItem.addActionListener(e -> addPhotoToAlbum(albumName));
            addToAlbumMenu.add(albumItem);
        }
    }

    private void addPhotoToAlbum(String albumName) {
        if (selectedPhoto != null) {
            controller.addPhotoToAlbum(albumName, selectedPhoto);
            JOptionPane.showMessageDialog(this, "Photo added to album: " + albumName, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deletePhoto() {
        if (selectedPhoto != null) {
            controller.removePhoto(selectedPhoto);
            controller.getAlbums().values().forEach(album -> album.remove(selectedPhoto));
            synchronized (this) {
                listModel.removeElement(selectedPhoto.getTitle());
                photoMap.remove(selectedPhoto.getTitle());
            }
            closeImage();
            JOptionPane.showMessageDialog(this, "Photo deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void addPhoto(Photo photo) {
        synchronized (this) {
            listModel.addElement(photo.getTitle());
            photoMap.put(photo.getTitle(), photo);
        }
    }

    public void clearPhotos() {
        synchronized (this) {
            listModel.clear();
            photoMap.clear();
        }
    }

    private void closeImage() {
        SwingUtilities.invokeLater(() -> {
            imageLabel.setIcon(null);
            imageLabel.setText("");
        });
    }

    private void showPhotoInfo(Photo photo) {
        if (photo != null) {
            Date date = photo.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            String formattedDate = dateFormat.format(date);

            String info = String.format("Title: %s\nLocation: %s\nDate: %s\nTags: %s\nFile Path: %s",
                    photo.getTitle(), photo.getLocation(), formattedDate, photo.getTags(), photo.getFilePath());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, info, "Photo Info", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
}