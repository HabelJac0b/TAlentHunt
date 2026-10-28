package com.talenthunt.dao;

import com.talenthunt.db.DatabaseManager;
import com.talenthunt.model.User;
import com.talenthunt.model.Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TalentDao {

    public User authenticateUser(String emailOrUser, String password, String targetPortal) {
        String cleanInput = emailOrUser != null ? emailOrUser.toLowerCase().trim() : "";
        String cleanPass = password != null ? password.trim() : "";

        // 1. Direct Database Query
        String sql = "SELECT * FROM users WHERE (LOWER(email) = ? OR LOWER(name) = ? OR (LOWER(?) = 'admin' AND role = 'ADMIN') OR (LOWER(?) IN ('user', 'user@talenthunt.com', 'alex', 'alex@talenthunt.com') AND role = 'CONTESTANT')) AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cleanInput);
            ps.setString(2, cleanInput);
            ps.setString(3, cleanInput);
            ps.setString(4, cleanInput);
            ps.setString(5, cleanPass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("ADMIN".equalsIgnoreCase(targetPortal) && !"ADMIN".equalsIgnoreCase(role)) {
                        return null; // Non-admin attempting admin portal login
                    }
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", // Hide password from output
                        rs.getString("role"),
                        rs.getString("avatar_url"),
                        rs.getString("bio")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Guaranteed Demo Fallback (supports both alex@talenthunt.com and user@talenthunt.com)
        if ("ADMIN".equalsIgnoreCase(targetPortal)) {
            if (("admin".equals(cleanInput) || "admin@talenthunt.com".equals(cleanInput)) && "admin123".equals(cleanPass)) {
                return new User(8, "Admin SuperUser", "admin@talenthunt.com", "", "ADMIN", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80", "System Administrator & Talent Hunt Director.");
            }
        } else {
            if (("alex@talenthunt.com".equals(cleanInput) || "user@talenthunt.com".equals(cleanInput) || "alex".equals(cleanInput) || "user".equals(cleanInput)) && "user123".equals(cleanPass)) {
                return new User(1, "Alex Rivera", "alex@talenthunt.com", "", "CONTESTANT", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80", "Passionate acoustic guitarist & vocalist from Seattle.");
            }
        }

        return null;
    }

    public User registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, password, role, avatar_url, bio) VALUES (?, ?, ?, ?, ?, ?)";
        String defaultAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email.toLowerCase().trim());
            ps.setString(3, password);
            ps.setString(4, role != null && !role.isEmpty() ? role.toUpperCase() : "CONTESTANT");
            ps.setString(5, defaultAvatar);
            ps.setString(6, "Registered Talent System User.");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        return new User(newId, name, email, "", role, defaultAvatar, "Registered Talent System User.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    "",
                    rs.getString("role"),
                    rs.getString("avatar_url"),
                    rs.getString("bio")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(s.id) as sub_count FROM categories c " +
                     "LEFT JOIN talent_submissions s ON c.id = s.category_id " +
                     "GROUP BY c.id ORDER BY c.id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category cat = new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("icon"),
                    rs.getString("description")
                );
                cat.submissionCount = rs.getInt("sub_count");
                categories.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Competition> getCompetitions() {
        List<Competition> list = new ArrayList<>();
        String sql = "SELECT comp.*, c.name as category_name FROM competitions comp " +
                     "LEFT JOIN categories c ON comp.category_id = c.id ORDER BY comp.id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Competition comp = new Competition();
                comp.id = rs.getInt("id");
                comp.title = rs.getString("title");
                comp.categoryId = rs.getInt("category_id");
                comp.categoryName = rs.getString("category_name");
                comp.description = rs.getString("description");
                comp.prize = rs.getString("prize");
                comp.status = rs.getString("status");
                comp.startDate = rs.getString("start_date");
                comp.endDate = rs.getString("end_date");
                list.add(comp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TalentSubmission> getSubmissions(String categoryIdStr, String search, String sortBy) {
        List<TalentSubmission> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, u.name as user_name, u.avatar_url as user_avatar, u.role as user_role, " +
            "c.name as category_name, comp.title as competition_title " +
            "FROM talent_submissions s " +
            "JOIN users u ON s.user_id = u.id " +
            "JOIN categories c ON s.category_id = c.id " +
            "LEFT JOIN competitions comp ON s.competition_id = comp.id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (categoryIdStr != null && !categoryIdStr.isEmpty() && !"all".equalsIgnoreCase(categoryIdStr)) {
            sql.append(" AND s.category_id = ? ");
            params.add(Integer.parseInt(categoryIdStr));
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(s.title) LIKE ? OR LOWER(s.description) LIKE ? OR LOWER(u.name) LIKE ?) ");
            String queryPattern = "%" + search.toLowerCase().trim() + "%";
            params.add(queryPattern);
            params.add(queryPattern);
            params.add(queryPattern);
        }

        if ("top_rated".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY s.average_score DESC, s.votes_count DESC ");
        } else if ("most_voted".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY s.votes_count DESC, s.average_score DESC ");
        } else {
            sql.append(" ORDER BY s.submitted_at DESC ");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TalentSubmission sub = mapSubmission(rs);
                    list.add(sub);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public TalentSubmission getSubmissionById(int id) {
        String sql = "SELECT s.*, u.name as user_name, u.avatar_url as user_avatar, u.role as user_role, " +
                     "c.name as category_name, comp.title as competition_title " +
                     "FROM talent_submissions s " +
                     "JOIN users u ON s.user_id = u.id " +
                     "JOIN categories c ON s.category_id = c.id " +
                     "LEFT JOIN competitions comp ON s.competition_id = comp.id WHERE s.id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TalentSubmission sub = mapSubmission(rs);
                    sub.evaluations = getEvaluationsForSubmission(id, conn);
                    return sub;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Evaluation> getEvaluationsForSubmission(int submissionId, Connection conn) throws SQLException {
        List<Evaluation> evals = new ArrayList<>();
        String sql = "SELECT e.*, u.name as judge_name, u.avatar_url as judge_avatar " +
                     "FROM evaluations e JOIN users u ON e.judge_id = u.id WHERE e.submission_id = ? " +
                     "ORDER BY e.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evaluation ev = new Evaluation();
                    ev.id = rs.getInt("id");
                    ev.submissionId = rs.getInt("submission_id");
                    ev.judgeId = rs.getInt("judge_id");
                    ev.judgeName = rs.getString("judge_name");
                    ev.judgeAvatar = rs.getString("judge_avatar");
                    ev.scoreTechnical = rs.getInt("score_technical");
                    ev.scoreCreativity = rs.getInt("score_creativity");
                    ev.scorePresentation = rs.getInt("score_presentation");
                    ev.totalScore = rs.getDouble("total_score");
                    ev.feedback = rs.getString("feedback");
                    ev.createdAt = rs.getString("created_at");
                    evals.add(ev);
                }
            }
        }
        return evals;
    }

    public boolean createSubmission(TalentSubmission sub) {
        String sql = "INSERT INTO talent_submissions (title, description, user_id, competition_id, category_id, media_type, media_url, thumbnail_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'APPROVED')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sub.title);
            ps.setString(2, sub.description);
            ps.setInt(3, sub.userId > 0 ? sub.userId : 1);
            ps.setInt(4, sub.competitionId > 0 ? sub.competitionId : 1);
            ps.setInt(5, sub.categoryId > 0 ? sub.categoryId : 1);
            ps.setString(6, sub.mediaType != null ? sub.mediaType : "video");
            ps.setString(7, sub.mediaUrl);
            ps.setString(8, sub.thumbnailUrl != null && !sub.thumbnailUrl.isEmpty() ? sub.thumbnailUrl : "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80");
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addEvaluation(Evaluation eval) {
        String insertSql = "INSERT INTO evaluations (submission_id, judge_id, score_technical, score_creativity, score_presentation, total_score, feedback) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, eval.submissionId);
                ps.setInt(2, eval.judgeId > 0 ? eval.judgeId : 6);
                ps.setInt(3, eval.scoreTechnical);
                ps.setInt(4, eval.scoreCreativity);
                ps.setInt(5, eval.scorePresentation);
                
                double avg = (eval.scoreTechnical + eval.scoreCreativity + eval.scorePresentation) / 3.0;
                ps.setDouble(6, Math.round(avg * 10.0) / 10.0);
                ps.setString(7, eval.feedback);
                ps.executeUpdate();
            }

            // Recalculate average score for submission
            String updateSql = "UPDATE talent_submissions SET average_score = (" +
                               "SELECT AVG(total_score) FROM evaluations WHERE submission_id = ?" +
                               ") WHERE id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                ps2.setInt(1, eval.submissionId);
                ps2.setInt(2, eval.submissionId);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addVote(int submissionId, int userId) {
        String voteSql = "INSERT INTO votes (submission_id, user_id) VALUES (?, ?)";
        String countSql = "UPDATE talent_submissions SET votes_count = votes_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(voteSql)) {
                ps.setInt(1, submissionId);
                ps.setInt(2, userId > 0 ? userId : 9);
                ps.executeUpdate();
            } catch (SQLException dupEx) {
                // Ignore if user already voted
            }

            try (PreparedStatement ps2 = conn.prepareStatement(countSql)) {
                ps2.setInt(1, submissionId);
                ps2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public SystemStats getSystemStats() {
        SystemStats stats = new SystemStats();
        stats.dbEngine = DatabaseManager.isH2Fallback() ? "Embedded H2 (MySQL Mode)" : "MySQL Server 8/26";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) stats.totalTalents = rs.getInt(1);
                
                rs = stmt.executeQuery("SELECT COUNT(*) FROM talent_submissions");
                if (rs.next()) stats.totalSubmissions = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM votes");
                if (rs.next()) stats.totalVotes = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM evaluations");
                if (rs.next()) stats.totalEvaluations = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM competitions");
                if (rs.next()) stats.totalCompetitions = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public boolean isEmailRegistered(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email != null ? email.toLowerCase().trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email != null ? email.toLowerCase().trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSubmissionStatus(int id, String status) {
        String sql = "UPDATE talent_submissions SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status != null ? status.toUpperCase().trim() : "APPROVED");
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            "",
            rs.getString("role"),
            rs.getString("avatar_url"),
            rs.getString("bio")
        );
    }

    private TalentSubmission mapSubmission(ResultSet rs) throws SQLException {
        TalentSubmission sub = new TalentSubmission();
        sub.id = rs.getInt("id");
        sub.title = rs.getString("title");
        sub.description = rs.getString("description");
        sub.userId = rs.getInt("user_id");
        sub.userName = rs.getString("user_name");
        sub.userAvatar = rs.getString("user_avatar");
        sub.userRole = rs.getString("user_role");
        sub.competitionId = rs.getInt("competition_id");
        sub.competitionTitle = rs.getString("competition_title");
        sub.categoryId = rs.getInt("category_id");
        sub.categoryName = rs.getString("category_name");
        sub.mediaType = rs.getString("media_type");
        sub.mediaUrl = rs.getString("media_url");
        sub.thumbnailUrl = rs.getString("thumbnail_url");
        sub.views = rs.getInt("views");
        sub.votesCount = rs.getInt("votes_count");
        sub.averageScore = rs.getDouble("average_score");
        sub.status = rs.getString("status");
        sub.submittedAt = rs.getString("submitted_at");
        return sub;
    }
}
