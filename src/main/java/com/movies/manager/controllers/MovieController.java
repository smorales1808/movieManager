package com.movies.manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movies.manager.entities.Movie;
import com.movies.manager.services.MovieService;

@RequestMapping(produces = { "application/json" })
@RestController
public class MovieController {
	@Autowired
	private MovieService movieService;

	@GetMapping("/movies")
	public ResponseEntity<List<List<Movie>>> findMovies() {
		return ResponseEntity.status(HttpStatus.OK).body(movieService.getAllGrouped());

	}

	@GetMapping("/movies/")
	public ResponseEntity<List<Movie>> findMoviesByYear(@RequestParam(value = "year") int year) {
		return ResponseEntity.status(HttpStatus.OK).body(movieService.getMoviesByYear(year));

	}

	@GetMapping("/movies/{id}")
	public ResponseEntity<Movie> movieDetailsById(@PathVariable Long id) throws NotFoundException {
		return ResponseEntity.status(HttpStatus.OK).body(movieService.getMovieById(id));
	}

	@PostMapping("/movies/{id}/vote")
	public ResponseEntity<Void> voteForMovie(@PathVariable("id") long id, @RequestParam("ranking") int ranking,
			Authentication authentication) throws NotFoundException {
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			if (!movieService.hasUserVotedForMovie(id, username)) {
				movieService.voteForMovie(id, ranking, username);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error","User has already voted for this movie.").build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Error","User must be authenticated to vote.").build();
		}
		return null;
	}

}
