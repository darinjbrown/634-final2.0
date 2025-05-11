-- Create user_roles table for role-based access control
CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role VARCHAR(50) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Add index for faster role lookups
CREATE INDEX idx_user_roles_role ON user_roles (role);

-- Assign the USER role to all existing users as a default
INSERT INTO user_roles (user_id, role)
SELECT id, 'USER' FROM users;

-- Create a default admin user if one doesn't exist
-- The password hash is for 'admin123' using BCrypt
INSERT INTO users (username, email, password_hash, first_name, last_name, created_at, updated_at)
SELECT 'admin', 'admin@example.com', '$2a$10$6AHDYFq.lVMXyBJBOad64O0meBPVgiVhDgTQZC2v7xGXF5J8cWPMC', 'Admin', 'User', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Set the admin user to have ADMIN role
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'admin';