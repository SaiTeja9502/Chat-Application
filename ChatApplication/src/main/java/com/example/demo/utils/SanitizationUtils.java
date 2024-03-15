package com.example.demo.utils;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SanitizationUtils {

    private static final PolicyFactory HTML_POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    public static String sanitizeHtml(String html) {
        return HTML_POLICY.sanitize(html);
    }
}
