-- Reset the AdminTester account with a reliable password hash
-- This script uses a known working BCrypt hash for 'Test634'

-- If the user exists, update their password
UPDATE users 
SET password_hash = '$2a$10$6uA8VPxVzUqPUBL3QIlGY.CSVKtMVp7A4hBQg9IR3ME3WOH7.jCF6',
    updated_at = NOW()
WHERE username = 'AdminTester';

-- If the user doesn't exist, create it with the proper hash and roles
INSERT INTO users (username, email, password_hash, first_name, last_name, created_at, updated_at)
SELECT 'AdminTester', 'admin.tester@example.com', 
       '$2a$10$6uA8VPxVzUqPUBL3QIlGY.CSVKtMVp7A4hBQg9IR3ME3WOH7.jCF6',
       'Admin', 'Tester', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'AdminTester');

-- Make sure the user has the ADMIN role
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'AdminTester'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'AdminTester') AND role = 'ADMIN');

-- Make sure the user has the USER role
INSERT INTO user_roles (user_id, role)
SELECT id, 'USER' FROM users WHERE username = 'AdminTester'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'AdminTester') AND role = 'USER');