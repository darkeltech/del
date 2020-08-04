create table if not exists users (
id bigserial primary key,
nick text unique,
password text,
faculty text,
email text,
points int,
subscribed boolean,
additional_info text,
roles text[]
);

create table if not exists descriptions (
version bigserial primary key,
description text,
authors text[]
);

create table if not exists questions (
id bigserial primary key,
asker_id bigint,
theme text,
question text,
price int,
created_at timestamp with time zone,
answerer_id bigint,
amount_of_watches int default 0,
amount_of_comment int default 0
);

alter table questions add foreign key (asker_id) references users(id);
alter table questions add foreign key (answerer_id) references users(id);

create table if not exists comments (
    question_id bigint primary key,
    user_id bigint primary key,
    comment text primary key,
    created_at timestamp primary key
);

alter table comments add foreign key (user_id) references users(id);
alter table comments add foreign key (question_id) references questions(id);


/*
insert into questions(asker_id, theme, question, price, created_at) values(1, 'Fucking good theme', 'Почему р1убашка в клетку?', 10, '2020-04-12 04:05:06+03');
insert into questions(asker_id, theme, question, price, created_at) values(1, 'Fucking2 good theme', 'Почему ру2башка в клетку?', 100, '2020-04-12 05:05:06+03');
insert into questions(asker_id, theme, question, price, created_at) values(1, 'Fucking3 good theme', 'Почему руб3ашка в клетку?', 180, '2020-04-12 23:05:06+03');
insert into questions(asker_id, theme, question, price, created_at) values(1, 'Fucking4 good theme', 'Почему руба4шка в клетку?', 1, '2020-04-12 19:05:06+03');
insert into questions(asker_id, theme, question, price, created_at) values(1, 'Fucking5 good theme', 'Почему рубаш5ка в клетку?', 78, '2020-04-12 04:07:06+03');
insert into questions(asker_id, theme, question, price, created_at) values(1, 'Fucking6 good theme', 'Почему рубашка666 в клетку?', 3, '2020-04-12 04:05:11-08');

*/
