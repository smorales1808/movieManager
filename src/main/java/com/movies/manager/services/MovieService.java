package com.movies.manager.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movies.manager.entities.Movie;
import com.movies.manager.entities.Vote;
import com.movies.manager.repositories.MovieRepository;
import com.movies.manager.repositories.VoteRepository;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private VoteRepository voteRepository;

	public List<List<Movie>> getAllGrouped() {

		return new ArrayList<>(
				movieRepository.getGroupedMovies().stream().collect(Collectors.groupingBy(Movie::getYear)).values());
	}

	public List<Movie> getMoviesByYear(int year) {
		return movieRepository.findByYear(year);
	}

	public Movie getMovieById(Long id) throws NotFoundException {
		Optional<Movie> optionalMovie = movieRepository.findById(id);
		if (optionalMovie.isPresent()) {
			return movieRepository.findById(id).get();
		} else {
			throw new NotFoundException();
		}

	}

	public void voteForMovie(long id, int ranking, String username) throws NotFoundException {
		Optional<Movie> optionalMovie = movieRepository.findById(id);
		if (optionalMovie.isPresent()) {
			Movie movie = optionalMovie.get();
			movie.setRanking(movie.getRanking() + ranking);
			movieRepository.save(movie);
			Vote vote = new Vote(username, movie);
			voteRepository.save(vote);

		} else {
			throw new NotFoundException();
		}
	}

	public boolean hasUserVotedForMovie(long movieId, String username) {
		return voteRepository.existsByMovieIdAndUsername(movieId, username);
	}

}
