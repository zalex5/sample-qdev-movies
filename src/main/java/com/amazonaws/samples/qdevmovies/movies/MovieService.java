package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! This method helps ye search for cinematic treasures in our vast collection.
     * Chart a course through our movie database using various search criteria.
     * 
     * @param name The name of the treasure ye be seekin' (case-insensitive partial match)
     * @param id The specific treasure ID ye want to find
     * @param genre The genre of adventures ye be interested in (case-insensitive partial match)
     * @return A list of movie treasures that match yer search criteria, arrr!
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Starting treasure hunt with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> searchResults = new ArrayList<>(movies);
        
        // Filter by movie name if provided, arrr!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            searchResults = searchResults.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
            logger.debug("Filtered by name '{}', found {} treasures", name, searchResults.size());
        }
        
        // Filter by movie ID if provided, me hearty!
        if (id != null && id > 0) {
            searchResults = searchResults.stream()
                .filter(movie -> movie.getId() == id)
                .collect(Collectors.toList());
            logger.debug("Filtered by ID '{}', found {} treasures", id, searchResults.size());
        }
        
        // Filter by genre if provided, batten down the hatches!
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            searchResults = searchResults.stream()
                .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                .collect(Collectors.toList());
            logger.debug("Filtered by genre '{}', found {} treasures", genre, searchResults.size());
        }
        
        logger.info("Treasure hunt complete! Found {} cinematic treasures matching yer criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Arrr! Get all available movie genres from our treasure chest.
     * This helps ye know what kinds of adventures await!
     * 
     * @return A list of unique genres available in our collection
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
