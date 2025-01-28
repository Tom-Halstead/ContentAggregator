INSERT INTO users (user_id, email, created_at, last_login, cognito_username) VALUES
('user1', 'user1@example.com', NOW(), NOW(), 'cognitoUser1'),
('user2', 'user2@example.com', NOW() - INTERVAL '10 days', NOW() - INTERVAL '1 day', 'cognitoUser2'),
('user3', 'user3@example.com', NOW() - INTERVAL '20 days', NOW() - INTERVAL '2 days', 'cognitoUser3');

INSERT INTO news_sources (name, api_type, url, category) VALUES
('Reddit News', 'Reddit', 'https://www.reddit.com/r/news/', 'news'),
('Reddit World', 'Reddit', 'https://www.reddit.com/r/worldnews/', 'world'),
('Tech News API', 'LocalNewsAPI', 'https://api.example.com/news/tech', 'technology'),
('Health News API', 'LocalNewsAPI', 'https://api.example.com/news/health', 'health');

INSERT INTO user_news_sources (user_id, news_source_id, custom_parameters) VALUES
('user1', 1, '{"filter": "hot"}'),
('user1', 3, '{"filter": "latest", "region": "US"}'),
('user2', 2, '{"filter": "top"}'),
('user2', 4, '{"filter": "latest", "region": "EU"}'),
('user3', 1, '{"filter": "new"}'),
('user3', 2, '{"filter": "rising"}');
