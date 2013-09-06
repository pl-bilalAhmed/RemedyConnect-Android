package com.newpush.mypractice.downloader;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownloadTaskStatusSummary {
    CopyOnWriteArrayList<DownloadTaskStatus> statuses;

    public DownloadTaskStatusSummary() {
        statuses = new CopyOnWriteArrayList<DownloadTaskStatus>();
    }

    public Integer reservePlace() {
        statuses.add(new DownloadTaskStatus(DownloadStatusCodes.DOWNLOAD_FAILED));
        return statuses.size()-1;
    }

    public void updateStatus(Integer index, DownloadTaskStatus status) {
        statuses.set(index, status);
    }

    public boolean isCompleted() {
        int EXPECTED_NUMBER_OF_EXTRACTED_FILES = 1;
        int finished = 0;
        int extracted = 0;
        for (DownloadTaskStatus status : statuses) {
            switch (status.getStatusCode()) {
                case DownloadStatusCodes.DOWNLOAD_FINISHED:
                    ++finished;
                    break;
                case DownloadStatusCodes.EXTRACTING_FINISHED:
                    ++extracted;
                    break;
            }
        }
        return finished == statuses.size()-EXPECTED_NUMBER_OF_EXTRACTED_FILES
                && extracted == EXPECTED_NUMBER_OF_EXTRACTED_FILES;
    }

    public Integer currentProgressPercentage() {
        Integer sumOfExpectedSize = 0;
        Integer sumOfDownloadedSize = 0;

        for (DownloadTaskStatus status : statuses) {
            if (status.getStatusCode() == DownloadStatusCodes.EXTRACTING_FAILED ||
                    status.getStatusCode() == DownloadStatusCodes.DOWNLOAD_FAILED) {
            }
            else {
                sumOfDownloadedSize += status.getDownloadedSize();
                sumOfExpectedSize += status.getExpectedSize();
            }
        }

        Double percentage = 100 * sumOfDownloadedSize / (double)sumOfExpectedSize;
        return percentage.intValue();
    }
}


