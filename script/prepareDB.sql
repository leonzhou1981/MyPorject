create tablespace gitlog
datafile 'C:\app\oracle\oradata\ORCL12C\TABLESPACE\gitlog.dbf'
size 20480m
extent management local segment space management auto;

create temporary tablespace gitlogtemp
tempfile 'C:\app\oracle\oradata\ORCL12C\TABLESPACE\gitlogtemp.dbf'
size 2048m
extent management local uniform size 50m;

create tablespace gitlogindex
datafile 'C:\app\oracle\oradata\ORCL12C\TABLESPACE\gitlogindex.dbf'
size 20480m
extent management local segment space management auto;

create user gitlog identified by gitlog
  default tablespace gitlog
  temporary tablespace gitlogtemp;

grant connect, resource, exp_full_database, imp_full_database to gitlog;
grant dba to gitlog;
grant unlimited tablespace to gitlog;
grant select any table to gitlog;
grant select any dictionary to gitlog;

create sequence gitlogseq
  minvalue 1
  maxvalue 9999999999999999999999999999
  start with 1
  increment by 1
  cache 20;

create table gitlog
(
  reponame             VARCHAR2(200) not null,
  branch               VARCHAR2(200) not null,
  commitid             VARCHAR2(200) not null,
  batchid              VARCHAR2(200) not null,
  packdate             DATE
)
tablespace gitlog;

alter table gitlog
  add constraint gitlog_pk primary key (reponame, branch, commitid, batchid)
  using index tablespace gitlogindex;
