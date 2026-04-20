-- Create the database 

DROP TABLE IF EXISTS student;

-- Create the student table
CREATE TABLE student (
    id         SERIAL PRIMARY KEY,        -- auto-incremented unique ID
    first_name VARCHAR(100) NOT NULL,     -- student first name
    last_name  VARCHAR(100) NOT NULL,     -- student last name
    age        INT          NOT NULL CHECK (age > 0 AND age <= 120),
    grade      DOUBLE PRECISION NOT NULL CHECK (grade >= 0 AND grade <= 20)
);


-- Insert sample data for testing
INSERT INTO student (first_name, last_name, age, grade) VALUES
    ('Justin',   'Martin',   20, 15.5),
    ('Bob',     'Leponge',   22, 11.0),
    ('Charlie', 'Bernard',  19, 17.25),
    ('Morgan',   'Picard',    21, 8.5),
    ('Ethan',   'Moreau',   23, 13.0),
    ('Fatima',  'Nguyen',   20, 16.75),
    ('Alicia', 'Cordial',    24, 9.0),
    ('Hannah',  'Laurent',  19, 18.0),
    ('Ivan',    'Michel',   22, 12.5),
    ('Julia',   'Garcia',   21, 14.0);

-- Verify the result
SELECT * FROM student ORDER BY last_name, first_name;