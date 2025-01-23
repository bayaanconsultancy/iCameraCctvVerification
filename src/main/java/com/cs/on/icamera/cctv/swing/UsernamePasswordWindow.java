package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;
import com.cs.on.icamera.cctv.util.Credential;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UsernamePasswordWindow {

    private final JFrame frame;
    private final JPanel inputPanel;
    private final ArrayList<JTextField> usernameFields = new ArrayList<>();
    private final ArrayList<JPasswordField> passwordFields = new ArrayList<>();
    private int failedCctvCount = 0; // Simulate failed CCTVs, modify during task execution

    public UsernamePasswordWindow() {
        frame = new JFrame("Provide CCTV Credentials");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        // Label to display the count of unauthorized CCTVs
        String countMessage = String.format("Unauthorized CCTV count is %d out of %d identified.", DataStore.getUnauthorizedCctvCount(), DataStore.getIdentifiedCctvCount());
        JLabel cctvCountLabel = new JLabel(countMessage);
        cctvCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cctvCountLabel.setForeground(Color.BLUE);

        // Warning message label
        JLabel warningLabel = new JLabel("Provide ONVIF login credentials that are likely to work with the largest number of CCTV first.");
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);

        // Panel to hold the labels
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(cctvCountLabel);
        topPanel.add(warningLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        // Panel for input fields
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        addInputRow(); // Start with one username and password pair
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = getJPanel();

        frame.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel getJPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add More button at the bottom
        JButton addMoreButton = new JButton("Add More Credentials");
        addMoreButton.addActionListener(e -> addInputRow());
        buttonsPanel.add(addMoreButton);


        // Next button to run task
        JButton nextButton = new JButton("Verify");
        nextButton.addActionListener(e -> runTask());
        buttonsPanel.add(nextButton);

        // Button to skip verification
        JButton skipButton = new JButton("Skip and Download Template");
        skipButton.addActionListener(e -> openDownloadTemplateWindow());
        buttonsPanel.add(skipButton);

        return buttonsPanel;
    }

    // Method to add a new row of username and password fields
    private void addInputRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField(15);

        usernameField.setText("admin");

        usernameFields.add(usernameField);
        passwordFields.add(passwordField);

        row.add(usernameLabel);
        row.add(usernameField);
        row.add(passwordLabel);
        row.add(passwordField);

        inputPanel.add(row);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private List<Credential> getAllCredentials() {
        List<Credential> credentials = new ArrayList<>();
        for (int i = 0; i < usernameFields.size(); i++) {
            String username = usernameFields.get(i).getText();
            String password = new String(passwordFields.get(i).getPassword());
            Credential credential = new Credential(username, password);
            credentials.add(credential);
        }
        return credentials;
    }

    // Method to simulate task execution
    private void runTask() {
        // Simulate task with "Please Wait" dialog
        JDialog loadingDialog = new JDialog(frame, "Please Wait", true);
        JLabel loadingLabel = new JLabel("Verifying remaining CCTVs. Please wait...");
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(frame);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                OnvifEnquiry.enquire(getAllCredentials());
                failedCctvCount = DataStore.getUnauthorizedCctvCount();
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                if (failedCctvCount > 1) {
                    JOptionPane.showMessageDialog(frame, "Verification failed for more than one CCTV. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    reopenWindow();
                } else {
                    openDownloadTemplateWindow();
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    // Method to reopen the window
    private void reopenWindow() {
        frame.dispose();
        new UsernamePasswordWindow();
    }

    // Method to open the "Download Template" window
    private void openDownloadTemplateWindow() {
        frame.dispose();
        SwingUtilities.invokeLater(DownloadTemplateWindow::new);
    }
}
