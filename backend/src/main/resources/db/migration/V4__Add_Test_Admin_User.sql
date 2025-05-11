-- Add a test admin user with username 'AdminTester' and password 'Test634'
-- Note: The password hash is generated using BCrypt algorithm for 'Test634'

-- Create the test admin user if it doesn't already exist
INSERT INTO users (username, email, password_hash, first_name, last_name, created_at, updated_at)
SELECT 'AdminTester', 'admin.tester@example.com', 
       -- BCrypt hash for password 'Test634'
       '$2a$10$MYVGS6NPAHHccqbEuGa23uAFRXykdVtsvnMMgQ83q4aQymRgo0k1q', 
       'Admin', 'Tester', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'AdminTester');

-- Assign the ADMIN role to the new user
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'AdminTester';

-- Also assign the USER role to ensure the user has all user capabilities
INSERT INTO user_roles (user_id, role)
SELECT id, 'USER' FROM users WHERE username = 'AdminTester'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'AdminTester') AND role = 'USER');