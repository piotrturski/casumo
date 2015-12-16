package net.piotrturski.task.rental;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.piotrturski.task.billing.MovieType;
import net.piotrturski.task.rental.RentalRepository;
import net.piotrturski.task.store.Movie;
import net.piotrturski.task.store.StartupDataLoader;

public class SampleRepositoryFactory {

	public static RentalRepository makeStandardRepo() {

		ConcurrentMap<String, Movie> moviesByTapeId = new ConcurrentHashMap<>();
		ConcurrentMap<Movie, MovieType> movieTypes = new ConcurrentHashMap<>();

		StartupDataLoader.loadData(moviesByTapeId, movieTypes);
		
		return new RentalRepository(moviesByTapeId, movieTypes, 
										new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
	}
	
}
