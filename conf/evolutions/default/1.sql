# --- Created by Ebean DDL
# ---To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table battery (
  id                            bigserial not null,
  turn_off                      boolean default false not null,
  charge                        integer,
  status                        varchar(8),
  constraint ck_battery_status check ( status in ('HIGH','MEDIUM','LOW','VERY_LOW','FULL')),
  constraint pk_battery primary key (id)
);

create table device_config (
  id                            bigserial not null,
  patient_first_name            varchar(255),
  patient_last_name             varchar(255),
  patient_gender                varchar(255),
  patient_dob                   date,
  patient_weight                integer,
  patient_height                integer,
  device_id                     varchar(255),
  patient_id                    varchar(255),
  basal_daily_max               integer,
  basal_hourly                  integer,
  bolus_max                     integer,
  last_update                   timestamptz,
  constraint pk_device_config primary key (id)
);

create table patient (
  id                            bigserial not null,
  email                         varchar(255) not null,
  user_uuid                     varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  sha_password                  varchar(64) not null,
  last_update                   timestamptz,
  constraint uq_patient_email unique (email),
  constraint pk_patient primary key (id)
);

create table sensor_data (
  id                            bigserial not null,
  device_id                     bigint,
  patient_id                    bigint,
  blood_sugar_level             integer,
  recording_time_stamp          timestamptz,
  constraint pk_sensor_data primary key (id)
);

create table user_data (
  id                            bigserial not null,
  email                         varchar(255) not null,
  user_type                     varchar(255),
  user_uuid                     varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  sha_password                  varchar(64) not null,
  last_update                   timestamptz,
  constraint uq_user_data_email unique (email),
  constraint pk_user_data primary key (id)
);


# --- !Downs

drop table if exists battery cascade;

drop table if exists device_config cascade;

drop table if exists patient cascade;

drop table if exists sensor_data cascade;

drop table if exists user_data cascade;

