# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

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
  device_id                     varchar(255),
  patient_id                    varchar(255),
  device_mode                   varchar(255),
  battery_level                 float,
  insulin_level                 float,
  glucagon_level                float,
  daily_max                     float,
  basal_hourly                  float,
  target_bgl                    float,
  bolus_max                     float,
  last_update                   timestamptz,
  constraint pk_device_config primary key (id)
);

create table doctor_profiles (
  id                            bigserial not null,
  email                         varchar(255),
  doctor_id                     varchar(255),
  password                      varchar(255),
  gender                        varchar(255),
  mobile_number                 varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  last_update                   timestamptz,
  constraint pk_doctor_profiles primary key (id)
);

create table patient (
  id                            bigserial not null,
  device_id                     varchar(255),
  patient_id                    varchar(255),
  patient_first_name            varchar(255),
  patient_last_name             varchar(255),
  patient_gender                varchar(255),
  patient_age                   integer,
  patient_weight                float,
  patient_height                float,
  blood_volume                  float,
  glucose_sensitivity           float,
  email_id                      varchar(255),
  mobile_number                 varchar(255),
  last_update                   timestamptz,
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

create table simulation_log (
  id                            bigserial not null,
  simulation_id                 varchar(255),
  device_id                     varchar(255),
  patient_id                    varchar(255),
  current_bgl                   float,
  current_insulin               float,
  constraint pk_simulation_log primary key (id)
);


# --- !Downs

drop table if exists battery cascade;

drop table if exists device_config cascade;

drop table if exists doctor_profiles cascade;

drop table if exists patient cascade;

drop table if exists sensor_data cascade;

drop table if exists simulation_log cascade;

