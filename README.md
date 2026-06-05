# Collective Calendar

## Stack

- Backend
	- **Spring Boot**
	- **Spring Data**
	- **Spring Security**
	- **Lombok**
- Frontend
	- **Thymeleaf**
- Database
	- **PostgreSQL** 17
	- **Flyway** for migrations
- Devops
	- **Docker**

## Instructions

### Docker

- Build and start (force recompile) `docker compose up -d --build`
- Start application `docker compose up -d`
- Stop application `docker compose down`
- Wipe database `docker compose down -v`

The `-d` flag detaches the terminal.
If you want to keep docker open in the terminal, remove this flag.
