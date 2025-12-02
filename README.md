# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! A swashbuckling movie catalog web application built with Spring Boot, featuring a treasure hunt search system with pirate flair!

## Features

- **Movie Catalog**: Browse 12 classic cinematic treasures with detailed information
- **Treasure Hunt Search**: Advanced search functionality to find movies by name, ID, or genre
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate-themed styling
- **REST API**: JSON endpoints for programmatic access to movie data

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for server-side templating
- **JSON** for data exchange

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List with Search**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search (see API documentation below)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie catalog data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── templates/
│       │   ├── movies.html                   # Main page with search form
│       │   └── movie-details.html            # Movie details page
│       └── static/css/
│           └── movies.css                    # Pirate-themed styling
└── test/                                     # Comprehensive unit tests
```

## API Endpoints

### Get All Movies (HTML)
```
GET /movies
```
Returns an HTML page displaying all movies with a search form and pirate-themed interface.

### Get Movie Details (HTML)
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🔍 Search Movies (JSON API) - NEW!
```
GET /movies/search
```
Ahoy! This endpoint helps ye search for cinematic treasures using various criteria.

**Query Parameters (all optional):**
- `name` (string): Movie name to search for (case-insensitive partial match)
- `id` (long): Specific movie ID to find
- `genre` (string): Genre to filter by (case-insensitive partial match)

**Response Format:**
```json
{
  "success": true,
  "message": "Ahoy! Found 2 cinematic treasures for ye, me hearty!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "totalResults": 1,
  "searchCriteria": {
    "name": "prison",
    "id": "",
    "genre": ""
  }
}
```

**Examples:**

Search by movie name:
```
GET /movies/search?name=prison
```

Search by genre:
```
GET /movies/search?genre=drama
```

Search by specific ID:
```
GET /movies/search?id=1
```

Combine multiple criteria:
```
GET /movies/search?name=family&genre=crime
```

**Error Responses:**

Invalid ID (400 Bad Request):
```json
{
  "success": false,
  "message": "Arrr! That treasure ID be invalid, matey! Must be a positive number.",
  "movies": [],
  "totalResults": 0
}
```

No results found (200 OK):
```json
{
  "success": true,
  "message": "Shiver me timbers! No treasures found matching yer search criteria. Try adjusting yer course, matey!",
  "movies": [],
  "totalResults": 0
}
```

## Search Form Interface

The main movies page now includes a pirate-themed search form with:

- **Movie Name Field**: Search by partial movie title
- **Treasure ID Field**: Find specific movie by ID
- **Adventure Type Dropdown**: Filter by genre
- **Search Buttons**: 
  - 🔍 Start Treasure Hunt! (perform search)
  - 🧹 Clear Course (reset form)
  - 🗺️ Show All Treasures (display all movies)

The interface provides real-time feedback with pirate-themed messages and handles edge cases gracefully.

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

The project includes:
- **MovieServiceTest**: Tests for search functionality and business logic
- **MoviesControllerTest**: Tests for REST endpoints and HTML responses
- **MovieTest**: Tests for data model validation

All tests use pirate-themed test data and verify both functionality and error handling.

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check browser console for JavaScript errors
2. Verify the search endpoint is accessible: `curl http://localhost:8080/movies/search`
3. Check application logs for any service errors

## Contributing

This project demonstrates modern Spring Boot development with search functionality. Feel free to:
- Add more movies to the catalog
- Enhance the search algorithms
- Improve the pirate-themed UI/UX
- Add new search criteria (director, year range, rating)
- Implement advanced filtering options
- Add pagination for large result sets

## Pirate Language Features

The application includes authentic pirate language throughout:
- Search messages use terms like "Ahoy!", "matey", "treasure hunt"
- Error messages include "Arrr!", "Shiver me timbers!"
- Success messages celebrate with "me hearty" and treasure references
- Code comments maintain the pirate theme while staying professional

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye navigate through our cinematic treasures! 🏴‍☠️*
