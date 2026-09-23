package edu.neu.csye6200.lab2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.neu.csye6200.lab2.model.UserInfo;

/**
 * Lab 2 - Introduction to Java Swing.
 *
 * Use case 1: create a user profile (first name, last name, gender, age, phone,
 *             email and an optional photograph) with validation on every input.
 * Use case 2: display the profile - along with the photo - in a message dialog.
 *
 * Event driven architecture is demonstrated with several different listener
 * types: ActionListener (buttons / Enter key), FocusListener (validate a field
 * as soon as the user leaves it), DocumentListener (clear the error while the
 * user is typing), KeyListener (block non numeric keys in age and phone),
 * ItemListener (gender radio buttons), MouseListener (click the photo box) and
 * WindowListener (confirm before closing).
 */
public class MainJFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---- validation rules -------------------------------------------------
    // A name starts with a letter and may contain letters, spaces, . ' and -
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z .'-]{1,29}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 120;

    // ---- photo size (as per the photoUploadLogic reference) ---------------
    private static final int PHOTO_WIDTH = 60;
    private static final int PHOTO_HEIGHT = 80;

    private static final Color ERROR_COLOR = new Color(0xC0, 0x39, 0x2B);
    private static final Color OK_BORDER = new Color(0xAA, 0xAA, 0xAA);

    // ---- the model --------------------------------------------------------
    private final UserInfo userInfo = new UserInfo();

    // ---- input components -------------------------------------------------
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JRadioButton otherRadioButton;
    private ButtonGroup genderButtonGroup;
    private JTextField ageTextField;
    private JTextField phoneTextField;
    private JTextField emailTextField;

    // ---- error labels shown under each input ------------------------------
    private JLabel firstNameErrorLabel;
    private JLabel lastNameErrorLabel;
    private JLabel genderErrorLabel;
    private JLabel ageErrorLabel;
    private JLabel phoneErrorLabel;
    private JLabel emailErrorLabel;

    // ---- photo components -------------------------------------------------
    private JLabel photoPreviewLabel;
    private JLabel imgLabel;
    private JButton choosePhotoButton;
    private JButton removePhotoButton;

    // ---- action buttons ---------------------------------------------------
    private JButton submitButton;
    private JButton clearButton;

    public MainJFrame() {
        initComponents();
    }

    /**
     * Builds the whole window: header on top, the form in the middle, the photo
     * panel on the right and the action buttons at the bottom.
     */
    private void initComponents() {
        setTitle("Lab 2 - User Profile");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildPhotoPanel(), BorderLayout.EAST);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        // WindowListener - ask for a confirmation instead of closing directly
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                int choice = JOptionPane.showConfirmDialog(MainJFrame.this,
                        "Do you want to close the application?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        getRootPane().setDefaultButton(submitButton);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));

        JLabel title = new JLabel("Create User Profile");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Fields marked with * are required");
        subtitle.setFont(subtitle.getFont().deriveFont(11f));
        subtitle.setForeground(Color.GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        header.add(title, gbc);
        gbc.gridy = 1;
        header.add(subtitle, gbc);
        return header;
    }

    /** The left hand side of the window: one row per input field. */
    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        int row = 0;

        // ---------------- first name ----------------
        firstNameTextField = new JTextField(18);
        firstNameErrorLabel = createErrorLabel();
        row = addRow(form, row, "First Name *", firstNameTextField, firstNameErrorLabel);

        // FocusListener - validate the field the moment the user leaves it
        firstNameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                validateFirstName();
            }
        });
        // DocumentListener - hide the old error while the user is typing
        clearErrorWhileTyping(firstNameTextField, firstNameErrorLabel);

        // ---------------- last name ----------------
        lastNameTextField = new JTextField(18);
        lastNameErrorLabel = createErrorLabel();
        row = addRow(form, row, "Last Name *", lastNameTextField, lastNameErrorLabel);

        lastNameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                validateLastName();
            }
        });
        clearErrorWhileTyping(lastNameTextField, lastNameErrorLabel);

        // ---------------- gender ----------------
        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        otherRadioButton = new JRadioButton("Other");

        // the action command is the value we read back on submit
        maleRadioButton.setActionCommand("Male");
        femaleRadioButton.setActionCommand("Female");
        otherRadioButton.setActionCommand("Other");

        genderButtonGroup = new ButtonGroup();
        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);
        genderButtonGroup.add(otherRadioButton);

        JPanel genderPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gp = new GridBagConstraints();
        gp.gridy = 0;
        gp.insets = new Insets(0, 0, 0, 8);
        gp.gridx = 0;
        genderPanel.add(maleRadioButton, gp);
        gp.gridx = 1;
        genderPanel.add(femaleRadioButton, gp);
        gp.gridx = 2;
        genderPanel.add(otherRadioButton, gp);

        genderErrorLabel = createErrorLabel();
        row = addRow(form, row, "Gender *", genderPanel, genderErrorLabel);

        // ItemListener - a gender was picked, so the error is no longer valid
        maleRadioButton.addItemListener(evt -> genderErrorLabel.setText(" "));
        femaleRadioButton.addItemListener(evt -> genderErrorLabel.setText(" "));
        otherRadioButton.addItemListener(evt -> genderErrorLabel.setText(" "));

        // ---------------- age ----------------
        ageTextField = new JTextField(5);
        ageErrorLabel = createErrorLabel();
        row = addRow(form, row, "Age *", ageTextField, ageErrorLabel);

        // KeyListener - only digits are allowed to reach the field
        ageTextField.addKeyListener(new DigitsOnlyKeyListener(3));
        ageTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                validateAge();
            }
        });
        clearErrorWhileTyping(ageTextField, ageErrorLabel);

        // ---------------- phone ----------------
        phoneTextField = new JTextField(12);
        phoneErrorLabel = createErrorLabel();
        row = addRow(form, row, "Phone Number *", phoneTextField, phoneErrorLabel);

        phoneTextField.addKeyListener(new DigitsOnlyKeyListener(10));
        phoneTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                validatePhone();
            }
        });
        clearErrorWhileTyping(phoneTextField, phoneErrorLabel);

        // ---------------- email ----------------
        emailTextField = new JTextField(22);
        emailErrorLabel = createErrorLabel();
        row = addRow(form, row, "Email *", emailTextField, emailErrorLabel);

        emailTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                validateEmail();
            }
        });
        clearErrorWhileTyping(emailTextField, emailErrorLabel);

        // ActionListener on a text field - pressing Enter submits the form
        emailTextField.addActionListener(evt -> submitButtonActionPerformed());

        // push everything to the top of the panel
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row;
        filler.weighty = 1.0;
        filler.fill = GridBagConstraints.VERTICAL;
        form.add(new JPanel(), filler);

        return form;
    }

    /** The right hand side of the window: photo preview and its buttons. */
    private JPanel buildPhotoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 0, 6, 16),
                BorderFactory.createTitledBorder("Photo (optional)")));

        photoPreviewLabel = new JLabel("No photo", SwingConstants.CENTER);
        photoPreviewLabel.setPreferredSize(new Dimension(PHOTO_WIDTH + 10, PHOTO_HEIGHT + 10));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(OK_BORDER));
        photoPreviewLabel.setFont(photoPreviewLabel.getFont().deriveFont(10f));
        photoPreviewLabel.setForeground(Color.GRAY);
        photoPreviewLabel.setToolTipText("Click to select an image");
        photoPreviewLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // MouseListener - clicking the preview box also opens the file chooser
        photoPreviewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                choosePhotoButtonActionPerformed();
            }
        });

        choosePhotoButton = new JButton("Choose Photo...");
        choosePhotoButton.addActionListener(evt -> choosePhotoButtonActionPerformed());

        removePhotoButton = new JButton("Remove");
        removePhotoButton.setEnabled(false);
        removePhotoButton.addActionListener(evt -> removePhoto());

        imgLabel = new JLabel(" ");
        imgLabel.setFont(imgLabel.getFont().deriveFont(10f));
        imgLabel.setForeground(Color.GRAY);
        imgLabel.setPreferredSize(new Dimension(150, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 10, 6, 10);
        panel.add(photoPreviewLabel, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 4, 10);
        panel.add(choosePhotoButton, gbc);

        gbc.gridy = 2;
        panel.add(removePhotoButton, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(6, 10, 6, 10);
        panel.add(imgLabel, gbc);

        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 16, 14, 16));

        clearButton = new JButton("Clear");
        clearButton.addActionListener(evt -> clearButtonActionPerformed());

        submitButton = new JButton("Submit");
        submitButton.addActionListener(evt -> submitButtonActionPerformed());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 8);
        panel.add(clearButton, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(submitButton, gbc);
        return panel;
    }

    // =====================================================================
    // Event handlers
    // =====================================================================

    /**
     * Use case 1 + 2: validate every field and, when everything is correct,
     * store the values in the model and display the profile in a dialog.
     */
    private void submitButtonActionPerformed() {
        try {
            // 1. validate ALL the inputs (every field reports its own error)
            List<String> errors = new ArrayList<>();

            if (!validateFirstName()) {
                errors.add("First Name: " + firstNameErrorLabel.getText());
            }
            if (!validateLastName()) {
                errors.add("Last Name: " + lastNameErrorLabel.getText());
            }
            if (!validateGender()) {
                errors.add("Gender: " + genderErrorLabel.getText());
            }
            if (!validateAge()) {
                errors.add("Age: " + ageErrorLabel.getText());
            }
            if (!validatePhone()) {
                errors.add("Phone Number: " + phoneErrorLabel.getText());
            }
            if (!validateEmail()) {
                errors.add("Email: " + emailErrorLabel.getText());
            }

            // 2. prompt the user with every problem found
            if (!errors.isEmpty()) {
                StringBuilder message = new StringBuilder("Please fix the following:\n\n");
                for (String error : errors) {
                    message.append("  • ").append(error).append("\n");
                }
                JOptionPane.showMessageDialog(this, message.toString(),
                        "Oops! Invalid input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. all good - copy the form values into the model
            userInfo.setFirstName(firstNameTextField.getText().trim());
            userInfo.setLastName(lastNameTextField.getText().trim());
            userInfo.setGender(genderButtonGroup.getSelection().getActionCommand());
            userInfo.setAge(Integer.parseInt(ageTextField.getText().trim()));
            userInfo.setPhone(phoneTextField.getText().trim());
            userInfo.setEmail(emailTextField.getText().trim());

            // printing to the output window as well
            System.out.println("Profile created -> " + userInfo);

            // 4. display the profile (with the photo) in a message dialog
            showProfileDialog();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Age must be a whole number.",
                    "Oops!", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Oops, something went wrong!",
                    "Failed!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /** Use case 2: show the collected information plus the photo in a dialog. */
    private void showProfileDialog() {
        JPanel content = new JPanel(new BorderLayout(16, 0));

        // the photo on the left (or a placeholder when none was uploaded)
        JLabel picLabel = new JLabel("", SwingConstants.CENTER);
        picLabel.setPreferredSize(new Dimension(PHOTO_WIDTH + 10, PHOTO_HEIGHT + 10));
        picLabel.setBorder(BorderFactory.createLineBorder(OK_BORDER));
        if (userInfo.getPic() != null) {
            picLabel.setIcon(userInfo.getPic());
        } else {
            picLabel.setText("No photo");
            picLabel.setFont(picLabel.getFont().deriveFont(10f));
            picLabel.setForeground(Color.GRAY);
        }
        content.add(picLabel, BorderLayout.WEST);

        // the details on the right, laid out as a small HTML table
        String details = "<html><table cellpadding='2'>"
                + row("Name", userInfo.getFullName())
                + row("Gender", userInfo.getGender())
                + row("Age", String.valueOf(userInfo.getAge()))
                + row("Phone", userInfo.getFormattedPhone())
                + row("Email", userInfo.getEmail())
                + "</table></html>";
        content.add(new JLabel(details), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, content,
                "Success! User Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private String row(String label, String value) {
        return "<tr><td><b>" + label + "</b></td><td>" + escapeHtml(value) + "</td></tr>";
    }

    /**
     * BONUS: let the user pick an image file, scale it down and keep it in the
     * model so it can be shown in the profile dialog.
     */
    private void choosePhotoButtonActionPerformed() {
        JFileChooser file = new JFileChooser();
        file.setDialogTitle("Select a profile photo");
        file.setAcceptAllFileFilterUsed(false);
        file.setFileFilter(new FileNameExtensionFilter(
                "Image files (png, jpg, jpeg, gif, bmp)", "png", "jpg", "jpeg", "gif", "bmp"));

        if (file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(file.getSelectedFile());
                if (img == null) {
                    // the file was not a readable image
                    throw new IllegalArgumentException("Unsupported image file");
                }

                Image edited_image = img.getScaledInstance(PHOTO_WIDTH, PHOTO_HEIGHT, Image.SCALE_SMOOTH);
                if (edited_image != null) {
                    imgLabel.setText(file.getSelectedFile().getName());
                    imgLabel.setToolTipText(file.getSelectedFile().getAbsolutePath());
                    this.userInfo.setPic(new ImageIcon(edited_image));

                    photoPreviewLabel.setText("");
                    photoPreviewLabel.setIcon(this.userInfo.getPic());
                    removePhotoButton.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please upload the image correctly.",
                        "Error - Incorrect image", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void removePhoto() {
        userInfo.setPic(null);
        photoPreviewLabel.setIcon(null);
        photoPreviewLabel.setText("No photo");
        imgLabel.setText(" ");
        imgLabel.setToolTipText(null);
        removePhotoButton.setEnabled(false);
    }

    /** Resets the form back to an empty profile. */
    private void clearButtonActionPerformed() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        genderButtonGroup.clearSelection();
        ageTextField.setText("");
        phoneTextField.setText("");
        emailTextField.setText("");
        removePhoto();

        clearError(firstNameTextField, firstNameErrorLabel);
        clearError(lastNameTextField, lastNameErrorLabel);
        genderErrorLabel.setText(" ");
        clearError(ageTextField, ageErrorLabel);
        clearError(phoneTextField, phoneErrorLabel);
        clearError(emailTextField, emailErrorLabel);

        firstNameTextField.requestFocusInWindow();
    }

    // =====================================================================
    // Validation - one method per input field
    // =====================================================================

    private boolean validateFirstName() {
        return validateName(firstNameTextField, firstNameErrorLabel, "first name");
    }

    private boolean validateLastName() {
        return validateName(lastNameTextField, lastNameErrorLabel, "last name");
    }

    private boolean validateName(JTextField field, JLabel errorLabel, String what) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            return showError(field, errorLabel, "Please enter your " + what + ".");
        }
        if (!NAME_PATTERN.matcher(value).matches()) {
            return showError(field, errorLabel,
                    "Use 2-30 letters only (no digits or special characters).");
        }
        return clearError(field, errorLabel);
    }

    private boolean validateGender() {
        if (genderButtonGroup.getSelection() == null) {
            genderErrorLabel.setText("Please select your gender.");
            return false;
        }
        genderErrorLabel.setText(" ");
        return true;
    }

    private boolean validateAge() {
        String value = ageTextField.getText().trim();
        if (value.isEmpty()) {
            return showError(ageTextField, ageErrorLabel, "Please enter your age.");
        }
        int age;
        try {
            age = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return showError(ageTextField, ageErrorLabel, "Age must be a whole number.");
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            return showError(ageTextField, ageErrorLabel,
                    "Age must be between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
        return clearError(ageTextField, ageErrorLabel);
    }

    private boolean validatePhone() {
        String value = phoneTextField.getText().trim();
        if (value.isEmpty()) {
            return showError(phoneTextField, phoneErrorLabel, "Please enter your phone number.");
        }
        if (!value.matches("\\d{10}")) {
            return showError(phoneTextField, phoneErrorLabel,
                    "Phone number must be exactly 10 digits.");
        }
        return clearError(phoneTextField, phoneErrorLabel);
    }

    private boolean validateEmail() {
        String value = emailTextField.getText().trim();
        if (value.isEmpty()) {
            return showError(emailTextField, emailErrorLabel, "Please enter your email address.");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            return showError(emailTextField, emailErrorLabel,
                    "Enter a valid email, for example name@example.com");
        }
        return clearError(emailTextField, emailErrorLabel);
    }

    // =====================================================================
    // Small helpers
    // =====================================================================

    /** Adds a "label - input - error" row to the form panel. */
    private int addRow(JPanel form, int row, String labelText, JComponent input, JLabel errorLabel) {
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel(labelText);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(4, 0, 0, 10);
        form.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(input, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(1, 0, 4, 10);
        form.add(errorLabel, gbc);

        return row + 2;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(ERROR_COLOR);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 11f));
        return label;
    }

    private boolean showError(JTextField field, JLabel errorLabel, String message) {
        errorLabel.setText(message);
        field.setBorder(BorderFactory.createLineBorder(ERROR_COLOR));
        return false;
    }

    private boolean clearError(JTextField field, JLabel errorLabel) {
        errorLabel.setText(" ");
        field.setBorder(new JTextField().getBorder());
        return true;
    }

    /**
     * DocumentListener: as soon as the user edits a field the previous error is
     * removed, so the red message does not stay on screen while typing.
     */
    private void clearErrorWhileTyping(final JTextField field, final JLabel errorLabel) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evt) {
                clearError(field, errorLabel);
            }

            @Override
            public void removeUpdate(DocumentEvent evt) {
                clearError(field, errorLabel);
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                clearError(field, errorLabel);
            }
        });
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * KeyListener that keeps a text field numeric and limited in length. This is
     * the "prevent the bad input" half of the validation, the validate methods
     * are the "tell the user what is wrong" half.
     */
    private static class DigitsOnlyKeyListener extends KeyAdapter {

        private final int maxLength;

        DigitsOnlyKeyListener(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void keyTyped(KeyEvent evt) {
            char c = evt.getKeyChar();
            JTextField field = (JTextField) evt.getSource();

            if (!Character.isDigit(c)) {
                evt.consume();
                return;
            }
            if (field.getText().length() >= maxLength && field.getSelectedText() == null) {
                evt.consume();
            }
        }
    }

    // =====================================================================
    // main
    // =====================================================================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // keep the default look and feel if the system one is unavailable
        }

        // Swing components must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainJFrame().setVisible(true));
    }
}
