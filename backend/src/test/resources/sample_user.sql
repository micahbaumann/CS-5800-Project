-- INSERT INTO app_db.users (user_id, username, name) VALUES ('550e8400-e29b-41d4-a716-446655440000', 'testuser', 'Johnny Appleseed');
-- INSERT INTO app_db.chef (chef_id, user_id, price, listing_name)
-- VALUES (
--            '111e4567-e89b-12d3-a456-426614174000',  -- chef_id (UUID)
--            '550e8400-e29b-41d4-a716-446655440000',  -- user_id (must already exist in users)
--            29.99,
--            'Johnny’s Gourmet Tacos'
--        );

-- Before: INSERT INTO app_db.users ...
INSERT INTO users (user_id, username, name)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'testuser', 'Johnny Appleseed');

INSERT INTO chef (chef_id, user_id, price, listing_name)
VALUES ('111e4567-e89b-12d3-a456-426614174000',
        '550e8400-e29b-41d4-a716-446655440000',
        29.99,
        'Johnny’s Gourmet Tacos');