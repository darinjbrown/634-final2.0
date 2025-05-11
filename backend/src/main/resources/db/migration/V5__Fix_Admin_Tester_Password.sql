-- Fix the AdminTester account password
-- This script updates the password hash for the AdminTester account with a password of 'Test634'

-- Update the user's password hash with a properly generated BCrypt hash for 'Test634'
-- This hash was generated using BCrypt with strength 10, which is the Spring Security default
UPDATE users 
SET password_hash = '$2a$10$P97CZ1KV.q/ujxHq5xMBQ.fLJJqg5aIGtwgEFpsUR7kUwGFDPCnDC',
    updated_at = NOW()
WHERE username = 'AdminTester';

-- If the AdminTester account doesn't exist yet, create it
INSERT INTO users (username, email, password_hash, first_name, last_name, created_at, updated_at)
SELECT 'AdminTester', 'admin.tester@example.com', 
       '$2a$10$P97CZ1KV.q/ujxHq5xMBQ.fLJJqg5aIGtwgEFpsUR7kUwGFDPCnDC',
       'Admin', 'Tester', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'AdminTester');

-- Ensure the user has the ADMIN role
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'AdminTester'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'AdminTester') AND role = 'ADMIN');

-- Ensure the user has the USER role
INSERT INTO user_roles (user_id, role)
SELECT id, 'USER' FROM users WHERE username = 'AdminTester'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'AdminTester') AND role = 'USER');