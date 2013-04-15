package com.newpush.greenwoodpediatrics;

import java.io.File;

import android.content.Context;
import com.newpush.greenwoodpediatrics.Data;

public class DataChecker {
	public static boolean isDataAvailable(Context applicationContext) {
		// TODO Make this faster by checking a special file created on download.
		int i = 0;
		boolean data_available = true;
		String[] datafiles = Data.dataFiles.keySet().toArray(new String[0]);
		while (i < Data.dataFiles.size() && data_available) {
			File file = applicationContext.getFileStreamPath(datafiles[i]);
			if (!file.exists()) {
				data_available = false;
			}
			++i;
		}
		return data_available;
	}
}
