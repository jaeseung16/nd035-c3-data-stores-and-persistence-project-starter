create table if not exists schedule (
    id bigint not null,
    date DATE,
    primary key (id)
);

create table if not exists employee_schedule (
    employee_id bigint not null,
    schedule_id bigint not null
);

create table if not exists pet_schedule (
    pet_id bigint not null,
    schedule_id bigint not null
);

create table if not exists activity_schedule (
    activity_id int not null,
    schedule_id bigint not null
);
