package GUI;

import Model.Photo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

public class PhotoDetailsDialog extends JDialog {
    private JLabel photoLabel;
    private JTextField titleField;
    private JTextField locationField;
    private JTextField dateField;
    private JTextField tagField;
    private DefaultListModel<String> tagListModel;
    private JList<String> tagList;
    private JButton addTagButton;
    private JButton saveButton;
    private JButton selectFileButton;
    private File photoFile;
    private FormListener formListener;

    public PhotoDetailsDialog(Frame owner) {
        super(owner, "Photo Details", true);

        photoLabel = new JLabel("No file selected");
        selectFileButton = new JButton("Select File");
        titleField = new JTextField(20);
        locationField = new JTextField(10);
        dateField = new JTextField(10);
        tagField = new JTextField(10);
        tagListModel = new DefaultListModel<>();
        tagList = new JList<>(tagListModel);
        addTagButton = new JButton("Add Tag");
        saveButton = new JButton("Save Photo");

        selectFileButton.setMnemonic(KeyEvent.VK_S);
        saveButton.setMnemonic(KeyEvent.VK_O);

        selectFileButton.addActionListener(e -> selectFile());

        addTagButton.addActionListener(e -> addTag());

        saveButton.addActionListener(e -> savePhotoDetails());

        layoutComponents();

        setSize(400, 400);
        setLocationRelativeTo(owner);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            photoFile = fileChooser.getSelectedFile();
            photoLabel.setText(photoFile.getName());
        }
    }

    private void addTag() {
        String tag = tagField.getText().trim();
        if (!tag.isEmpty()) {
            tagListModel.addElement(tag);
            tagField.setText("");
        }
    }

    private void savePhotoDetails() {
        if (photoFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = titleField.getText();
        String location = locationField.getText();
        String date = dateField.getText();
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < tagListModel.size(); i++) {
            tags.add(tagListModel.getElementAt(i));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(date); // Validate date format
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Photo photo = new Photo(photoFile.getAbsolutePath(), title, tags, parsedDate, location);

        if (formListener != null) {
            formListener.formEventOccurred(new FormEvent(this, photo));
        }
        this.dispose();
    }

    public void setFormListener(FormListener formListener) {
        this.formListener = formListener;
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 0.1;

        // First row
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Photo:"), gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 0, 0, 0);
        add(photoLabel, gc);

        gc.gridy++;
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(selectFileButton, gc);

        // Title row
        gc.gridy++;
        gc.gridx = 0;
        gc.insets = new Insets(0, 0, 0, 5);
        gc.anchor = GridBagConstraints.LINE_START;
        add(new JLabel("Title:"), gc);

        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 0, 0, 0);
        add(titleField, gc);

        // Second row
        gc.gridy++;
        gc.gridx = 0;
        gc.insets = new Insets(0, 0, 0, 5);
        gc.anchor = GridBagConstraints.LINE_START;
        add(new JLabel("Location:"), gc);

        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 0, 0, 0);
        add(locationField, gc);

        // Third row
        gc.gridy++;
        gc.gridx = 0;
        gc.insets = new Insets(0, 0, 0, 5);
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Date(yyyy-MM-dd):"), gc);

        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 0, 0, 0);
        add(dateField, gc);

        // Fourth row
        gc.gridy++;
        gc.gridx = 0;
        gc.insets = new Insets(0, 0, 0, 5);
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Tags:"), gc);

        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 0, 0, 0);
        add(tagField, gc);

        // Add tag button
        gc.gridy++;
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(addTagButton, gc);

        // Tag list
        gc.gridy++;
        gc.gridx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        add(new JScrollPane(tagList), gc);

        // Save button
        gc.gridy++;
        gc.gridx = 1;
        gc.weighty = 0.1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gc);
    }

    public interface FormListener {
        void formEventOccurred(FormEvent e);
    }

    public static class FormEvent extends EventObject {
        private Photo photo;

        public FormEvent(Object source, Photo photo) {
            super(source);
            this.photo = photo;
        }

        public Photo getPhoto() {
            return photo;
        }
    }
}