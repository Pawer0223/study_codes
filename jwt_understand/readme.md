# 참조
- [MySQL 설치 참조 1](https://junghyungil.tistory.com/201)
- [MySQL 설치 참조 2](https://poiemaweb.com/docker-mysql)
- [MySQL 생활코딩](https://www.opentutorials.org/course/2136/12020)

# M1 Mac에서 MySQL 설치 with Docker

``` bash
# image 받기
$ docker pull --platform linux/amd64 mysql:latest

# container 생성
$ docker run --platform linux/amd64 --name mysql-container -e MYSQL_ROOT_PASSWORD=0000 -d -p 3306:3306 mysql:latest

# MySQL Docker 컨테이너 중지
$ docker stop mysql-container

# MySQL Docker 컨테이너 시작
$ docker start mysql-container

# MySQL Docker 컨테이너 재시작
$ docker restart mysql-container

# MySQL Docker 컨테이너 접속
$ docker exec -it mysql-container bash

```

# Database 및 유저 생성

``` bash

# 컨테이너 접속 후, mysql 접속
root@a8e67d74549b:/# mysql -u root -p0000

# 계정생성 -> 권한부여 -> 데이터베이스 생성
mysql> create user 'test'@'%' identified by '0000';
mysql> GRANT ALL PRIVILEGES ON *.* TO 'test'@'%';
mysql> create database test;
mysql> use test;

```
