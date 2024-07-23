package GUI;

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
import java.util.Date;
import java.util.List;

public class PhotoViewer extends JPanel {
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JLabel imageLabel;
    private List<Photo> photos;
    private JPopupMenu popupMenu;
    private JMenuItem getInfoItem;
    private JMenuItem deleteItem;
    private Photo selectedPhoto;

    public PhotoViewer(List<Photo> photos) {
        this.photos = photos;
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        for (Photo photo : photos) {
            listModel.addElement(photo.getTitle());
        }

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = fileList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        displayImage(photos.get(index));
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
                        selectedPhoto = photos.get(index);
                        popupMenu.show(fileList, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(fileList);
        JScrollPane imageScrollPane = new JScrollPane(imageLabel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, imageScrollPane);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        getInfoItem = new JMenuItem("Get Info");
        getInfoItem.addActionListener(e -> showPhotoInfo(selectedPhoto));
        popupMenu.add(getInfoItem);

        deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deletePhoto());
        popupMenu.add(deleteItem);
    }

    private void displayImage(Photo photo) {
        try {
            BufferedImage img = ImageIO.read(new File(photo.getFilePath()));
            if (img != null) {
                Image scaledImage = getScaledImage(img, imageLabel.getWidth(), imageLabel.getHeight());
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setText(null);  // Clear any previous text
            } else {
                JOptionPane.showMessageDialog(this, "Error loading image: " + photo.getFilePath(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + photo.getFilePath(), "Error",
                    JOptionPane.ERROR_MESSAGE);
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

    private void showPhotoInfo(Photo photo) {
        if (photo != null) {
            // Format the date
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

    private void deletePhoto() {
        if (selectedPhoto != null) {
            photos.remove(selectedPhoto);
            listModel.removeElement(selectedPhoto.getTitle());
            JOptionPane.showMessageDialog(this, "Photo deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
            closeImage();
        }
    }

    private void closeImage() {
        SwingUtilities.invokeLater(() -> {
            imageLabel.setIcon(null);
            imageLabel.setText("");
        });
    }
}