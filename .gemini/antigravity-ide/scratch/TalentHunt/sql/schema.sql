-- =========================================================
-- Talent Hunt System Database Schema (MySQL Compatible)
-- Database Name: talent_hunt_db
-- =========================================================

CREATE DATABASE IF NOT EXISTS talent_hunt_db;
USE talent_hunt_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL DEFAULT 'user123',
    role VARCHAR(20) NOT NULL DEFAULT 'CONTESTANT', -- 'CONTESTANT', 'JUDGE', 'AUDIENCE', 'ADMIN'
    avatar_url VARCHAR(500),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    description TEXT
);

-- 3. Competitions Table
CREATE TABLE IF NOT EXISTS competitions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category_id INT,
    description TEXT,
    prize VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'UPCOMING', 'ACTIVE', 'COMPLETED'
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 4. Talent Submissions Table
CREATE TABLE IF NOT EXISTS talent_submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    user_id INT NOT NULL,
    competition_id INT NOT NULL,
    category_id INT NOT NULL,
    media_type VARCHAR(20) DEFAULT 'video', -- 'video', 'audio', 'image'
    media_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    views INT DEFAULT 0,
    votes_count INT DEFAULT 0,
    average_score DECIMAL(3, 1) DEFAULT 0.0,
    status VARCHAR(20) DEFAULT 'APPROVED', -- 'PENDING', 'APPROVED', 'REJECTED'
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (competition_id) REFERENCES competitions(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 5. Judge Evaluations Table
CREATE TABLE IF NOT EXISTS evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    judge_id INT NOT NULL,
    score_technical INT NOT NULL,
    score_creativity INT NOT NULL,
    score_presentation INT NOT NULL,
    total_score DECIMAL(3, 1) NOT NULL,
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES talent_submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (judge_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Audience Votes Table
CREATE TABLE IF NOT EXISTS votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_submission UNIQUE (submission_id, user_id),
    FOREIGN KEY (submission_id) REFERENCES talent_submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
