package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * Ahoy matey! This endpoint helps ye search for cinematic treasures using various criteria.
     * Send yer search parameters and we'll chart a course through our movie collection!
     * 
     * @param name The name of the movie treasure ye be seekin' (optional)
     * @param id The specific treasure ID ye want to find (optional)  
     * @param genre The genre of adventures ye be interested in (optional)
     * @return A JSON response with matching movie treasures and search metadata, arrr!
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String genre) {
        
        logger.info("Ahoy! Starting treasure hunt with parameters - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate input parameters, me hearty!
            if (id != null && id <= 0) {
                logger.warn("Invalid treasure ID provided: {}", id);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Arrr! That treasure ID be invalid, matey! Must be a positive number.");
                errorResponse.put("movies", List.of());
                errorResponse.put("totalResults", 0);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Search for movie treasures
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Prepare the response with pirate flair, arrr!
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            if (searchResults.isEmpty()) {
                response.put("message", "Shiver me timbers! No treasures found matching yer search criteria. Try adjusting yer course, matey!");
            } else {
                String treasureCount = searchResults.size() == 1 ? "treasure" : "treasures";
                response.put("message", String.format("Ahoy! Found %d cinematic %s for ye, me hearty!", 
                    searchResults.size(), treasureCount));
            }
            
            response.put("movies", searchResults);
            response.put("totalResults", searchResults.size());
            response.put("searchCriteria", Map.of(
                "name", name != null ? name : "",
                "id", id != null ? id : "",
                "genre", genre != null ? genre : ""
            ));
            
            logger.info("Treasure hunt successful! Returning {} results", searchResults.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Blimey! Error during treasure hunt: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Arrr! Something went wrong during the treasure hunt. Try again later, matey!");
            errorResponse.put("movies", List.of());
            errorResponse.put("totalResults", 0);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}