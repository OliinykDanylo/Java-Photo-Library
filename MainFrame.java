package GUI;

import Controller.Controller;
import Model.Database;
import Model.Photo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private PhotoDownloader photoDownloader;
    private Controller controller;
    private Database database;
    private JPopupMenu albumPopupMenu;
    private JMenuItem deleteAlbumItem;
    private String selectedAlbum;

    public MainFrame() {
        setTitle("Photo Management Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        database = new Database();
        controller = new Controller(database);
        photoDownloader = new PhotoDownloader(controller);

        setJMenuBar(createMenuBar());

        add(photoDownloader, BorderLayout.CENTER);

        loadAllPhotos();

        createAlbumContextMenu();

        setVisible(true);
    }

    private void createAlbumContextMenu() {
        albumPopupMenu = new JPopupMenu();
        deleteAlbumItem = new JMenuItem("Delete Album");
        deleteAlbumItem.addActionListener(e -> {
            System.out.println("Delete button clicked for album: " + selectedAlbum);
            deleteAlbum(selectedAlbum);
        });
        albumPopupMenu.add(deleteAlbumItem);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> loadAllPhotos());
        backButton.setFocusable(false);
        InputMap inputMap = backButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = backButton.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "BACK_ACTION");
        actionMap.put("BACK_ACTION", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllPhotos();
            }
        });

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem importMenuItem = new JMenuItem("Import");
        JMenuItem exitItem = new JMenuItem("EXIT");

        importMenuItem.addActionListener(e -> openPhotoDetailsDialog());
        importMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> {
            int action = JOptionPane.showConfirmDialog(MainFrame.this, "Do you really want to exit the app?",
                    "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);

            if (action == JOptionPane.OK_OPTION) {
                System.exit(0);
            }
        });
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));

        fileMenu.add(importMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Sort menu
        JMenu sortMenu = new JMenu("Sort");
        JMenuItem byDate = new JMenuItem("by Date");
        JMenuItem byLocation = new JMenuItem("by Location");
        JMenuItem byTag = new JMenuItem("by Tag");

        byLocation.addActionListener(e -> filterByLocation());
        byLocation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        byDate.addActionListener(e -> filterByDate());
        byDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        byTag.addActionListener(e -> filterByTags());
        byTag.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));

        sortMenu.add(byDate);
        sortMenu.add(byLocation);
        sortMenu.add(byTag);

        // Albums menu
        JMenu albumMenu = new JMenu("Albums");
        updateAlbumMenu(albumMenu);

        // Add menus to the menu bar
        menuBar.add(backButton);
        menuBar.add(fileMenu);
        menuBar.add(sortMenu);
        menuBar.add(albumMenu);

        return menuBar;
    }

    private void openPhotoDetailsDialog() {
        PhotoDetailsDialog dialog = new PhotoDetailsDialog(this);
        dialog.setFormListener(e -> {
            Photo photo = e.getPhoto();
            photoDownloader.addPhoto(photo);
            controller.addPhoto(photo);
        });
        dialog.setVisible(true);
    }

    private void filterByDate() {
        DateInputDialog dialog = new DateInputDialog(this);
        dialog.setVisible(true);

        Date date = dialog.getDate();
        if (date != null) {
            controller.filterByDate(date, result -> updatePhotoList(result));
        }
    }

    private void filterByTags() {
        TagsInputDialog dialog = new TagsInputDialog(this);
        dialog.setVisible(true);

        List<String> tags = dialog.getTags();
        boolean useAndLogic = dialog.isUseAndLogic();
        if (tags != null) {
            controller.filterByTags(tags, useAndLogic, result -> updatePhotoList(result));
        }
    }

    private void filterByLocation() {
        LocationInputDialog dialog = new LocationInputDialog(this);
        dialog.setVisible(true);

        String location = dialog.getDialogLocation();
        if (location != null) {
            controller.filterByLocation(location, result -> updatePhotoList(result));
        }
    }

    private void loadAllPhotos() {
        List<Photo> allPhotos = controller.getPhotos();
        updatePhotoList(allPhotos);
    }

    private void updatePhotoList(List<Photo> photos) {
        SwingUtilities.invokeLater(() -> {
            photoDownloader.clearPhotos();
            for (Photo photo : photos) {
                photoDownloader.addPhoto(photo);
            }
        });
    }

    private void updateAlbumMenu(JMenu albumMenu) {
        albumMenu.removeAll();
        Map<String, List<Photo>> albums = controller.getAlbums();
        System.out.println("Albums before update: " + albums.keySet());

        for (String albumName : albums.keySet()) {
            JMenuItem albumItem = new JMenuItem(albumName);
            albumItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger() || (e.isControlDown() && SwingUtilities.isRightMouseButton(e))) {
                        selectedAlbum = albumName;
                        System.out.println("Context menu requested for album: " + albumName);
                        albumPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger() || (e.isControlDown() && SwingUtilities.isRightMouseButton(e))) {
                        selectedAlbum = albumName;
                        System.out.println("Context menu requested for album: " + albumName);
                        albumPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
            albumItem.addActionListener(e -> displayAlbum(albumName));
            albumMenu.add(albumItem);
        }

        albumMenu.addSeparator();

        JMenuItem createAlbumItem = new JMenuItem("Create New Album");
        createAlbumItem.addActionListener(e -> createNewAlbum());
        createAlbumItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        albumMenu.add(createAlbumItem);

        System.out.println("Albums after update: " + albums.keySet());
    }

    private void deleteAlbum(String albumName) {
        int response = JOptionPane.showConfirmDialog(this, "Do you really want to delete the album: " + albumName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.out.println("Deleting album: " + albumName);
            controller.removeAlbum(albumName);
            JMenu albumMenu = (JMenu) getJMenuBar().getComponent(3);
            updateAlbumMenu(albumMenu);
        }
    }

    private void displayAlbum(String albumName) {
        List<Photo> albumPhotos = controller.getPhotosFromAlbum(albumName);
        JDialog albumDialog = new JDialog(this, "Album: " + albumName, true);
        PhotoViewer photoViewer = new PhotoViewer(albumPhotos);
        albumDialog.add(photoViewer);
        albumDialog.setSize(600, 400);
        albumDialog.setLocationRelativeTo(this);
        albumDialog.setVisible(true);
    }

    private void createNewAlbum() {
        String albumName = JOptionPane.showInputDialog(this, "Enter album name:");
        if (albumName != null && !albumName.trim().isEmpty()) {
            controller.createAlbum(albumName);
            JMenu albumMenu = (JMenu) getJMenuBar().getComponent(3);
            updateAlbumMenu(albumMenu);
        }
    }
}