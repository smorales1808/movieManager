package com.movies.manager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.movies.manager.entities.Movie;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {

	@Query("SELECT m FROM Movie AS m  ORDER BY m.ranking DESC")
	public List<Movie> getGroupedMovies();
	
	List<Movie> findByYear(int year);
}
