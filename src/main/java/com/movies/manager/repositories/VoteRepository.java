package com.movies.manager.repositories;

import org.springframework.data.repository.CrudRepository;

import com.movies.manager.entities.Vote;

public interface VoteRepository extends CrudRepository<Vote, Long> {

	boolean existsByMovieIdAndUsername(long movieId, String username);

}
