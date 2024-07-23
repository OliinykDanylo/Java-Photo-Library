package GUI;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateInputDialog extends JDialog {
    private JTextField dateField;
    private JButton okButton;
    private JButton cancelButton;
    private Date date;

    public DateInputDialog(Frame owner) {
        super(owner, "Enter Date", true);

        dateField = new JTextField(20);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;
        gc.gridx = 0;
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Date (yyyy-MM-dd):"), gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(dateField, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridwidth = 2;
        add(okButton, gc);

        gc.gridy++;
        add(cancelButton, gc);

        okButton.addActionListener(e -> {
            try {
                String dateString = dateField.getText();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.parse(dateString);
                setVisible(false);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(DateInputDialog.this, "Invalid date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            date = null;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(owner);
    }

    public Date getDate() {
        return date;
    }
}
