TRUNCATE TABLE post2tag CASCADE;
TRUNCATE TABLE tags CASCADE;
TRUNCATE TABLE messages CASCADE;
TRUNCATE TABLE dialogs CASCADE;
TRUNCATE TABLE notifications CASCADE;
TRUNCATE TABLE post_files CASCADE;
TRUNCATE TABLE block_history CASCADE;
TRUNCATE TABLE post_comments CASCADE;
TRUNCATE TABLE posts CASCADE;
TRUNCATE TABLE likes CASCADE;
TRUNCATE TABLE persons CASCADE;
TRUNCATE TABLE currencies CASCADE;
TRUNCATE TABLE captcha CASCADE;
TRUNCATE TABLE person_settings CASCADE;
TRUNCATE TABLE friendships CASCADE;
TRUNCATE TABLE storage CASCADE;
TRUNCATE TABLE cities CASCADE;
TRUNCATE TABLE countries CASCADE;
TRUNCATE TABLE weather CASCADE;

ALTER SEQUENCE persons_id_seq RESTART;