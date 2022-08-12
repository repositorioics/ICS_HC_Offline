package com.ics.ics_hc_offline.listeners;

public interface UploadListener {
    void uploadComplete(String result);
    void progressUpdate(String message, int progress, int max);
}
