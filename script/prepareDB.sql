create tablespace gitlog
datafile 'C:\app\oracle\oradata\ORCL12C\TABLESPACE\gitlog.dbf'
size 20480m
extent management local segment space management auto;

create temporary tablespace gitlogtemp
tempfile 'C:\app\oracle\oradata\ORCL12C\TABLESPACE\gitlogtemp.dbf'
size 2048m
extent management local uniform size 50m;

create user gitlog identified by gitlog
default tablespace gitlog
temporary tablespace gitlogtemp;

grant connect, resource, exp_full_database, imp_full_database to gitlog;
grant dba to gitlog;
grant unlimited tablespace to gitlog;
grant select any table to gitlog;
grant select any dictionary to gitlog;

create table GITLOG
(
  reponame             VARCHAR2(200) not null,
  branch               VARCHAR2(200) not null,
  commitid             VARCHAR2(200) not null,
  author               VARCHAR2(200),
  authdate             DATE,
  committer            VARCHAR2(200),
  commitdate           DATE,
  bugno                VARCHAR2(100),
  remark               VARCHAR2(4000)
)
tablespace gitlog;

create table GITLOGDTL
(
  reponame             VARCHAR2(200) not null,
  branch               VARCHAR2(200) not null,
  commitid             VARCHAR2(200) not null,
  sno                  NUMBER(9) not null,
  changetype           VARCHAR2(20),
  path                 VARCHAR2(1000)
)
tablespace gitlog;




