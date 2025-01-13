package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class EnquireCctvWindow {
	private final JFrame frame;
	private final JProgressBar progressBar;
	private final JButton downloadButton;
	private Timer timer;

	public EnquireCctvWindow() {
		frame = new JFrame("Task Progress");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(300, 150);
		frame.setLayout(new BorderLayout());

		JLabel waitLabel = new JLabel("Please wait until the task is completed...");
		waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(waitLabel, BorderLayout.NORTH);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		frame.add(progressBar, BorderLayout.CENTER);

		downloadButton = new JButton("Download Excel");
		downloadButton.setEnabled(false);
		downloadButton.addActionListener(e -> downloadExcel());
		frame.add(downloadButton, BorderLayout.SOUTH);

		startTask();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		OnvifEnquiry.enquire();
	}

	private void startTask() {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			private int progress = 0;

			@Override
			public void run() {
				if (progress < 100) {
					progress = OnvifEnquiry.progress();
					progressBar.setValue(progress);
				} else {
					timer.cancel();
					downloadButton.setEnabled(true);
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 5000);
	}

	private void downloadExcel() {
		// Logic to download excel
		JOptionPane.showMessageDialog(frame, "Excel downloaded successfully.");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(EnquireCctvWindow::new);
	}
}