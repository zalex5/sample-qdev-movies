package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! Unit tests for the MovieService treasure hunting functionality.
 * These tests ensure our search methods work ship-shape, arrr!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMovies_NoFilters_ReturnsAllMovies() {
        // When - searching with no criteria
        List<Movie> results = movieService.searchMovies(null, null, null);
        
        // Then - should return all movies
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should filter movies by name (case-insensitive partial match)")
    public void testSearchMovies_ByName_ReturnsMatchingMovies() {
        // When - searching by partial movie name
        List<Movie> results = movieService.searchMovies("prison", null, null);
        
        // Then - should find "The Prison Escape"
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMovieName().toLowerCase().contains("prison"));
    }

    @Test
    @DisplayName("Should filter movies by name with different case")
    public void testSearchMovies_ByNameDifferentCase_ReturnsMatchingMovies() {
        // When - searching with uppercase
        List<Movie> results = movieService.searchMovies("FAMILY", null, null);
        
        // Then - should find "The Family Boss"
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMovieName().toLowerCase().contains("family"));
    }

    @Test
    @DisplayName("Should filter movies by exact ID")
    public void testSearchMovies_ById_ReturnsExactMovie() {
        // When - searching by specific ID
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        // Then - should return exactly one movie with that ID
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Should return empty list for non-existent ID")
    public void testSearchMovies_ByNonExistentId_ReturnsEmpty() {
        // When - searching by non-existent ID
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        
        // Then - should return empty list
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should filter movies by genre (case-insensitive partial match)")
    public void testSearchMovies_ByGenre_ReturnsMatchingMovies() {
        // When - searching by genre
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        
        // Then - should find all drama movies
        assertNotNull(results);
        assertFalse(results.isEmpty());
        results.forEach(movie -> 
            assertTrue(movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Should filter movies by partial genre match")
    public void testSearchMovies_ByPartialGenre_ReturnsMatchingMovies() {
        // When - searching by partial genre (Crime/Drama contains "Crime")
        List<Movie> results = movieService.searchMovies(null, null, "crime");
        
        // Then - should find movies with Crime in genre
        assertNotNull(results);
        assertFalse(results.isEmpty());
        results.forEach(movie -> 
            assertTrue(movie.getGenre().toLowerCase().contains("crime")));
    }

    @Test
    @DisplayName("Should combine multiple search criteria with AND logic")
    public void testSearchMovies_MultipleCriteria_ReturnsIntersection() {
        // When - searching with multiple criteria
        List<Movie> results = movieService.searchMovies("family", null, "crime");
        
        // Then - should find movies matching both name AND genre
        assertNotNull(results);
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertTrue(movie.getMovieName().toLowerCase().contains("family"));
        assertTrue(movie.getGenre().toLowerCase().contains("crime"));
    }

    @Test
    @DisplayName("Should return empty list when no movies match criteria")
    public void testSearchMovies_NoMatches_ReturnsEmpty() {
        // When - searching for non-existent movie
        List<Movie> results = movieService.searchMovies("nonexistent", null, null);
        
        // Then - should return empty list
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty string parameters gracefully")
    public void testSearchMovies_EmptyStrings_ReturnsAllMovies() {
        // When - searching with empty strings
        List<Movie> results = movieService.searchMovies("", null, "");
        
        // Then - should return all movies (empty strings ignored)
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should handle whitespace-only parameters gracefully")
    public void testSearchMovies_WhitespaceOnly_ReturnsAllMovies() {
        // When - searching with whitespace-only strings
        List<Movie> results = movieService.searchMovies("   ", null, "  ");
        
        // Then - should return all movies (whitespace trimmed and ignored)
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should ignore zero or negative ID values")
    public void testSearchMovies_InvalidId_IgnoresId() {
        // When - searching with invalid ID
        List<Movie> results1 = movieService.searchMovies(null, 0L, null);
        List<Movie> results2 = movieService.searchMovies(null, -1L, null);
        
        // Then - should return all movies (invalid ID ignored)
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(movieService.getAllMovies().size(), results1.size());
        assertEquals(movieService.getAllMovies().size(), results2.size());
    }

    @Test
    @DisplayName("Should return all unique genres from movie collection")
    public void testGetAllGenres_ReturnsUniqueGenres() {
        // When - getting all genres
        List<String> genres = movieService.getAllGenres();
        
        // Then - should return unique, sorted genres
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Check for expected genres from test data
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Crime/Drama"));
        assertTrue(genres.contains("Action/Crime"));
        
        // Verify no duplicates
        assertEquals(genres.size(), genres.stream().distinct().count());
    }

    @Test
    @DisplayName("Should return movie by valid ID")
    public void testGetMovieById_ValidId_ReturnsMovie() {
        // When - getting movie by valid ID
        Optional<Movie> result = movieService.getMovieById(1L);
        
        // Then - should return the movie
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for invalid ID")
    public void testGetMovieById_InvalidId_ReturnsEmpty() {
        // When - getting movie by invalid IDs
        Optional<Movie> result1 = movieService.getMovieById(null);
        Optional<Movie> result2 = movieService.getMovieById(0L);
        Optional<Movie> result3 = movieService.getMovieById(-1L);
        Optional<Movie> result4 = movieService.getMovieById(999L);
        
        // Then - should return empty
        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
        assertFalse(result3.isPresent());
        assertFalse(result4.isPresent());
    }

    @Test
    @DisplayName("Should return all movies from collection")
    public void testGetAllMovies_ReturnsAllMovies() {
        // When - getting all movies
        List<Movie> movies = movieService.getAllMovies();
        
        // Then - should return non-empty list
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        
        // Verify we have expected test movies
        assertTrue(movies.size() >= 10); // Should have at least 10 movies from JSON
    }
}