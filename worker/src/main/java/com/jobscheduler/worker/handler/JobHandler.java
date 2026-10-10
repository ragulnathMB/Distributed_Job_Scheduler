package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;

import java.util.Map;

/** Implement and annotate with @Component to add a new job type. {@link #type()} is the job's handlerType. */
public interface JobHandler {

    String type();

    /** Return output text on success; throw to signal failure (the job will be retried with backoff). */
    String execute(Map<String, Object> payload, DispatchRequest request) throws Exception;

    static String str(Map<String, Object> m, String key, String def) {
        Object v = m.get(key);
        return v == null ? def : String.valueOf(v);
    }

    static double num(Map<String, Object> m, String key, double def) {
        Object v = m.get(key);
        if (v instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return v == null ? def : Double.parseDouble(String.valueOf(v));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
