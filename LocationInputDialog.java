package GUI;

import javax.swing.*;
import java.awt.*;

public class LocationInputDialog extends JDialog {
    private JTextField locationField;
    private JButton okButton;
    private JButton cancelButton;
    private String location;

    public LocationInputDialog(Frame owner) {
        super(owner, "Enter Location", true);

        locationField = new JTextField(20);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;
        gc.gridx = 0;
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Location:"), gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(locationField, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridwidth = 2;
        add(okButton, gc);

        gc.gridy++;
        add(cancelButton, gc);

        okButton.addActionListener(e -> {
            location = locationField.getText();
            setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            location = null;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(owner);
    }

    public String getDialogLocation() {
        return location;
    }
}
