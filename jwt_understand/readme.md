# 참조
- [MySQL 설치 참조 1](https://junghyungil.tistory.com/201)
- [MySQL 설치 참조 2](https://poiemaweb.com/docker-mysql)

# M1 Mac에서 MySQL 설치 with Docker

``` docker

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

# MySQL-Container에 접속하여 mysql 실행
root@a8e67d74549b:/# mysql -u root -p0000


```
