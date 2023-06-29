INSERT INTO persons (email, first_name, last_name, is_approved, is_blocked, is_deleted)
VALUES ('user1@email.com', 'User', 'Testovich', true, false, false);

INSERT INTO persons (email, first_name, last_name, is_approved, is_blocked, is_deleted)
VALUES ('user2@email.com', 'Iam', 'Deleted', true, true, true);

INSERT INTO posts (title, post_text, author_id, is_blocked, is_deleted, time, time_delete)
VALUES ('Post title #1', 'Deleted', 1, false, true, NOW(), NOW());

INSERT INTO posts (title, post_text, author_id, is_blocked, is_deleted, time, time_delete)
VALUES ('Post title #2', 'Это просто текст поста', 1, false, false, NOW() - INTERVAL '1 day', NULL);

INSERT INTO posts (title, post_text, author_id, is_blocked, is_deleted, time, time_delete)
VALUES ('Post title #3', 'ъъъ ёёё', 1, false, false, NOW(), NULL);

INSERT INTO posts (title, post_text, author_id, is_blocked, is_deleted, time, time_delete)
VALUES ('Post title #4', 'Автор поста удалён', 2, false, false, NOW(), NULL);

INSERT INTO post_comments (comment_text, is_blocked, is_deleted, time, parent_id, author_id, post_id)
VALUES ('Комментарий к посту #3', false, false, NOW(), NULL, 2, 3);

INSERT INTO post_comments (comment_text, is_blocked, is_deleted, time, parent_id, author_id, post_id)
VALUES ('Комментарий к комментарию поста #3', false, false, NOW(), 1, 1, 3);

INSERT INTO tags (tag) VALUES ('post3tag');
INSERT INTO post2tag (post_id, tag_id) VALUES (3, 1);

INSERT INTO likes (type, entity_id, time, person_id)
VALUES ('Post', 2, NOW(), 1);
