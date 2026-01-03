-- Database Migration Script for Enhanced Security
-- Run this script to upgrade existing database to version 2.0

USE lk_job;

-- 1. Backup existing data (recommended to backup before running)
-- mysqldump -u root -p lk_job > lk_job_backup_$(date +%Y%m%d).sql

-- 2. Add new columns for enhanced security
ALTER TABLE users 
ADD COLUMN last_login TIMESTAMP NULL AFTER created_at,
ADD COLUMN failed_login_attempts INT DEFAULT 0 AFTER last_login,
ADD COLUMN account_locked BOOLEAN DEFAULT FALSE AFTER failed_login_attempts,
ADD COLUMN email_verified BOOLEAN DEFAULT FALSE AFTER account_locked,
ADD COLUMN verification_token VARCHAR(100) NULL AFTER email_verified,
ADD COLUMN reset_token VARCHAR(100) NULL AFTER verification_token,
ADD COLUMN reset_token_expiry TIMESTAMP NULL AFTER reset_token;

-- 3. Add indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_jobs_employer ON jobs(employer_id);
CREATE INDEX idx_jobs_created ON jobs(created_at);
CREATE INDEX idx_applications_job ON applications(job_id);
CREATE INDEX idx_applications_student ON applications(student_id);
CREATE INDEX idx_applications_status ON applications(status);

-- 4. Update password column to accommodate BCrypt hashes (60 characters)
ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NOT NULL;
ALTER TABLE students MODIFY COLUMN password VARCHAR(255) NULL;
ALTER TABLE admin MODIFY COLUMN password VARCHAR(255) NOT NULL;

-- 5. Add audit columns
ALTER TABLE jobs 
ADD COLUMN updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

ALTER TABLE applications 
ADD COLUMN updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP AFTER application_date;

-- 6. Create activity log table for security auditing
CREATE TABLE IF NOT EXISTS activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NULL,
    entity_id INT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_activity_user (user_id),
    INDEX idx_activity_action (action),
    INDEX idx_activity_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 7. Create email queue table for async email sending
CREATE TABLE IF NOT EXISTS email_queue (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    status ENUM('pending', 'sent', 'failed') DEFAULT 'pending',
    attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    error_message TEXT NULL,
    INDEX idx_email_status (status),
    INDEX idx_email_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 8. Add resume file metadata
ALTER TABLE students 
ADD COLUMN resume_filename VARCHAR(255) NULL AFTER resume_path,
ADD COLUMN resume_size INT NULL AFTER resume_filename,
ADD COLUMN resume_uploaded_at TIMESTAMP NULL AFTER resume_size;

-- 9. Enhanced job posting fields
ALTER TABLE jobs 
ADD COLUMN job_type ENUM('full-time', 'part-time', 'contract', 'internship') DEFAULT 'full-time' AFTER requirements,
ADD COLUMN salary_min DECIMAL(10,2) NULL AFTER job_type,
ADD COLUMN salary_max DECIMAL(10,2) NULL AFTER salary_min,
ADD COLUMN application_deadline DATE NULL AFTER salary_max,
ADD COLUMN is_active BOOLEAN DEFAULT TRUE AFTER application_deadline;

-- 10. Add application notes for employers
ALTER TABLE applications 
ADD COLUMN employer_notes TEXT NULL AFTER status,
ADD COLUMN interview_date TIMESTAMP NULL AFTER employer_notes;

-- 11. Create table for saved jobs
CREATE TABLE IF NOT EXISTS saved_jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    job_id INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE KEY unique_save (student_id, job_id),
    INDEX idx_saved_student (student_id),
    INDEX idx_saved_job (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 12. IMPORTANT: Existing passwords need to be rehashed with BCrypt
-- This cannot be done automatically. Users will need to:
-- Option 1: Reset passwords through "Forgot Password" feature
-- Option 2: Admin can run a one-time migration script to hash existing passwords
-- Option 3: Force password reset on next login

-- Note: After running this migration, implement a flag to force existing users
-- to reset their passwords on next login

-- 13. Add data integrity constraints
ALTER TABLE applications 
ADD CONSTRAINT check_status CHECK (status IN ('applied', 'interviewed', 'accepted', 'rejected'));

ALTER TABLE users 
ADD CONSTRAINT check_role CHECK (role IN ('student', 'employer', 'admin'));

-- 14. Create notification preferences table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email_notifications BOOLEAN DEFAULT TRUE,
    application_updates BOOLEAN DEFAULT TRUE,
    new_job_alerts BOOLEAN DEFAULT TRUE,
    weekly_digest BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_prefs (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 15. Insert default notification preferences for existing users
INSERT INTO notification_preferences (user_id)
SELECT id FROM users
WHERE id NOT IN (SELECT user_id FROM notification_preferences);

COMMIT;

-- Migration completed successfully!
-- Remember to:
-- 1. Update application.properties with correct database credentials
-- 2. Test all functionality thoroughly
-- 3. Monitor logs for any errors
-- 4. Implement password reset for existing users
