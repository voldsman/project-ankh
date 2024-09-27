package io.vold.ankh;

import java.lang.management.ManagementFactory;

public class DevLogger {

    private DevLogger() {
    }

    public static RequestLogContext startRequest(String method, String path) {
        long startTime = System.currentTimeMillis();
        var threadBean = ManagementFactory.getThreadMXBean();
        return new RequestLogContext(method, path, startTime, threadBean.getThreadCount(), threadBean.getPeakThreadCount());
    }

    public static void endRequest(RequestLogContext context) {
        long duration = System.currentTimeMillis() - context.startTime;
        System.out.printf("[DEV LOG] %s %s - %dms, Threads: %d, Peak Threads: %d%n",
                context.method, context.path, duration, context.threadCount, context.peakThreadCount);
    }

    public static class RequestLogContext {
        final String method;
        final String path;
        final long startTime;
        final int threadCount;
        final int peakThreadCount;

        RequestLogContext(String method, String path, long startTime, int threadCount, int peakThreadCount) {
            this.method = method;
            this.path = path;
            this.startTime = startTime;
            this.threadCount = threadCount;
            this.peakThreadCount = peakThreadCount;
        }
    }
}