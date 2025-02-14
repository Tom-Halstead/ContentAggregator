-- Drop existing tables if they exist already
DROP TABLE IF EXISTS "user", news_source, user_news_source;

-- Create the users table
CREATE TABLE "user" (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE,
    cognito_uuid VARCHAR(255) UNIQUE NOT NULL
);

-- Create the news_source table
CREATE TABLE news_source (
    news_source_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    api_type VARCHAR(100),  -- Type of API like 'Reddit', 'LocalNewsAPI', etc.
    url VARCHAR(255),       -- Base URL or an identifier for API calls
    category VARCHAR(100)   -- Optional categorization like 'world', 'technology', etc.
);

-- Create the user_news_source table
CREATE TABLE user_news_source (
    user_id INT NOT NULL,
    news_source_id INT NOT NULL,
    custom_parameters JSONB, -- Storing JSON data for API-specific parameters
    added_on TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, news_source_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE,
    FOREIGN KEY (news_source_id) REFERENCES news_source(news_source_id) ON DELETE CASCADE
);
