package com.talenthunt.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import com.talenthunt.dao.TalentDao;
import com.talenthunt.db.DatabaseManager;
import com.talenthunt.model.User;
import com.talenthunt.model.Models.*;
import com.talenthunt.service.UserOperations;
import com.talenthunt.service.impl.UserServiceImpl;
import com.talenthunt.exception.ValidationException;
import com.talenthunt.exception.AuthenticationException;
import com.talenthunt.exception.UserAlreadyExistsException;
import com.talenthunt.util.JsonUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Embedded Server for Talent Hunt Platform.
 * Supports RESTful JSON endpoints and static file serving.
 * Complies with Phases 1 - 4 (User validation, Structured service interfaces, Exceptions, and Execution).
 */
public class TalentHuntServer {

    private static final int PORT = 8080;
    private static final TalentDao talentDao = new TalentDao();
    private static final UserOperations userService = new UserServiceImpl(talentDao);

    public static void main(String[] args) throws IOException {
        // Initialize Database
        DatabaseManager.initDatabase();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Core API Endpoints
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/categories", new CategoriesHandler());
        server.createContext("/api/competitions", new CompetitionsHandler());
        server.createContext("/api/submissions", new SubmissionsHandler());
        server.createContext("/api/talents", new TalentsHandler());
        server.createContext("/api/submission", new SingleSubmissionHandler());
        server.createContext("/api/evaluations", new EvaluationsHandler());
        server.createContext("/api/vote", new VoteHandler());
        server.createContext("/api/votes", new VoteHandler());
        server.createContext("/api/stats", new StatsHandler());
        server.createContext("/api/users", new UsersHandler());
        server.createContext("/api/user/json", new UserJsonHandler());

        // Static Web Asset Handler (serves public UI and webapp assets)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        System.out.println("=================================================");
        System.out.println("   TALENT HUNT SYSTEM SERVER IS NOW ONLINE!      ");
        System.out.println("   URL: http://localhost:" + PORT + "                  ");
        System.out.println("   JSP/Tomcat Dir: src/main/webapp               ");
        System.out.println("   Database Engine: " + (DatabaseManager.isH2Fallback() ? "Embedded H2 (MySQL Mode)" : "MySQL Server"));
        System.out.println("=================================================");
    }

    // ─── Phase 1 & 2: User Authentication Handler with Interface & Exceptions ───
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseFormOrJsonBody(body);
                String email = data.getOrDefault("email", "");
                String password = data.getOrDefault("password", "");
                String portal = data.getOrDefault("portal", data.getOrDefault("role", "USER"));

