package com.ics.ics_hc_offline.listeners;

public interface DownloadListener {
    void downloadComplete(String result);
    void progressUpdate(String message, int progress, int max);
}
