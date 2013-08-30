package com.newpush.mypractice.downloader;

public class DownloadStatus {
    public static final int UPDATE_PROGRESS = 1;
    public static final int DOWNLOAD_FAILED = 2;
    public static final int NETWORK_AVAILABLE = 3;
    public static final int SWITCH_TO_DETERMINATE = 4;
    public static final int DOWNLOAD_FINISHED = 5;
    public static final int SWITCH_TO_NON_DETERMINATE = 6;
    public static final int EXTRACTING = 7;
    public static final int EXTRACTING_FAILED = 8;
    public static final int EXTRACTING_FINISHED = 9;
    public static final int NEW_DOWNLOAD = 10;
}
