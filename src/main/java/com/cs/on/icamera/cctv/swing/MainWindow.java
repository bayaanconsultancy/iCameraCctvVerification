package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.error.VerificationException;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainWindow {
    private final JFrame frame;

    public MainWindow() {
        frame = new JFrame("iCAMERA CCTV Verification");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Consistent padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow buttons to stretch horizontally

        // Title Label
        JLabel titleLabel = new JLabel("iCAMERA CCTV Verification Tool");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 153)); // Dark blue text
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        frame.add(titleLabel, gbc);

        // Discover CCTV Button
        JButton discoverButton = createStyledButton("Discover CCTV");
        discoverButton.setToolTipText("Click to discover available CCTV devices on the network.");
        discoverButton.addActionListener(e -> {
            frame.dispose();
            new DiscoveryWindow();
        });
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(discoverButton, gbc);

        // Verify CCTV Button
        JButton verifyButton = createStyledButton("Verify CCTV using Excel");
        verifyButton.setToolTipText("Click to verify available CCTV devices using Excel file.");
        verifyButton.addActionListener(e -> uploadExcelTemplate());
        gbc.gridy = 2;
        frame.add(verifyButton, gbc);

        // Hint Label
        JLabel hintLabel = new JLabel("Please select an option to begin.");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        hintLabel.setForeground(new Color(90, 90, 90)); // Subtle gray text
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        frame.add(hintLabel, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    private void uploadExcelTemplate() {
        try {
            File file = FileDialogUtils.openExcelFileDialog(frame);
            if (file != null) {
                List<Cctv> cctvs = Workbook.readCctvsFromExcel(file, SheetNames.TEMPLATE, TemplateColumns.values());
                if (cctvs.isEmpty()) {
                    throw new VerificationException("Excel file is empty.");
                }
                DataStore.setCctvsToVerify(cctvs);
                frame.dispose();
                new VerifyCctvWindow();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading template Excel file: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 76)); // Green background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 51), 2), // Dark green border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding inside button
        ));
        return button;
    }
}