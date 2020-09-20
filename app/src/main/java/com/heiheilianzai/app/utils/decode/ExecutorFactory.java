package com.heiheilianzai.app.utils.decode;

public class ExecutorFactory {
    private static JobExecutor INSTANCE = null;

    public static JobExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JobExecutor();
        }
        return INSTANCE;
    }
}