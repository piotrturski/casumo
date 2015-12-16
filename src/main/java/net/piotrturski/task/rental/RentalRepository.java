package net.piotrturski.task.rental;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.piotrturski.task.billing.MovieType;
import net.piotrturski.task.store.Movie;
import net.piotrturski.task.store.StartupDataLoader;


/**
 * quick & dirty repository 
 *
 */
@Service
@Getter @AllArgsConstructor
public class RentalRepository {
	
	@NonNull private final ConcurrentMap<String, Movie> moviesByTapeId;
	@NonNull private final ConcurrentMap<Movie, MovieType> movieTypes;
	@NonNull private final ConcurrentMap<String, Integer> bonusPerClient;
	@NonNull private final ConcurrentMap<String, Rental> rentalsByTapeId;

	// starting 'database' with sample content
	public RentalRepository() {
		this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
		StartupDataLoader.loadData(moviesByTapeId, movieTypes);
	}

	public MovieType toMovieType(String tapeId) {
		Movie movie = getMoviesByTapeId().get(tapeId);
		Preconditions.checkArgument(movie != null, "tape: '%s' doesn't exist", tapeId);
		return getMovieTypes().get(movie);
	}
	
	
}