package com.cs.on.icamera.cctv.swing;

import javax.swing.*;
import java.awt.*;

public abstract class SwingWindow {

    // Professional color palette
    protected static final Color BACKGROUND_COLOR = new Color(224, 224, 224);
    protected static final Color FOREGROUND_COLOR = new Color(32, 32, 64);
    protected static final Color ACCENT_COLOR = new Color(0, 128, 255);
    protected static final Color BORDER_COLOR = new Color(128, 128, 192);
    private final JFrame frame;
    private int gridy = 0;

    protected SwingWindow(String title) {
        frame = new JFrame(title);
        frame.setSize(640, 480);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    protected static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FOREGROUND_COLOR);
        return label;
    }

    protected static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setBackground(Color.WHITE);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return textField;
    }

    protected static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(FOREGROUND_COLOR);
        textArea.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    protected static JButton createOkButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(FOREGROUND_COLOR);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(128, 192, 128), 1), // Border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
        return button;
    }
    protected static JButton createSkipButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(FOREGROUND_COLOR);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(192, 128, 128), 1), // Border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
        return button;
    }

    protected void addComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        frame.add(component, gbc);
    }

    protected void addComponent(Component... components) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = gridy++;
        int gridx = 0;
        for (Component component : components) {
            gbc.gridx = gridx++;
            frame.add(component, gbc);
        }
    }

    // Abstract method that subclasses must implement
    protected abstract void buildUI(JFrame frame);

    public void show() {
        SwingUtilities.invokeLater(() -> {
            this.buildUI(frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public void next(Runnable action) {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            action.run();
        });
    }

    public static void main(String[] args) {
        // Example usage with an anonymous inner class:
        new SwingWindow("Example Form") {
            @Override
            protected void buildUI(JFrame frame) {
                JLabel nameLabel = createLabel("Name:");
                JTextField nameField = createTextField(20);

                JLabel addressLabel = createLabel("Address:");
                JTextArea addressArea = createTextArea(3, 20);

                JButton submitButton = createOkButton("Submit");
                JButton skipButton = createSkipButton("Skip");
                JButton skipButton1 = createSkipButton("Skip");

                skipButton.addActionListener(e -> next(() -> JOptionPane.showMessageDialog(null, "Disposing and executing runnable", "Success", JOptionPane.INFORMATION_MESSAGE)));

                addComponent(skipButton1);
                addComponent(nameLabel, nameField);
                addComponent(addressLabel, addressArea);
                JLabel nameLabel1 = createLabel("Name: jdhfjcdf nvkf dkjfdk dkl dkfjdkf jdf jdkl");
                addComponent(nameLabel1);
                addComponent(skipButton, submitButton);


//                addComponent(nameLabel, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5));
//                addComponent(nameField, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 10));
//
//                addComponent(addressLabel, 0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 5));
//                addComponent(addressArea, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 10));
//
//                addComponent(submitButton, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10));
//                addComponent(skipButton, 1, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10));
            }
        }.show();
    }
}