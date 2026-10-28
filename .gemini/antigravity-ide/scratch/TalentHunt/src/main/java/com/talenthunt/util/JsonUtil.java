package com.talenthunt.util;

import com.talenthunt.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JsonUtil - JSON Utility for Talent Hunt System
 * 
 * Implements Phase 3 Curriculum Requirement:
 * "Represent user data format using JSON (simple structure)" - CLO 2
 *
 * Provides bidirectional conversion between Java User entities and standard JSON strings
 * without requiring bulky external dependencies.
 */
public class JsonUtil {

    /**
     * Converts a User object into a simple JSON string.
     * Excludes sensitive fields (e.g. passwords) for safety.
     *
     * @param user The User entity to serialize
     * @return Formatted JSON string
     */
    public static String userToJson(User user) {
        if (user == null) {
            return "null";
        }
        return "{"
                + "\"id\":" + user.getId() + ","
                + "\"name\":\"" + escape(user.getName()) + "\","
                + "\"email\":\"" + escape(user.getEmail()) + "\","
                + "\"role\":\"" + escape(user.getRole()) + "\","
                + "\"avatarUrl\":\"" + escape(user.getAvatarUrl() != null ? user.getAvatarUrl() : "") + "\","
                + "\"bio\":\"" + escape(user.getBio() != null ? user.getBio() : "") + "\""
                + "}";
    }

    /**
     * Converts a List of User objects into a JSON Array.
     *
     * @param users The list of User entities
     * @return JSON Array string
     */
    public static String usersToJson(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            sb.append(userToJson(users.get(i)));
            if (i < users.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Parses a simple JSON object string into a User entity.
     * Supports basic key-value pairs in JSON format: {"id":1,"name":"Alex",...}
     *
     * @param json The input JSON string
     * @return Hydrated User instance
     */
    public static User jsonToUser(String json) {
        if (json == null || json.trim().isEmpty() || json.trim().equals("null")) {
            return null;
        }

        Map<String, String> map = parseSimpleJson(json);
        int id = 0;
        try {
            if (map.containsKey("id")) {
                id = Integer.parseInt(map.get("id"));
            }
        } catch (NumberFormatException ignored) {}

        String name = map.getOrDefault("name", "");
        String email = map.getOrDefault("email", "");
        String role = map.getOrDefault("role", "CONTESTANT");
        String avatarUrl = map.getOrDefault("avatarUrl", "");
        String bio = map.getOrDefault("bio", "");
        String password = map.getOrDefault("password", "");

        return new User(id, name, email, password, role, avatarUrl, bio);
    }

    /**
     * Lightweight regex-based parser for simple, flat JSON objects.
     */
    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> result = new HashMap<>();
        if (json == null) return result;

        // Pattern matches "key": "value" or "key": 123 or "key": true
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([\\w.-]+))");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String key = matcher.group(1);
            String strVal = matcher.group(2);
            String primitiveVal = matcher.group(3);
            String value = (strVal != null) ? unescape(strVal) : (primitiveVal != null ? primitiveVal : "");
            result.put(key, value);
        }

        return result;
    }

    /**
     * Pretty-prints a raw JSON string with 2-space indentation.
     */
    public static String prettyPrint(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) return "";
        StringBuilder pretty = new StringBuilder();
        int indentLevel = 0;
        boolean inQuotes = false;

        for (int i = 0; i < rawJson.length(); i++) {
            char c = rawJson.charAt(i);
            if (c == '\"' && (i == 0 || rawJson.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                pretty.append(c);
            } else if (!inQuotes) {
                if (c == '{' || c == '[') {
                    pretty.append(c).append("\n");
                    indentLevel++;
                    appendIndent(pretty, indentLevel);
                } else if (c == '}' || c == ']') {
                    pretty.append("\n");
                    indentLevel--;
                    appendIndent(pretty, indentLevel);
                    pretty.append(c);
                } else if (c == ',') {
                    pretty.append(c).append("\n");
                    appendIndent(pretty, indentLevel);
                } else if (c == ':') {
                    pretty.append(": ");
                } else if (!Character.isWhitespace(c)) {
                    pretty.append(c);
                }
            } else {
                pretty.append(c);
            }
        }
        return pretty.toString();
    }

    private static void appendIndent(StringBuilder sb, int count) {
        for (int i = 0; i < count; i++) {
            sb.append("  ");
        }
    }

    /**
     * Escapes characters for safe JSON output.
     */
    public static String escape(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Unescapes escaped characters from JSON values.
     */
    public static String unescape(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
}
