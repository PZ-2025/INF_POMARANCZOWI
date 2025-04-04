create table authors (id bigint not null, biography varchar(2000), first_name varchar(255), last_name varchar(255), primary key (id)) engine=InnoDB;
create table authors_seq (next_val bigint) engine=InnoDB;
insert into authors_seq values ( 1 );
create table book_authors (author_id bigint not null, book_id bigint not null) engine=InnoDB;
create table books (created_at datetime(6), id bigint not null, publisher_id bigint, updated_at datetime(6), cover_image varchar(255), description varchar(255), genre varchar(255), title varchar(255), status enum ('AVAILABLE','BORROWED','LOST','RESERVED'), primary key (id)) engine=InnoDB;
create table books_seq (next_val bigint) engine=InnoDB;
insert into books_seq values ( 1 );
create table event_publication (completion_date datetime(6), publication_date datetime(6), id binary(16) not null, event_type varchar(255), listener_id varchar(255), serialized_event varchar(255), primary key (id)) engine=InnoDB;
create table loans (book_id bigint not null, borrowed_at datetime(6), due_date datetime(6), id bigint not null, lending_librarian_id bigint, returned_at datetime(6), returning_librarian_id bigint, updated_at datetime(6), user_id bigint not null, notes varchar(255), status enum ('ACTIVE','LOST','OVERDUE','RETURNED'), primary key (id)) engine=InnoDB;
create table loans_seq (next_val bigint) engine=InnoDB;
insert into loans_seq values ( 1 );
create table notifications (created_at datetime(6), id bigint not null, updated_at datetime(6), user_id bigint not null, body varchar(255), title varchar(255), notification_status enum ('CREATED','READ','SENT'), notification_type enum ('ERROR','INFO','REMINDER','WARNING'), primary key (id)) engine=InnoDB;
create table notifications_seq (next_val bigint) engine=InnoDB;
insert into notifications_seq values ( 1 );
create table publishers (id bigint not null, description varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table publishers_seq (next_val bigint) engine=InnoDB;
insert into publishers_seq values ( 1 );
create table reservations (queue_position integer not null, book_id bigint not null, expires_at datetime(6), id bigint not null, reserved_at datetime(6), updated_at datetime(6), user_id bigint not null, status enum ('CANCELLED','COMPLETED','EXPIRED','PENDING','READY'), primary key (id)) engine=InnoDB;
create table reservations_seq (next_val bigint) engine=InnoDB;
insert into reservations_seq values ( 1 );
create table user_settings_seq (next_val bigint) engine=InnoDB;
insert into user_settings_seq values ( 1 );
create table users (blocked bit not null, locked bit not null, verified bit not null, blocked_at datetime(6), changed_password_at datetime(6), created_at datetime(6), id bigint not null, locked_at datetime(6), updated_at datetime(6), user_settings bigint, verified_at datetime(6), email varchar(255), first_name varchar(255), last_name varchar(255), password varchar(255), user_type enum ('ADMIN','LIBRARIAN','READER'), primary key (id)) engine=InnoDB;
create table users_seq (next_val bigint) engine=InnoDB;
insert into users_seq values ( 1 );
create table user_settings (dark_mode bit not null, email_notifications_enabled bit not null, email_reminders_enabled bit not null, new_books_notifications_enabled bit not null, notifications_enabled bit not null, id bigint not null, primary key (id)) engine=InnoDB;
alter table users add constraint UK6y1l5ijwp9ho34tkofoanm04n unique (user_settings);
alter table book_authors add constraint FKo86065vktj3hy1m7syr9cn7va foreign key (author_id) references authors (id);
alter table book_authors add constraint FKbhqtkv2cndf10uhtknaqbyo0a foreign key (book_id) references books (id);
alter table books add constraint FKayy5edfrqnegqj3882nce6qo8 foreign key (publisher_id) references publishers (id);
alter table loans add constraint FKokwvlrv6o4i4h3le3bwhe6kie foreign key (book_id) references books (id);
alter table loans add constraint FKlnwo8xfptp66rt9cawvsm5d35 foreign key (lending_librarian_id) references users (id);
alter table loans add constraint FKo0l5wrb6fneai60e5nebsny8k foreign key (returning_librarian_id) references users (id);
alter table loans add constraint FK6xxlcjc0rqtn5nq28vjnx5t9d foreign key (user_id) references users (id);
alter table reservations add constraint FKrsdd3ib3landfpmgoolccjakt foreign key (book_id) references books (id);
alter table reservations add constraint FKb5g9io5h54iwl2inkno50ppln foreign key (user_id) references users (id);
alter table users add constraint FKoyu22rg9veuhq9gsi2syk89x1 foreign key (user_settings) references user_settings (id);
