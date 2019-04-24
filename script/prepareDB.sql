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
size 2048m
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
  unid                 Number(18) not null,
  reponame             VARCHAR2(200),
  branch               VARCHAR2(200),
  commitid             VARCHAR2(200),
  parentcommitid       VARCHAR2(200),
  author               VARCHAR2(200),
  authdate             DATE,
  committer            VARCHAR2(200),
  commitdate           DATE,
  bugno                VARCHAR2(100),
  remark               VARCHAR2(4000)
)
tablespace gitlog;

alter table gitlog
  add constraint gitlog_pk primary key (unid)
  using index tablespace gitlogindex;

create table gitlogdtl
(
  gitlogunid           Number(18) not null,
  changetype           VARCHAR2(20),
  oldpath              VARCHAR2(1000),
  newpath              VARCHAR2(1000)
)
tablespace gitlog;

alter table gitlogdtl
  add constraint gitlogdtl_pk primary key (gitlogunid, changetype, oldpath, newpath)
  using index tablespace gitlogindex;