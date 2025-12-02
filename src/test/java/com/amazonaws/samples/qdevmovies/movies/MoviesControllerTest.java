package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! Unit tests for the MoviesController treasure hunting functionality.
 * These tests ensure our controller endpoints work ship-shape, arrr!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with pirate-themed test data
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "The Pirate's Treasure", "Captain Director", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5),
                    new Movie(2L, "Sea Battle", "Admiral Filmmaker", 2022, "Action", "Epic naval combat", 140, 4.0)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "The Pirate's Treasure", "Captain Director", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>(allMovies);
                
                if (name != null && !name.trim().isEmpty()) {
                    results = results.stream()
                        .filter(movie -> movie.getMovieName().toLowerCase().contains(name.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                if (id != null && id > 0) {
                    results = results.stream()
                        .filter(movie -> movie.getId() == id)
                        .collect(java.util.stream.Collectors.toList());
                }
                
                if (genre != null && !genre.trim().isEmpty()) {
                    results = results.stream()
                        .filter(movie -> movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Adventure", "Action", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view with all movies and genres")
    public void testGetMovies() {
        // When - getting movies page
        String result = moviesController.getMovies(model);
        
        // Then - should return movies view with data
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("genres");
        
        assertEquals(2, movies.size());
        assertEquals(3, genres.size());
    }

    @Test
    @DisplayName("Should return movie details for valid ID")
    public void testGetMovieDetails() {
        // When - getting movie details for valid ID
        String result = moviesController.getMovieDetails(1L, model);
        
        // Then - should return movie-details view
        assertNotNull(result);
        assertEquals("movie-details", result);
        assertTrue(model.containsAttribute("movie"));
    }

    @Test
    @DisplayName("Should return error view for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        // When - getting movie details for invalid ID
        String result = moviesController.getMovieDetails(999L, model);
        
        // Then - should return error view
        assertNotNull(result);
        assertEquals("error", result);
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
    }

    // Ahoy! New tests for the treasure hunt search endpoint

    @Test
    @DisplayName("Should return all movies when no search parameters provided")
    public void testSearchMovies_NoParameters_ReturnsAllTreasures() {
        // When - searching without parameters
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, null);
        
        // Then - should return success with all movies
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(2, movies.size());
        assertEquals(2, body.get("totalResults"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Ahoy!"));
        assertTrue(message.contains("treasures"));
    }

    @Test
    @DisplayName("Should filter movies by name parameter")
    public void testSearchMovies_ByName_ReturnsMatchingTreasures() {
        // When - searching by movie name
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("pirate", null, null);
        
        // Then - should return matching movies
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertTrue(movies.get(0).getMovieName().toLowerCase().contains("pirate"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Found 1 cinematic treasure"));
    }

    @Test
    @DisplayName("Should return bad request for invalid ID parameter")
    public void testSearchMovies_InvalidId_ReturnsBadRequest() {
        // When - searching with invalid ID
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, -1L, null);
        
        // Then - should return bad request with pirate error message
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("invalid"));
        assertTrue(message.contains("matey"));
        
        assertEquals(0, body.get("totalResults"));
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(2, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }
}
