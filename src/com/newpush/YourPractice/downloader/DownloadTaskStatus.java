package com.newpush.YourPractice.downloader;

import java.util.HashMap;

// A class to store and pass the status of a download task around.
public class DownloadTaskStatus {
    Integer statusCode;
    Integer downloadedSize;
    Integer expectedSize;
    HashMap<String, String> newDownload;

    public DownloadTaskStatus(Integer statusCode, Integer downloadedSize, Integer expectedSize) {
        this.statusCode = statusCode;
        this.downloadedSize = downloadedSize;
        this.expectedSize = expectedSize;
    }

    public DownloadTaskStatus(Integer statusCode) {
        this.statusCode = statusCode;
        this.downloadedSize = 0;
        this.expectedSize = 0;
    }

    public DownloadTaskStatus(Integer statusCode, HashMap<String, String> newDownload) {
        this.statusCode = statusCode;
        this.downloadedSize = 0;
        this.expectedSize = 0;
        this.newDownload = newDownload;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Integer getDownloadedSize() {
        return downloadedSize;
    }

    public Integer getExpectedSize() {
        return expectedSize;
    }

    public HashMap<String, String> getNewDownload() {
        return newDownload;
    }
}