                try {
                    // Authenticate via UserOperations interface with ValidationException / AuthenticationException
                    User user = userService.authenticate(email, password, portal);
                    String json = String.format(
                        "{\"success\":true,\"user\":{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"avatarUrl\":\"%s\",\"bio\":\"%s\"},\"name\":\"%s\",\"role\":\"%s\"}",
                        user.getId(), escapeJson(user.getName()), escapeJson(user.getEmail()), escapeJson(user.getRole()), escapeJson(user.getAvatarUrl()), escapeJson(user.getBio()),
                        escapeJson(user.getName()), escapeJson(user.getRole())
                    );
                    sendJsonResponse(exchange, 200, json);
                } catch (ValidationException | AuthenticationException ex) {
                    sendJsonResponse(exchange, 401, "{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // ─── Phase 1 & 2: User Registration Handler with Interface & Validation ───
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseFormOrJsonBody(body);
                String name = data.getOrDefault("name", "");
                String email = data.getOrDefault("email", "");
                String password = data.getOrDefault("password", "");
                String role = data.getOrDefault("role", "CONTESTANT");

                try {
                    User newUser = new User(name, email, password, role);
                    User registered = userService.register(newUser);
                    String json = String.format(
                        "{\"success\":true,\"user\":{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"avatarUrl\":\"%s\",\"bio\":\"%s\"},\"name\":\"%s\",\"role\":\"%s\"}",
                        registered.getId(), escapeJson(registered.getName()), escapeJson(registered.getEmail()), escapeJson(registered.getRole()), escapeJson(registered.getAvatarUrl()), escapeJson(registered.getBio()),
                        escapeJson(registered.getName()), escapeJson(registered.getRole())
                    );
                    sendJsonResponse(exchange, 201, json);
                } catch (ValidationException | UserAlreadyExistsException ex) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class CategoriesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Category> categories = talentDao.getCategories();
                sendJsonResponse(exchange, 200, toJsonCategories(categories));
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class CompetitionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Competition> competitions = talentDao.getCompetitions();
                sendJsonResponse(exchange, 200, toJsonCompetitions(competitions));
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class TalentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<TalentSubmission> list = talentDao.getSubmissions(null, null, "most_voted");
                sendJsonResponse(exchange, 200, toJsonSubmissions(list));
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class SubmissionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            // Moderator Action: Approve Submission
            if (path.contains("/approve")) {
                int id = extractSubId(path, exchange.getRequestURI().getQuery());
                boolean ok = talentDao.updateSubmissionStatus(id, "APPROVED");
                sendJsonResponse(exchange, ok ? 200 : 400, "{\"success\":" + ok + "}");
                return;
            }

            // Moderator Action: Reject Submission
            if (path.contains("/reject")) {
                int id = extractSubId(path, exchange.getRequestURI().getQuery());
                boolean ok = talentDao.updateSubmissionStatus(id, "REJECTED");
                sendJsonResponse(exchange, ok ? 200 : 400, "{\"success\":" + ok + "}");
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());
                String category = query.get("category");
                String search = query.get("search");
                String sort = query.get("sort");
                List<TalentSubmission> list = talentDao.getSubmissions(category, search, sort);
                sendJsonResponse(exchange, 200, toJsonSubmissions(list));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseFormOrJsonBody(body);
                TalentSubmission sub = new TalentSubmission();
                sub.title = data.getOrDefault("title", "Untitled Talent");
                sub.description = data.getOrDefault("description", data.getOrDefault("bio", ""));
                sub.userId = parseInteger(data.get("userId"), 1);
                sub.competitionId = parseInteger(data.get("competitionId"), 1);
                sub.categoryId = parseInteger(data.get("categoryId"), 1);
                sub.mediaType = data.getOrDefault("mediaType", "video");
                sub.mediaUrl = data.getOrDefault("mediaUrl", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
                sub.thumbnailUrl = data.getOrDefault("thumbnailUrl", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80");

                boolean success = talentDao.createSubmission(sub);
                sendJsonResponse(exchange, success ? 201 : 400, "{\"success\":" + success + "}");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private int extractSubId(String path, String query) {
            String[] parts = path.split("/");
            for (String p : parts) {
                if (p.matches("\\d+")) return Integer.parseInt(p);
            }
            Map<String, String> q = parseQueryParams(query);
            return parseInteger(q.get("id"), parseInteger(q.get("sid"), 1));
        }
    }

    static class SingleSubmissionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());
            int id = parseInteger(query.get("id"), 0);
            if (id > 0) {
                TalentSubmission sub = talentDao.getSubmissionById(id);
                if (sub != null) {
                    sendJsonResponse(exchange, 200, toJsonSubmission(sub));
                    return;
                }
            }
            sendJsonResponse(exchange, 404, "{\"error\":\"Submission not found\"}");
        }
    }

    static class EvaluationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseFormOrJsonBody(body);
                Evaluation eval = new Evaluation();
                eval.submissionId = parseInteger(data.get("submissionId"), 0);
                eval.judgeId = parseInteger(data.get("judgeId"), 6);
                eval.scoreTechnical = parseInteger(data.get("scoreTechnical"), 8);
                eval.scoreCreativity = parseInteger(data.get("scoreCreativity"), 8);
                eval.scorePresentation = parseInteger(data.get("scorePresentation"), 8);
                eval.feedback = data.getOrDefault("feedback", "Solid performance with high technical execution.");

