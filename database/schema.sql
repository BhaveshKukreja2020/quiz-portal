-- ============================================================
--  Online Quiz Portal — MySQL Schema  (Phase 3 Complete)
--  Compatible: MySQL 5.7+ / 8.x
--  Run once: mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS quiz_portal
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE quiz_portal;

-- --------------------------------------------------------
-- 1. users
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('student','admin') NOT NULL DEFAULT 'student',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login  DATETIME     NULL,
    UNIQUE KEY uq_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (name, email, password, role)
VALUES ('Admin', 'admin@quizportal.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh9y', 'admin')
ON DUPLICATE KEY UPDATE id = id;

-- --------------------------------------------------------
-- 2. tags
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS tags (
    id        INT         AUTO_INCREMENT PRIMARY KEY,
    tag_name  VARCHAR(80) NOT NULL,
    UNIQUE KEY uq_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO tags (tag_name) VALUES
    ('Python'),('Java'),('Data Science'),('Machine Learning'),
    ('Web Development'),('Database'),('Algorithms'),('Operating Systems');

-- --------------------------------------------------------
-- 3. questions  (now includes explanation)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
    id             INT          AUTO_INCREMENT PRIMARY KEY,
    question_text  TEXT         NOT NULL,
    option1        VARCHAR(500) NOT NULL,
    option2        VARCHAR(500) NOT NULL,
    option3        VARCHAR(500) NOT NULL,
    option4        VARCHAR(500) NOT NULL,
    correct_answer TINYINT      NOT NULL,
    difficulty     ENUM('easy','medium','hard') NOT NULL DEFAULT 'medium',
    explanation    TEXT         NULL,        -- NEW: post-answer explanation
    created_by     INT          NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_ca CHECK (correct_answer BETWEEN 1 AND 4),
    CONSTRAINT fk_q_creator FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_q_difficulty (difficulty),
    INDEX idx_q_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 4. question_tags
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS question_tags (
    question_id INT NOT NULL,
    tag_id      INT NOT NULL,
    PRIMARY KEY (question_id, tag_id),
    CONSTRAINT fk_qt_q FOREIGN KEY (question_id) REFERENCES questions(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_qt_t FOREIGN KEY (tag_id) REFERENCES tags(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 5. quiz_sessions
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS quiz_sessions (
    id              INT           AUTO_INCREMENT PRIMARY KEY,
    session_id      CHAR(36)      NOT NULL,
    user_id         INT           NOT NULL,
    selected_tags   VARCHAR(500)  NULL,
    total_questions INT           NOT NULL DEFAULT 0,
    score           INT           NOT NULL DEFAULT 0,
    weighted_score  DECIMAL(8,2)  NOT NULL DEFAULT 0.00,   -- NEW: difficulty-weighted
    accuracy        DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    time_taken_secs INT           NOT NULL DEFAULT 0,       -- NEW: actual time used
    status          ENUM('in_progress','completed','auto_submitted') NOT NULL DEFAULT 'in_progress',
    started_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    DATETIME      NULL,
    UNIQUE KEY uq_session_id (session_id),
    CONSTRAINT fk_qs_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_qs_user   (user_id),
    INDEX idx_qs_status (status),
    INDEX idx_qs_score  (score DESC),
    INDEX idx_qs_acc    (accuracy DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 6. attempts
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS attempts (
    id               INT         AUTO_INCREMENT PRIMARY KEY,
    user_id          INT         NOT NULL,
    session_id       CHAR(36)    NOT NULL,
    question_id      INT         NOT NULL,
    selected_answer  TINYINT     NULL,
    is_correct       TINYINT(1)  NOT NULL DEFAULT 0,
    time_spent_secs  INT         NOT NULL DEFAULT 0,        -- NEW: per-question time
    answered_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_a_user     FOREIGN KEY (user_id)     REFERENCES users(id)     ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_a_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_a_user     (user_id),
    INDEX idx_a_session  (session_id),
    INDEX idx_a_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 7. quiz_progress  (auto-save / resume)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS quiz_progress (
    progress_id        INT       AUTO_INCREMENT PRIMARY KEY,
    user_id            INT       NOT NULL,
    session_id         CHAR(36)  NOT NULL,
    current_question   INT       NOT NULL DEFAULT 0,
    remaining_time     INT       NOT NULL DEFAULT 0,
    saved_answers      TEXT      NULL,
    question_order     TEXT      NULL,
    current_difficulty ENUM('easy','medium','hard') NOT NULL DEFAULT 'medium',
    warning_count      INT       NOT NULL DEFAULT 0,
    quiz_status        ENUM('in_progress','completed','auto_submitted') NOT NULL DEFAULT 'in_progress',
    last_saved_time    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_session (user_id, session_id),
    CONSTRAINT fk_p_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_p_user   (user_id),
    INDEX idx_p_status (quiz_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 8. leaderboard  (NEW — cached rankings for performance)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS leaderboard (
    id           INT          AUTO_INCREMENT PRIMARY KEY,
    user_id      INT          NOT NULL,
    user_name    VARCHAR(100) NOT NULL,
    total_score  INT          NOT NULL DEFAULT 0,
    total_quizzes INT         NOT NULL DEFAULT 0,
    avg_accuracy DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    best_score   INT          NOT NULL DEFAULT 0,
    tag_id       INT          NULL,          -- NULL = overall; set = per-topic
    period       ENUM('all_time','monthly','weekly') NOT NULL DEFAULT 'all_time',
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lb_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lb_tag  FOREIGN KEY (tag_id)  REFERENCES tags(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uq_lb (user_id, tag_id, period),
    INDEX idx_lb_score    (total_score DESC),
    INDEX idx_lb_accuracy (avg_accuracy DESC),
    INDEX idx_lb_period   (period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 9. topic_performance  (NEW — per-student per-tag stats)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS topic_performance (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NOT NULL,
    tag_id          INT          NOT NULL,
    correct_count   INT          NOT NULL DEFAULT 0,
    total_count     INT          NOT NULL DEFAULT 0,
    accuracy        DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    mastery_level   ENUM('beginner','intermediate','advanced','master') NOT NULL DEFAULT 'beginner',
    last_updated    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_tp (user_id, tag_id),
    CONSTRAINT fk_tp_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tp_tag  FOREIGN KEY (tag_id)  REFERENCES tags(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_tp_user    (user_id),
    INDEX idx_tp_mastery (mastery_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 10. question_analytics  (NEW — per-question stats)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS question_analytics (
    question_id      INT          PRIMARY KEY,
    times_shown      INT          NOT NULL DEFAULT 0,
    times_correct    INT          NOT NULL DEFAULT 0,
    avg_time_secs    DECIMAL(6,1) NOT NULL DEFAULT 0.0,
    difficulty_index DECIMAL(5,2) NOT NULL DEFAULT 0.50,   -- 0=hardest, 1=easiest
    last_updated     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_qa_q FOREIGN KEY (question_id) REFERENCES questions(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 11. activity_log  (NEW — admin audit trail)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS activity_log (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50)  NULL,
    entity_id   INT          NULL,
    detail      TEXT         NULL,
    ip_address  VARCHAR(45)  NULL,
    logged_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_al_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_al_user   (user_id),
    INDEX idx_al_action (action),
    INDEX idx_al_time   (logged_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
