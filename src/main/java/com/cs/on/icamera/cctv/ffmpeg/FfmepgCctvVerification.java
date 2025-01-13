package com.cs.on.icamera.cctv.ffmpeg;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;

import java.util.ArrayList;
import java.util.List;

public class FfmepgCctvVerification {
	private static final List<Cctv> cctvsToVerify = new ArrayList<>();

	private FfmepgCctvVerification() {
	}

	public static int progress() {
		// TODO: Implement verification progress, 0 to 100
		return 100;
	}

	public static boolean isComplete() {
		// TODO: Implement if verification is complete
		return true;
	}

	public static void verify() {
		cctvsToVerify.addAll(DataStore.getCctvsToVerify());
		// TODO: Implement verification logic
	}
}
