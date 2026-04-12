-- Reset DB
DROP DATABASE IF EXISTS minesweeper;
CREATE DATABASE minesweeper;
USE minesweeper;

-- =========================================
-- 1. PLAYERS
-- =========================================
CREATE TABLE players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================
-- 2. GAME RESULTS (mỗi trận)
-- =========================================
CREATE TABLE game_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    player_id BIGINT NOT NULL,
    played_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    time_seconds DECIMAL(10,3) NOT NULL,  -- ví dụ 80.651
    experience INT DEFAULT 0,
    minecoins INT DEFAULT 0,
    mastery INT DEFAULT 0,

    difficulty ENUM('easy', 'medium', 'hard') NOT NULL,

    FOREIGN KEY (player_id)
        REFERENCES players(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================================
-- 3. PLAYER STATS (tổng hợp)
-- =========================================
CREATE TABLE player_stats (
    player_id BIGINT PRIMARY KEY,

    total_experience BIGINT DEFAULT 0,
    total_minecoins BIGINT DEFAULT 0,
    total_mastery BIGINT DEFAULT 0,

    best_time DECIMAL(10,3),  -- càng nhỏ càng tốt

    total_games INT DEFAULT 0,

    FOREIGN KEY (player_id)
        REFERENCES players(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================================
-- 4. INDEX (tối ưu)
-- =========================================
CREATE INDEX idx_results_player ON game_results(player_id);
CREATE INDEX idx_results_time ON game_results(time_seconds);
CREATE INDEX idx_results_played_at ON game_results(played_at);

-- =========================================
-- 5. DATA TEST
-- =========================================
INSERT INTO players (username) VALUES 
('Hung'),
('Khai'),
('Linh');

-- =========================================
-- 6. LOGIC LƯU GAME (QUAN TRỌNG NHẤT)
-- =========================================

START TRANSACTION;

-- Insert kết quả trận
INSERT INTO game_results 
(player_id, time_seconds, experience, minecoins, mastery, difficulty)
VALUES (1, 80.651, 15, 1, 2, 'medium');

-- Update stats (UPSERT)
INSERT INTO player_stats 
(player_id, total_experience, total_minecoins, total_mastery, best_time, total_games)
VALUES (1, 15, 1, 2, 80.651, 1)
ON DUPLICATE KEY UPDATE
    total_experience = total_experience + VALUES(total_experience),
    total_minecoins = total_minecoins + VALUES(total_minecoins),
    total_mastery = total_mastery + VALUES(total_mastery),
    total_games = total_games + 1,
    best_time = LEAST(best_time, VALUES(best_time));

COMMIT;

-- =========================================
-- 7. LEADERBOARD (time thấp nhất thắng)
-- =========================================
SELECT 
    p.username,
    ps.best_time,
    ps.total_games
FROM player_stats ps
JOIN players p ON p.id = ps.player_id
ORDER BY ps.best_time ASC
LIMIT 10;

-- =========================================
-- 8. HISTORY PLAYER
-- =========================================
SELECT *
FROM game_results
WHERE player_id = 1
ORDER BY played_at DESC;

-- =========================================
-- 9. BEST TIME THEO LEVEL
-- =========================================
SELECT difficulty, MIN(time_seconds) AS best_time
FROM game_results
GROUP BY difficulty;

-- =========================================
-- 10. RANK REALTIME
-- =========================================
SELECT 
    p.username,
    ps.best_time,
    RANK() OVER (ORDER BY ps.best_time ASC) AS rank_position
FROM player_stats ps
JOIN players p ON p.id = ps.player_id;