- mysql container실행 명령어
```
docker run \
--name test-mysql \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=1234 \
-d mysql:8.3.0 \
--character-set-server=utf8mb4 \
--collation-server=utf8mb4_unicode_ci
```


- 게시글(post) save 요청 명령어
```
curl -X POST http://localhost:8080/test/posts \
     -H "Content-Type: application/json" \
     -d '{"memberId": 1, "contents": "Hello, World!"}'
```

- 게시글(post) 조회 by memberId && date
```
curl -X GET "http://localhost:8080/test/posts/daily-post-counts?memberId=1&firstDate=2024-07-01&lastDate=2024-07-31"
```
