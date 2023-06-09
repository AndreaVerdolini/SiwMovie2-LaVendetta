package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Rewiew;
import it.uniroma3.siw.model.User;

public interface RewiewRepository extends CrudRepository<Rewiew, Long> {
    public boolean existsByUserAndMovie(User user, Movie movie);
}