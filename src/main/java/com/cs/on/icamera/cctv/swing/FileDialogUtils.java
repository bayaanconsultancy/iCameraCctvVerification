package com.cs.on.icamera.cctv.swing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDialogUtils {
	private FileDialogUtils() {
	}

	public static File openExcelFileDialog() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xls", "xlsx");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Select Excel File");
		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION)
			return fileChooser.getSelectedFile();
		else
			return null;
	}

	public static File saveFileDialog(String suggestedFileName) {
		JFileChooser fileChooser = new JFileChooser();
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String fileName = date + "-" + suggestedFileName + ".xlsx";
		fileChooser.setSelectedFile(new File(fileName));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Save Excel File");
		int result = fileChooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile.exists()) {
				int overwrite = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite it?",
						"File Exists", JOptionPane.YES_NO_OPTION);
				if (overwrite != JOptionPane.YES_OPTION)
					return null;
			}
			return selectedFile;
		}
		return null;
	}
}