package com.CineLock.Service;

import com.CineLock.Entity.Movie;
import com.CineLock.Entity.Show;
import com.CineLock.Repository.MovieRepository;
import com.CineLock.Repository.ShowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;

    public ShowService(ShowRepository showRepository,
                       MovieRepository movieRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
    }

    public Show createShow(Show show) {

        Long movieId = show.getMovie().getId();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        show.setMovie(movie);

        return showRepository.save(show);
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }
}