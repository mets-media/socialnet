INSERT INTO post_comments (comment_text, is_blocked, is_deleted, time, parent_id, author_id, post_id)
VALUES ('Комментарий к посту #3', false, false, NOW(), NULL, 1, 3);

INSERT INTO post_comments (comment_text, is_blocked, is_deleted, time, parent_id, author_id, post_id)
VALUES ('Комментарий к комментарию поста #3', false, false, NOW(), 1, 1, 3);

INSERT INTO post_comments (comment_text, is_blocked, is_deleted, time, parent_id, author_id, post_id)
VALUES ('Комментарий №1 к посту #1', false, false, NOW(), NULL, 2, 1);

INSERT INTO post_comments (comment_text, is_blocked, is_deleted, time, parent_id, author_id, post_id)
VALUES ('Комментарий №2 к посту #1', false, false, NOW(), NULL, 1, 1);