package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.onvif.OnvifEnquiry;
import com.tcs.ion.icamera.cctv.util.Credential;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UsernamePasswordWindow extends SwingWindow {

    private final ArrayList<JTextField> usernameFields = new ArrayList<>();
    private final ArrayList<JPasswordField> passwordFields = new ArrayList<>();
    private JPanel inputPanel;
    private int failedCctvCount = 0;

    public UsernamePasswordWindow() {
        super("Provide CCTV Credentials");
    }

    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Add More button at the bottom
        JButton addMoreButton = createOkButton("Add More Credentials", "Click to add more ONVIF credentials.");
        addMoreButton.addActionListener(e -> addInputRow());
        buttonsPanel.add(addMoreButton);

        // Next button to run task
        JButton nextButton = createOkButton("Verify", "Click to get details of remaining unauthorized CCTVs using the provided credentials.");
        nextButton.addActionListener(e -> runTask());
        buttonsPanel.add(nextButton);

        // Button to skip verification
        JButton skipButton = createSkipButton("Skip", "Click to skip further verification and download template.");
        skipButton.addActionListener(e -> next(new DownloadTemplateWindow()));
        buttonsPanel.add(skipButton);

        return buttonsPanel;
    }

    // Method to add a new row of username and password fields
    private void addInputRow() {
        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setPreferredSize(new Dimension(560, 30));
        JLabel usernameLabel = createLabel("Username:");
        JTextField usernameField = createTextField(15);
        JLabel passwordLabel = createLabel("Password:");
        JPasswordField passwordField = createPasswordField(15);
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
        JDialog loadingDialog = getLoadingDialog("Verifying remaining CCTVs..");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                OnvifEnquiry.enquire(getAllCredentials());
                failedCctvCount = DataStore.getUnauthorizedCctvCount();
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                if (failedCctvCount > 1) {
                    showWarningMessage("Failed to get details of " + failedCctvCount + " CCTV(s). Tou may try again with a different set of credentials.");
                    next(new UsernamePasswordWindow());
                } else {
                    next(new DownloadTemplateWindow());
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    @Override
    protected void buildUiAndFunctionality() {
        // Label to display the count of unauthorized CCTVs
        String countMessage = String.format("Unauthorized CCTV count is %d out of %d identified.", DataStore.getUnauthorizedCctvCount(), DataStore.getIdentifiedCctvCount());
        JLabel cctvCountLabel = createLabel(countMessage);
        cctvCountLabel.setForeground(Color.RED);

        // Warning message label
        JLabel warningLabel = createLabel("Provide ONVIF login credentials that are likely to work with the largest number of CCTV first.");
        warningLabel.setForeground(Color.BLUE);

        // Panel to hold the labels
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(cctvCountLabel);
        topPanel.add(warningLabel);

        add(topPanel);

        // Panel for input fields
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        addInputRow(); // Start with one username and password pair
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane);

        JPanel buttonsPanel = getButtonsPanel();
        add(buttonsPanel);
    }
}
