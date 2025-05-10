-- Initial sample data for Flight Search Application

-- Insert sample users (password hash for 'password123')
INSERT INTO users (username, email, password_hash, first_name, last_name)
VALUES 
('johndoe', 'john.doe@example.com', '$2a$10$1eTN6LKg5Q3oA.Kg8JDIcOmS3SYA0IE9S2lR.Rjnk3qWyyn3IY8KC', 'John', 'Doe'),
('janedoe', 'jane.doe@example.com', '$2a$10$1eTN6LKg5Q3oA.Kg8JDIcOmS3SYA0IE9S2lR.Rjnk3qWyyn3IY8KC', 'Jane', 'Doe'),
('testuser', 'test.user@example.com', '$2a$10$1eTN6LKg5Q3oA.Kg8JDIcOmS3SYA0IE9S2lR.Rjnk3qWyyn3IY8KC', 'Test', 'User');

-- Insert sample flight searches
INSERT INTO flight_searches (user_id, origin, destination, departure_date, return_date, number_of_travelers, trip_type)
VALUES 
(1, 'JFK', 'LAX', '2025-06-15', '2025-06-22', 2, 'round-trip'),
(1, 'BOS', 'SFO', '2025-07-10', NULL, 1, 'one-way'),
(2, 'ORD', 'MIA', '2025-08-01', '2025-08-08', 3, 'round-trip');

-- Insert sample saved flights
INSERT INTO saved_flights (user_id, airline_code, airline_name, flight_number, origin, destination, departure_time, arrival_time, price)
VALUES 
(1, 'AA', 'American Airlines', 'AA123', 'JFK', 'LAX', '2025-06-15 08:00:00', '2025-06-15 11:30:00', 349.99),
(1, 'DL', 'Delta Air Lines', 'DL456', 'LAX', 'JFK', '2025-06-22 14:15:00', '2025-06-22 22:45:00', 399.99),
(2, 'UA', 'United Airlines', 'UA789', 'ORD', 'MIA', '2025-08-01 06:30:00', '2025-08-01 10:15:00', 289.99);

-- Insert sample booking records
INSERT INTO booking_records (user_id, booking_reference, airline_code, flight_number, origin, destination, departure_time, arrival_time, passenger_count, total_price, booking_status)
VALUES 
(1, 'ABC123XYZ', 'AA', 'AA123', 'JFK', 'LAX', '2025-06-15 08:00:00', '2025-06-15 11:30:00', 2, 699.98, 'CONFIRMED'),
(2, 'DEF456UVW', 'UA', 'UA789', 'ORD', 'MIA', '2025-08-01 06:30:00', '2025-08-01 10:15:00', 3, 869.97, 'CONFIRMED');

-- Insert audit trail entries
INSERT INTO audit_trail (user_id, action_type, entity_type, entity_id, description, ip_address)
VALUES 
(1, 'CREATE', 'BOOKING', 1, 'User created a new booking with reference ABC123XYZ', '192.168.1.100'),
(2, 'CREATE', 'BOOKING', 2, 'User created a new booking with reference DEF456UVW', '192.168.1.101');