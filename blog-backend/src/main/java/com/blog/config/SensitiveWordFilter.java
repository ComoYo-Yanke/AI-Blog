package com.blog.config;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SensitiveWordFilter {

    private static final Set<String> SENSITIVE_WORDS = Set.of(
            "fuck", "shit", "damn", "asshole", "bitch",
            "操你", "傻逼", "sb", "tmd", "妈的", "滚", "去死",
            "垃圾网站", "骗子", "nmsl",
            "fck", "fuk", "b1tch", "sh1t"
    );

    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = text;
        for (String word : SENSITIVE_WORDS) {
            if (result.toLowerCase().contains(word.toLowerCase())) {
                String replacement = "*".repeat(word.length());
                result = result.replaceAll("(?i)" + Pattern.quote(word), replacement);
            }
        }
        return result;
    }

    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String lower = text.toLowerCase();
        return SENSITIVE_WORDS.stream().anyMatch(w -> lower.contains(w.toLowerCase()));
    }
}
