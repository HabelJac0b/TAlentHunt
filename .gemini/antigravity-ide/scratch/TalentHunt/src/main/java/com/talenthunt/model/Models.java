package com.talenthunt.model;

import java.sql.Timestamp;
import java.util.List;

public class Models {

    public static class User extends com.talenthunt.model.User {
        public User() {
            super();
        }
        public User(int id, String name, String email, String password, String role, String avatarUrl, String bio) {
            super(id, name, email, password, role, avatarUrl, bio);
        }
    }

    public static class Category {
        public int id;
        public String name;
        public String icon;
        public String description;
        public int submissionCount;

        public Category() {}
        public Category(int id, String name, String icon, String description) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.description = description;
        }
    }

    public static class Competition {
        public int id;
        public String title;
        public int categoryId;
        public String categoryName;
        public String description;
        public String prize;
        public String status; // UPCOMING, ACTIVE, COMPLETED
        public String startDate;
        public String endDate;

        public Competition() {}
    }

    public static class TalentSubmission {
        public int id;
        public String title;
        public String description;
        public int userId;
        public String userName;
        public String userAvatar;
        public String userRole;
        public int competitionId;
        public String competitionTitle;
        public int categoryId;
        public String categoryName;
        public String mediaType; // video, audio, image, code
        public String mediaUrl;
        public String thumbnailUrl;
        public int views;
        public int votesCount;
        public double averageScore;
        public String status; // PENDING, APPROVED, REJECTED
        public String submittedAt;
        public List<Evaluation> evaluations;

        public TalentSubmission() {}
    }

    public static class Evaluation {
        public int id;
        public int submissionId;
        public int judgeId;
        public String judgeName;
        public String judgeAvatar;
        public int scoreTechnical;
        public int scoreCreativity;
        public int scorePresentation;
        public double totalScore;
        public String feedback;
        public String createdAt;

        public Evaluation() {}
    }

    public static class Vote {
        public int id;
        public int submissionId;
        public int userId;
        public String createdAt;

        public Vote() {}
    }

    public static class SystemStats {
        public int totalTalents;
        public int totalSubmissions;
        public int totalVotes;
        public int totalEvaluations;
        public int totalCompetitions;
        public String dbEngine;

        public SystemStats() {}
    }
}