                boolean success = talentDao.addEvaluation(eval);
                sendJsonResponse(exchange, success ? 200 : 400, "{\"success\":" + success + "}");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class VoteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseFormOrJsonBody(body);
                Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());

                int submissionId = parseInteger(data.get("submissionId"),
                    parseInteger(data.get("talentId"),
                        parseInteger(query.get("talentId"),
                            parseInteger(query.get("submissionId"), 1))));
                int userId = parseInteger(data.get("userId"), parseInteger(query.get("userId"), 9));

                boolean success = talentDao.addVote(submissionId, userId);
                sendJsonResponse(exchange, success ? 200 : 400, "{\"success\":" + success + "}");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            SystemStats stats = talentDao.getSystemStats();
            String json = String.format(
                "{\"totalTalents\":%d,\"totalSubmissions\":%d,\"totalVotes\":%d,\"totalEvaluations\":%d,\"totalCompetitions\":%d,\"dbEngine\":\"%s\"}",
                stats.totalTalents, stats.totalSubmissions, stats.totalVotes, stats.totalEvaluations, stats.totalCompetitions, escapeJson(stats.dbEngine)
            );
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<User> users = talentDao.getUsers();
            sendJsonResponse(exchange, 200, JsonUtil.usersToJson(users));
        }
    }

    // ─── Phase 3: Dedicated User Data JSON Representation Endpoint (CLO 2) ───
    static class UserJsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                User user = null;
                if (query != null && query.contains("email=")) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("email=")) {
                            String email = URLDecoder.decode(param.substring(6), StandardCharsets.UTF_8.name());
                            user = talentDao.getUserByEmail(email);
                            break;
                        }
                    }
                }
                if (user == null) {
                    // Default to sample contestant (Alex Rivera)
                    user = talentDao.getUserByEmail("alex@talenthunt.com");
                    if (user == null) {
                        user = new User(1, "Alex Rivera", "alex@talenthunt.com", "", "CONTESTANT",
                            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                            "Passionate acoustic guitarist & vocalist from Seattle.");
                    }
                }
                String jsonOutput = JsonUtil.userToJson(user);
                sendJsonResponse(exchange, 200, jsonOutput);
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Demonstrates parsing incoming JSON user structure
                String body = readRequestBody(exchange);
                User parsedUser = JsonUtil.jsonToUser(body);
                String resultJson = "{\"status\":\"success\",\"message\":\"User parsed successfully via JsonUtil\",\"parsedUser\":" + JsonUtil.userToJson(parsedUser) + "}";
                sendJsonResponse(exchange, 200, resultJson);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("src/main/resources/public" + path);
            if (!file.exists()) {
                file = new File("src/main/webapp" + path);
            }

            if (!file.exists() || file.isDirectory()) {
                sendResponse(exchange, 404, "404 File Not Found");
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            else if (path.endsWith(".jsp")) contentType = "text/plain; charset=UTF-8";
            else if (path.endsWith(".css")) contentType = "text/css";
            else if (path.endsWith(".js")) contentType = "application/javascript";
            else if (path.endsWith(".png")) contentType = "image/png";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (path.endsWith(".svg")) contentType = "image/svg+xml";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    // Helper JSON String Formatter Utilities
    private static String toJsonCategories(List<Category> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Category c = list.get(i);
            sb.append(String.format("{\"id\":%d,\"name\":\"%s\",\"icon\":\"%s\",\"description\":\"%s\",\"submissionCount\":%d}",
                c.id, escapeJson(c.name), escapeJson(c.icon), escapeJson(c.description), c.submissionCount));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonCompetitions(List<Competition> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Competition c = list.get(i);
            sb.append(String.format("{\"id\":%d,\"title\":\"%s\",\"categoryId\":%d,\"categoryName\":\"%s\",\"description\":\"%s\",\"prize\":\"%s\",\"status\":\"%s\",\"startDate\":\"%s\",\"endDate\":\"%s\"}",
                c.id, escapeJson(c.title), c.categoryId, escapeJson(c.categoryName != null ? c.categoryName : ""), escapeJson(c.description != null ? c.description : ""),
                escapeJson(c.prize != null ? c.prize : ""), escapeJson(c.status), escapeJson(c.startDate != null ? c.startDate : ""), escapeJson(c.endDate != null ? c.endDate : "")));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonSubmissions(List<TalentSubmission> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJsonSubmission(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonSubmission(TalentSubmission s) {
        StringBuilder evalsJson = new StringBuilder("[");
        if (s.evaluations != null) {
            for (int j = 0; j < s.evaluations.size(); j++) {
                Evaluation e = s.evaluations.get(j);
                evalsJson.append(String.format("{\"id\":%d,\"judgeId\":%d,\"judgeName\":\"%s\",\"judgeAvatar\":\"%s\",\"scoreTechnical\":%d,\"scoreCreativity\":%d,\"scorePresentation\":%d,\"totalScore\":%.1f,\"feedback\":\"%s\",\"createdAt\":\"%s\"}",
                    e.id, e.judgeId, escapeJson(e.judgeName), escapeJson(e.judgeAvatar), e.scoreTechnical, e.scoreCreativity, e.scorePresentation, e.totalScore, escapeJson(e.feedback != null ? e.feedback : ""), escapeJson(e.createdAt != null ? e.createdAt : "")));
                if (j < s.evaluations.size() - 1) evalsJson.append(",");
            }
        }
        evalsJson.append("]");

        return String.format("{\"id\":%d,\"title\":\"%s\",\"description\":\"%s\",\"userId\":%d,\"userName\":\"%s\",\"userAvatar\":\"%s\",\"userRole\":\"%s\",\"competitionId\":%d,\"competitionTitle\":\"%s\",\"categoryId\":%d,\"categoryName\":\"%s\",\"mediaType\":\"%s\",\"mediaUrl\":\"%s\",\"thumbnailUrl\":\"%s\",\"views\":%d,\"votesCount\":%d,\"averageScore\":%.1f,\"status\":\"%s\",\"submittedAt\":\"%s\",\"evaluations\":%s}",
            s.id, escapeJson(s.title), escapeJson(s.description != null ? s.description : ""), s.userId, escapeJson(s.userName != null ? s.userName : ""), escapeJson(s.userAvatar != null ? s.userAvatar : ""), escapeJson(s.userRole != null ? s.userRole : ""),
            s.competitionId, escapeJson(s.competitionTitle != null ? s.competitionTitle : ""), s.categoryId, escapeJson(s.categoryName != null ? s.categoryName : ""), escapeJson(s.mediaType != null ? s.mediaType : "video"), escapeJson(s.mediaUrl != null ? s.mediaUrl : ""), escapeJson(s.thumbnailUrl != null ? s.thumbnailUrl : ""),
            s.views, s.votesCount, s.averageScore, escapeJson(s.status), escapeJson(s.submittedAt != null ? s.submittedAt : ""), evalsJson.toString());
    }

    private static String toJsonUsers(List<User> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toJson());
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                try {
                    map.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
                } catch (Exception e) {}
            } else if (pair.length == 1) {
                map.put(pair[0], "");
            }
        }
        return map;
    }

    private static Map<String, String> parseFormOrJsonBody(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;
        if (body.trim().startsWith("{")) {
            String clean = body.trim().substring(1, body.trim().length() - 1);
            String[] tokens = clean.split(",");
            for (String token : tokens) {
                String[] pair = token.split(":", 2);
                if (pair.length == 2) {
                    String key = pair[0].trim().replaceAll("^\"|\"$", "");
                    String val = pair[1].trim().replaceAll("^\"|\"$", "");
                    map.put(key, val);
                }
            }
        } else {
            map = parseQueryParams(body);
        }
        return map;
    }

    private static int parseInteger(String val, int def) {
        if (val == null) return def;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
