-- Training types
INSERT INTO training_types (name) VALUES ('Fitness');
INSERT INTO training_types (name) VALUES ('Yoga');
INSERT INTO training_types (name) VALUES ('Zumba');
INSERT INTO training_types (name) VALUES ('Stretching');
INSERT INTO training_types (name) VALUES ('Resistance');

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Users (trainers)
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES ('Alice', 'Johnson', 'Alice.Johnson', crypt('pass1234', gen_salt('bf', 12)), true);
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES ('Bob', 'Smith', 'Bob.Smith', crypt('pass1234', gen_salt('bf', 12)), true);

-- Trainers
INSERT INTO trainers (user_id, specialization_id) VALUES (1, 1);
INSERT INTO trainers (user_id, specialization_id) VALUES (2, 2);

-- Users (trainees)
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES ('Carol', 'White', 'Carol.White', crypt('pass1234', gen_salt('bf', 12)), true);
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES ('David', 'Brown', 'David.Brown', crypt('pass1234', gen_salt('bf', 12)), true);

-- Trainees
INSERT INTO trainees (user_id, date_of_birth, address) VALUES (3, '1995-03-15', '123 Main St');
INSERT INTO trainees (user_id, date_of_birth, address) VALUES (4, '1990-07-22', '456 Oak Ave');

-- Trainee-Trainer assignments
INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES (1, 1);
INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES (2, 2);

-- Trainings
INSERT INTO trainings (trainee_id, trainer_id, name, training_type_id, date, duration) VALUES (1, 1, 'Morning Fitness', 1, '2024-06-01', 60);
INSERT INTO trainings (trainee_id, trainer_id, name, training_type_id, date, duration) VALUES (2, 2, 'Yoga Basics', 2, '2024-06-02', 45);
