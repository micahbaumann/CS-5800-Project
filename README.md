# CS 5800 Project

## Start
```
docker compose up --build
```

## Shutdown
```
docker compose down
```

## Rebuild Backend (Spring Boot)
```
docker compose build backend
docker compose up -d --no-deps backend
```