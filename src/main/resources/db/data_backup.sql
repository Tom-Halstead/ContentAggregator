INSERT INTO news_source (name, api_type, url, category)
VALUES 
('Tech News API', 'LocalNewsAPI', 'https://api.technews.com', 'technology'),
('World News Network', 'Reddit', 'https://www.reddit.com/r/worldnews', 'world');
INSERT INTO user_news_source (user_id, news_source_id, custom_parameters)
VALUES 
(1, 1, '{"language": "en", "country": "US"}'),  -- John Doe subscribes to Tech News API with custom parameters
(2, 2, '{"limit": 10, "sort": "top"}');         -- Jane Smith subscribes to World News Network with custom parameters
