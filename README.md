# Library Management System

A Full Stack Library Management System developed using Spring Boot, Hibernate/JPA, PostgreSQL, HTML, CSS, and JavaScript.
--------------------------------------------------

# Technologies Used
## Backend
- Java
- Spring Boot
- Hibernate / JPA
- PostgreSQL
- Maven

## Frontend
- HTML
- CSS
- JavaScript
- React
--------------------------------------------------

# Project Architecture

Controller Layer
        ↓
Service Layer
        ↓
DAO Layer
        ↓
Repository Layer
        ↓
PostgreSQL Database

--------------------------------------------------

# Package Structure

com.pentagon.library_management
│
├── controller
├── dao
├── dto
├── entity
├── repository
├── service

--------------------------------------------------

# Hibernate Relationships Implemented

- OneToOne Mapping
- OneToMany Mapping
- ManyToOne Mapping
- ManyToMany Mapping
- CascadeType.ALL

--------------------------------------------------
# Features
- Add Authors
- Add Books
- Add Categories
- Add Users
- Borrow Books
- Return Books
- User Authentication
- REST API Integration
- Dynamic Frontend UI
- Book Availability Tracking
- Borrow Record Management

--------------------------------------------------
# REST APIs Implemented

## Author APIs
- POST /authors
- GET /authors
- GET /authors/{id}/books

## Book APIs
- POST /books
- GET /books
- PUT /books/{bookId}/borrow/{userId}
- PUT /books/{bookId}/return

## User APIs
- POST /users
- GET /users
- PUT /users
- DELETE /users/{id}

## Category APIs
- POST /categories
- GET /categories

--------------------------------------------------

# Frontend Structure

static
│
├── api.js
├── application.js
├── components.js
├── constants.js
├── main.js
├── styles.css
└── index.html

--------------------------------------------------

# Database

PostgreSQL is used for storing:

- Users
- Authors
- Books
- Categories
- Borrow Records

--------------------------------------------------
