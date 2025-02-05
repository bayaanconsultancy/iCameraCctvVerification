package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.error.VerificationException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.xssf.SheetNames;
import com.tcs.ion.icamera.cctv.xssf.TemplateColumns;
import com.tcs.ion.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class MainWindow extends SwingWindow {

    public MainWindow() {
        super("iCAMERA CCTV Verification Tool");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().show());
    }

    @Override
    protected void buildUiAndFunctionality() {
        JLabel titleLabel = createLabel("iCAMERA CCTV Verification Tool");

        JButton discoverButton = createOkButton("Discover CCTV", "Click to discover available CCTV devices on the network.");
        discoverButton.addActionListener(e -> next(new DiscoveryWindow()::show));

        JButton verifyButton = createOkButton("Verify CCTV using Excel", "Click to verify available CCTV devices using Excel file.");
        verifyButton.addActionListener(e -> uploadExcelTemplate());

        add(titleLabel);
        add(discoverButton);
        add(verifyButton);

    }

    private void uploadExcelTemplate() {
        try {
            File file = openExcelFile();
            if (file != null) {
                List<Cctv> cctvs = Workbook.readCctvsFromExcel(file, SheetNames.TEMPLATE, TemplateColumns.values());
                if (cctvs.isEmpty()) {
                    throw new VerificationException("Excel file is empty.");
                }
                DataStore.setExcelCctvs(cctvs);
                next(new VerifyCctvWindow());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading template Excel file: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}