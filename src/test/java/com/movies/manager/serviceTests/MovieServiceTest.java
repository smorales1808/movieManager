package com.movies.manager.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.movies.manager.entities.Movie;
import com.movies.manager.entities.Vote;
import com.movies.manager.repositories.MovieRepository;
import com.movies.manager.repositories.VoteRepository;
import com.movies.manager.services.MovieService;

@SpringBootTest
public class MovieServiceTest {
	@Mock
	private MovieRepository movieRepository;

	@Mock
	private VoteRepository voteRepository;

	@InjectMocks
	private MovieService movieService;

	@Test
	public void testGetAllGrouped() {
		// Given
		List<Movie> movies = Arrays.asList(new Movie(1L, "Movie 1", 2022), new Movie(2L, "Movie 2", 2022),
				new Movie(3L, "Movie 3", 2023));

		when(movieRepository.getGroupedMovies()).thenReturn(movies);

		// When
		List<List<Movie>> groupedMovies = movieService.getAllGrouped();

		// Then
		assertEquals(2, groupedMovies.size());
		assertEquals(2, groupedMovies.get(0).size());
		assertEquals(1, groupedMovies.get(1).size());
	}

	@Test
	public void testGetMoviesByYear() {
		// Given
		int year = 2022;
		List<Movie> movies = Arrays.asList(new Movie(1L, "Movie 1", year), new Movie(2L, "Movie 2", year));

		when(movieRepository.findByYear(year)).thenReturn(movies);

		// When
		List<Movie> moviesByYear = movieService.getMoviesByYear(year);

		// Then
		assertEquals(2, moviesByYear.size());
	}

	@Test
	public void testGetMovieById() throws NotFoundException {
		// Given
		long id = 1L;
		Movie movie = new Movie(id, "Movie 1", 2022);

		when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

		// When
		Movie retrievedMovie = movieService.getMovieById(id);

		// Then
		assertEquals(movie, retrievedMovie);
	}

	@Test
	public void testVoteForMovie() throws NotFoundException {
		// Given
		long id = 1L;
		int ranking = 5;
		String username = "testUser";
		Movie movie = new Movie(id, "Movie 1", 2022);

		when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

		// When
		movieService.voteForMovie(id, ranking, username);

		// Then
		assertEquals(5, movie.getRanking());
		verify(movieRepository, times(1)).save(movie);
		verify(voteRepository, times(1)).save(any(Vote.class));
	}

	@Test
	public void testHasUserVotedForMovie() {
		// Given
		long movieId = 1L;
		String username = "testUser";

		when(voteRepository.existsByMovieIdAndUsername(movieId, username)).thenReturn(true);

		// When
		boolean hasVoted = movieService.hasUserVotedForMovie(movieId, username);

		// Then
		assertTrue(hasVoted);
	}

	@Test
	public void testGetAllGroupedWhenNoMovies() {
		// Given
		when(movieRepository.getGroupedMovies()).thenReturn(Arrays.asList());

		// When
		List<List<Movie>> groupedMovies = movieService.getAllGrouped();

		// Then
		assertEquals(0, groupedMovies.size());
	}

	@Test
	public void testGetMoviesByYearWhenNoMovies() {
		// Given
		int year = 2022;
		when(movieRepository.findByYear(year)).thenReturn(Arrays.asList());

		// When
		List<Movie> moviesByYear = movieService.getMoviesByYear(year);

		// Then
		assertEquals(0, moviesByYear.size());
	}

	@Test
	public void testGetMovieByIdNotFound() {
		// Given
		long id = 1L;
		when(movieRepository.findById(id)).thenReturn(Optional.empty());

		// Then
		assertThrows(NotFoundException.class, () -> movieService.getMovieById(id));
	}

	@Test
	public void testVoteForMovieNotFound() {
		// Given
		long id = 1L;
		int ranking = 5;
		String username = "testUser";

		when(movieRepository.findById(id)).thenReturn(Optional.empty());

		// Then
		assertThrows(NotFoundException.class, () -> movieService.voteForMovie(id, ranking, username));
	}

	@Test
	public void testHasUserVotedForMovieFalse() {
		// Given
		long movieId = 1L;
		String username = "testUser";

		when(voteRepository.existsByMovieIdAndUsername(movieId, username)).thenReturn(false);

		// When
		boolean hasVoted = movieService.hasUserVotedForMovie(movieId, username);

		// Then
		assertFalse(hasVoted);
	}

}
