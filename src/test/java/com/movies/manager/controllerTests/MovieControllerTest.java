package com.movies.manager.controllerTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.movies.manager.controllers.MovieController;
import com.movies.manager.entities.Movie;
import com.movies.manager.services.MovieService;

@SpringBootTest
public class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @Test
    public void testFindMovies() {
        List<List<Movie>> groupedMovies = Arrays.asList(Arrays.asList(new Movie(), new Movie()));
        when(movieService.getAllGrouped()).thenReturn(groupedMovies);

        ResponseEntity<List<List<Movie>>> response = movieController.findMovies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(groupedMovies, response.getBody());
    }

    @Test
    public void testFindMoviesByYear() {
        int year = 2022;
        List<Movie> movies = Arrays.asList(new Movie(), new Movie());
        when(movieService.getMoviesByYear(year)).thenReturn(movies);

        ResponseEntity<List<Movie>> response = movieController.findMoviesByYear(year);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movies, response.getBody());
    }

    @Test
    public void testMovieDetailsById() throws NotFoundException {
        long id = 1L;
        Movie movie = new Movie();
        when(movieService.getMovieById(id)).thenReturn(movie);

        ResponseEntity<Movie> response = movieController.movieDetailsById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movie, response.getBody());
    }

    @Test
    public void testVoteForMovie() throws NotFoundException {
        // Given
        long id = 1L;
        int ranking = 5;
        String username = "testUser";

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(movieService.hasUserVotedForMovie(id, username)).thenReturn(false);

        ResponseEntity<Void> response = movieController.voteForMovie(id, ranking, authentication);

        verify(movieService, times(1)).voteForMovie(id, ranking, username);
    }

    @Test
    public void testVoteForMovieAlreadyVoted() throws NotFoundException {
        // Given
        long id = 1L;
        int ranking = 5;
        String username = "testUser";

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(movieService.hasUserVotedForMovie(id, username)).thenReturn(true);

        // When
        ResponseEntity<Void> response = movieController.voteForMovie(id, ranking, authentication);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User has already voted for this movie.", response.getHeaders().get("Error").get(0));
        verify(movieService, never()).voteForMovie(id, ranking, username);
    }

    @Test
    public void testVoteForMovieUnauthorized() throws NotFoundException {
        // Given
        long id = 1L;
        int ranking = 5;
        String username = "testUser";

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        ResponseEntity<Void> response = movieController.voteForMovie(id, ranking, authentication);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User must be authenticated to vote.", response.getHeaders().get("Error").get(0));
        verify(movieService, never()).voteForMovie(id, ranking, username);
    }
}