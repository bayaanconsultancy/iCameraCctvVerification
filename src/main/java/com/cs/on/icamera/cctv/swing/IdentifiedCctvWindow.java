package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class IdentifiedCctvWindow {

    private final JFrame frame;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public IdentifiedCctvWindow() {
        frame = new JFrame("Verify Identified CCTVs");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Consistent padding for elements
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("CCTV Verification Utility");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 153));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        // Discovered Count Label and Value
        JLabel discoveredCountLabel = new JLabel("Discovered CCTVs: ");
        discoveredCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        frame.add(discoveredCountLabel, gbc);

        int discoveredCount = DataStore.getIdentifiedCctvCount();
        JLabel discoveredCountValue = new JLabel(String.valueOf(discoveredCount));
        discoveredCountValue.setFont(new Font("Arial", Font.BOLD, 16));
        discoveredCountValue.setForeground(discoveredCount > 0 ? new Color(0, 153, 76) : Color.RED);
        gbc.gridx = 1;
        frame.add(discoveredCountValue, gbc);

        // Username Label and Text Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setText("admin");
        gbc.gridx = 1;
        frame.add(usernameField, gbc);

        // Password Label and Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        String hintText;
        gbc.gridy = 4;

        if (discoveredCount > 0) {
            hintText = "Enter credentials that are valid for the majority of CCTVs.";
            discoveredCountLabel.setForeground(new Color(0, 102, 153));

            JButton getDetailsButton = getGetDetailsButton(discoveredCount);
            gbc.gridx = 0;
            gbc.gridwidth = 2; // Span across two columns
            frame.add(getDetailsButton, gbc);
        } else {
            hintText = "Download the template and manually fill CCTV details to verify.";
            discoveredCountLabel.setForeground(Color.RED);
            usernameField.setEnabled(false);
            passwordField.setEnabled(false);

            JButton downloadTemplateButton = createStyledButton("Download Template Excel");
            downloadTemplateButton.addActionListener(e -> downloadExcelTemplate());
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            frame.add(downloadTemplateButton, gbc);
        }

        // Hint Label
        JLabel hintLabel = new JLabel(hintText);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(70, 70, 70)); // Subtle gray
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.gridx = 0;
        frame.add(hintLabel, gbc);

        SwingUtilities.invokeLater(passwordField::requestFocusInWindow);
        frame.setVisible(true);
    }

    private void downloadExcelTemplate() {
        try {
            File file = FileDialogUtils.saveFileDialog("iCamera-cctv-template", frame);
            if (file != null) {
                Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(), DataStore.getIdentifiedCctvs());
                JOptionPane.showMessageDialog(frame, "Excel template created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error creating excel template: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton getGetDetailsButton(int discoveredCount) {
        JButton getDetailsButton = createStyledButton("Get Details of " + discoveredCount + " CCTVs");
        getDetailsButton.addActionListener(e -> {
            frame.setEnabled(false);

            JLabel waitLabel = new JLabel("Please wait...");
            waitLabel.setFont(new Font("Arial", Font.BOLD, 14));
            waitLabel.setHorizontalAlignment(SwingConstants.CENTER);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 6; // Positioned below the button
            gbc.gridwidth = 2;
            frame.add(waitLabel, gbc);

            frame.revalidate();
            frame.repaint();

            new Thread(() -> {
                DataStore.setOnvifCredential(usernameField.getText(), String.valueOf(passwordField.getPassword()));
                OnvifEnquiry.enquire();
                frame.dispose();

                if (DataStore.getUnauthorizedCctvCount() > 0) {
                    SwingUtilities.invokeLater(UsernamePasswordWindow::new);
                } else {
                    SwingUtilities.invokeLater(DownloadTemplateWindow::new);
                }
            }).start();
        });
        return getDetailsButton;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 76)); // Green color
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 51), 2), // Darker green border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding inside
        ));
        return button;
    }
}