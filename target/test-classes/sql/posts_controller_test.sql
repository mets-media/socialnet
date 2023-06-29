delete from friendships;
delete from block_history;
delete from likes;
delete from notifications;
delete from messages;
delete from dialogs;
delete from post_comments;
delete from post2tag;
delete from post_files;
delete from posts;
delete from persons;

truncate table posts cascade;
truncate table persons cascade;

ALTER SEQUENCE persons_id_seq RESTART;
UPDATE persons SET id = DEFAULT;

ALTER SEQUENCE posts_id_seq RESTART;
UPDATE posts SET id = DEFAULT;

insert into persons (about,birth_date,change_password_token,configuration_code,deleted_time,email,first_name,is_approved,is_blocked,is_deleted,last_name,last_online_time,message_permissions,notifications_session_id,online_status,password,phone,photo,reg_date,city,country,telegram_id,person_settings_id) values ('About user','1972-11-14 21:25:19','xfolip091','1','2022-04-15 00:43:45','user1@email.com','Leon',true,false,false,'Kennedy','2022-07-21 14:45:29','adipiscing','ipsum','accumsan','$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG','966-998-0544','go86atavdxhcvcagbv','2000-07-26 16:21:43','Bourg-en-Bresse','France',93,633);
insert into persons (about,birth_date,change_password_token,configuration_code,deleted_time,email,first_name,is_approved,is_blocked,is_deleted,last_name,last_online_time,message_permissions,notifications_session_id,online_status,password,phone,photo,reg_date,city,country,telegram_id,person_settings_id) values ('About user','1972-11-14 21:25:19','xfolip091','1','2022-04-15 00:43:45','user2@email.com','Leon',true,false,false,'Kennedy','2022-07-21 14:45:29','adipiscing','ipsum','accumsan','$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG','966-998-0544','go86atavdxhcvcagbv','2000-07-26 16:21:43','Bourg-en-Bresse','France',93,633);
insert into persons (about,birth_date,change_password_token,configuration_code,deleted_time,email,first_name,is_approved,is_blocked,is_deleted,last_name,last_online_time,message_permissions,notifications_session_id,online_status,password,phone,photo,reg_date,city,country,telegram_id,person_settings_id) values ('About user','1972-11-14 21:25:19','xfolip091','1','2022-04-15 00:43:45','user3@email.com','NotLeon',true,false,false,'Kennedy','2022-07-21 14:45:29','adipiscing','ipsum','accumsan','$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG','966-998-0544','go86atavdxhcvcagbv','2000-07-26 16:21:43','Bourg-en-Bresse','France',93,633);
insert into persons (about,birth_date,change_password_token,configuration_code,deleted_time,email,first_name,is_approved,is_blocked,is_deleted,last_name,last_online_time,message_permissions,notifications_session_id,online_status,password,phone,photo,reg_date,city,country,telegram_id,person_settings_id) values ('About user','1972-11-14 21:25:19','xfolip091','1','2022-04-15 00:43:45','user4@email.com','Leon',true,false,false,'NotKennedy','2022-07-21 14:45:29','adipiscing','ipsum','accumsan','$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG','966-998-0544','go86atavdxhcvcagbv','2000-07-26 16:21:43','Bourg-en-Bresse','France',93,633);
insert into persons (about,birth_date,change_password_token,configuration_code,deleted_time,email,first_name,is_approved,is_blocked,is_deleted,last_name,last_online_time,message_permissions,notifications_session_id,online_status,password,phone,photo,reg_date,city,country,telegram_id,person_settings_id) values ('About user','1972-11-14 21:25:19','xfolip091','1','2022-04-15 00:43:45','user5@email.com','FirstName',true,false,false,'LastName','2022-07-21 14:45:29','adipiscing','ipsum','accumsan','$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG','966-998-0544','go86atavdxhcvcagbv','2000-07-26 16:21:43','Bourg-en-Bresse','France',93,633);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #1',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #2',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #3',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #4',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #5',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #6',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #7',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #8',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #9',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-25 03:42:13','2023-02-13 07:13:20','Title #10',1);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-26 03:42:13','2023-02-13 07:13:20','Title #11',2);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-26 03:42:13','2023-02-13 07:13:20','Title #12',2);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-27 03:42:13','2023-02-13 07:13:20','Title #13',3);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-27 03:42:13','2023-02-13 07:13:20','Title #14',3);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-28 03:42:13','2023-02-13 07:13:20','Title #15',4);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Some text','2022-05-28 03:42:13','2023-02-13 07:13:20','Title #16',4);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,false,'Can I search this fucking post by this text?','2022-05-29 03:42:13','2023-02-13 07:13:20','Title #17',5);
insert into posts (is_blocked,is_deleted,post_text,time,time_delete,title,author_id) values (false,true,'Can I search this fucking post by this text?','2022-05-29 03:42:13','2023-02-13 07:13:20','Title #18',5);
