package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TagsInputDialog extends JDialog {
    private JTextField tagsField;
    private JRadioButton andRadioButton;
    private JRadioButton orRadioButton;
    private JButton okButton;
    private JButton cancelButton;
    private List<String> tags;
    private boolean useAndLogic;

    public TagsInputDialog(Frame owner) {
        super(owner, "Enter Tags", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;
        gc.gridx = 0;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Tags (comma separated):"), gc);

        tagsField = new JTextField(20);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(tagsField, gc);

        ButtonGroup group = new ButtonGroup();
        andRadioButton = new JRadioButton("AND", true);
        orRadioButton = new JRadioButton("OR");
        group.add(andRadioButton);
        group.add(orRadioButton);

        gc.gridy++;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        add(andRadioButton, gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(orRadioButton, gc);

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        gc.gridy++;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        add(okButton, gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(cancelButton, gc);

        okButton.addActionListener(e -> {
            tags = Arrays.asList(tagsField.getText().split("\\s*,\\s*"));
            useAndLogic = andRadioButton.isSelected();
            setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            tags = null;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(owner);
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isUseAndLogic() {
        return useAndLogic;
    }
}
