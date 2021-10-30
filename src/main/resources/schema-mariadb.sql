-- Autogenerated: do not edit this file
CREATE TABLE IF NOT EXISTS BATCH_JOB_INSTANCE  (
    JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
    VERSION BIGINT ,
    JOB_NAME VARCHAR(100) NOT NULL,
    JOB_KEY VARCHAR(32) NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION  (
    JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
    VERSION BIGINT  ,
    JOB_INSTANCE_ID BIGINT NOT NULL,
    CREATE_TIME DATETIME(6) NOT NULL,
    START_TIME DATETIME(6) DEFAULT NULL ,
    END_TIME DATETIME(6) DEFAULT NULL ,
    STATUS VARCHAR(10) ,
    EXIT_CODE VARCHAR(2500) ,
    EXIT_MESSAGE TEXT,
    LAST_UPDATED DATETIME(6),
    JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
      references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ENGINE=InnoDB;
alter table BATCH_JOB_EXECUTION modify EXIT_MESSAGE TEXT;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_PARAMS  (
    JOB_EXECUTION_ID BIGINT NOT NULL ,
    TYPE_CD VARCHAR(6) NOT NULL ,
    KEY_NAME VARCHAR(100) NOT NULL ,
    STRING_VAL VARCHAR(250) ,
    DATE_VAL DATETIME(6) DEFAULT NULL ,
    LONG_VAL BIGINT ,
    DOUBLE_VAL DOUBLE PRECISION ,
    IDENTIFYING CHAR(1) NOT NULL ,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
     references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION  (
    STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
    VERSION BIGINT NOT NULL,
    STEP_NAME VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID BIGINT NOT NULL,
    START_TIME DATETIME(6) NOT NULL ,
    END_TIME DATETIME(6) DEFAULT NULL ,
    STATUS VARCHAR(10) ,
    COMMIT_COUNT BIGINT ,
    READ_COUNT BIGINT ,
    FILTER_COUNT BIGINT ,
    WRITE_COUNT BIGINT ,
    READ_SKIP_COUNT BIGINT ,
    WRITE_SKIP_COUNT BIGINT ,
    PROCESS_SKIP_COUNT BIGINT ,
    ROLLBACK_COUNT BIGINT ,
    EXIT_CODE VARCHAR(2500) ,
    EXIT_MESSAGE VARCHAR(2500) ,
    LAST_UPDATED DATETIME(6),
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
       references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT  (
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT ,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
       references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT  (
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT ,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
      references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ (
    ID BIGINT NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
        constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE=InnoDB;

INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_STEP_EXECUTION_SEQ);

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ (
    ID BIGINT NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
        constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE=InnoDB;

INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_JOB_EXECUTION_SEQ);

CREATE TABLE IF NOT EXISTS BATCH_JOB_SEQ (
    ID BIGINT NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
        constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE=InnoDB;

INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_JOB_SEQ);

create table if not exists book_details (
    isbn varchar(13) not null primary key,
    title varchar(256) not null,
    series_code varchar(32),
    series_isbn varchar(32),
    publisher_code varchar(32) not null,
    publish_date date not null,
    lage_thumbnail_url varchar(128),
    medium_thumbnail_url varchar(128),
    small_thumbnail_url varchar(128),
    description text,
    price double,
    created_at timestamp not null,
    upstream_target boolean not null default false
) engine = InnoDB charset=utf8;
alter table book_details modify title varchar(256) not null;
alter table book_details add column if not exists upstream_target boolean not null default false;
alter table book_details add column if not exists series_isbn varchar(32);
alter table book_details change created_at created_at timestamp not null default current_timestamp;
create index if not exists book_publish_date_index on book_details (publish_date);
create index if not exists book_created_at_index on book_details (created_at desc);
create index if not exists book_series_code_index on book_details (series_code);
create index if not exists book_series_isbn_index on book_details (series_isbn);
alter table book_details drop column price;

create table if not exists book_indexes (
    isbn varchar(13) not null,
    title varchar(128) not null,
    odr int not null,

    foreign key (isbn) references book_details(isbn)
) engine = InnoDB charset = utf8;

create table if not exists book_detail_divisions (
    isbn varchar(13) not null,
    division_code varchar(32) not null,

    foreign key (isbn) references book_details(isbn)
) engine = InnoDB charset=utf8;

create table if not exists book_detail_authors (
    isbn varchar(13) not null,
    author varchar(128) not null,

    foreign key (isbn) references book_details(isbn)
) engine = InnoDB charset=utf8;
alter table book_detail_authors modify author varchar(128) not null;

create table if not exists book_detail_keywords (
    isbn varchar(13) not null,
    keyword varchar(32) not null,

    foreign key (isbn) references book_details(isbn)
) engine = InnoDB charset=utf8;

create table if not exists book_detail_originals (
    isbn varchar(13) not null,
    property varchar(32) not null,
    mapping_type varchar(32) not null,
    value varchar(1024),

    foreign key (isbn) references book_details(isbn)
) engine = InnoDB charset=utf8;

create table if not exists divisions (
    division_code varchar(32) not null primary key,
    depth integer not null,
    name varchar(32)
) engine = InnoDB charset=utf8;
alter table divisions add column if not exists name varchar(32);

create table if not exists division_raw_mappings (
    division_code varchar(32) not null,
    raw varchar(32) not null,
    mapping_type varchar(32) not null,

    foreign key (division_code) references divisions(division_code)
) engine = InnoDB charset=utf8;

create table if not exists publishers (
    publisher_code varchar(32) not null primary key,
    name varchar(32)
) engine = InnoDB charset=utf8;
alter table publishers add column if not exists name varchar(32);

create table if not exists publisher_raw_mappings (
    publisher_code varchar(32) not null,
    raw varchar(64) not null,
    mapping_type varchar(32) not null,

    foreign key (publisher_code) references publishers(publisher_code)
) engine = InnoDB charset=utf8;
alter table publisher_raw_mappings modify raw varchar(64) not null;

create table if not exists publisher_keyword_mappings (
    publisher_code varchar(32) not null,
    keyword varchar(32) not null,
    mapping_type varchar(32) not null,

    foreign key (publisher_code) references publishers(publisher_code)
) engine = InnoDB charset=utf8;

create table if not exists book_original_filters (
    id varchar(32) not null primary key,
    name varchar(32) not null,
    mapping_type varchar(32) not null,
    is_root boolean not null,
    operator_type varchar(32),
    property_name varchar(32),
    regex varchar(128),
    parent_id varchar(32),

    foreign key (parent_id) references book_original_filters(id)
) engine = InnoDB charset=utf8;

create table if not exists job_scheduler_reservations (
    id bigint not null primary key auto_increment,
    name varchar(32),
    `from` date not null,
    `to` date not null,
    created_at datetime not null,
    status varchar(32) not null
) engine = InnoDB charset=utf8;

create table if not exists job_scheduler_results (
    reservation_id bigint not null,
    job_instance_id bigint not null,

    primary key (reservation_id, job_instance_id),
    constraint job_scheduler_result_reservation_id foreign key (reservation_id) references job_scheduler_reservations (id),
    constraint job_scheduler_result_instance_id foreign key (job_instance_id) references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
) engine = InnoDB charset=utf8;

create table if not exists book_upstream_failed_logs (
    sequence bigint not null primary key auto_increment,
    isbn varchar(13) not null,
    created_at timestamp not null,

    foreign key (isbn) references book_details(isbn)
) engine = InnoDB charset=utf8;
alter table book_upstream_failed_logs change created_at created_at timestamp not null default current_timestamp;

create table if not exists book_upstream_failed_reasons (
    failed_id bigint not null,
    property varchar(64) not null,
    message varchar(128) not null,

    foreign key (failed_id) references book_upstream_failed_logs(sequence)
) engine = InnoDB charset=utf8;