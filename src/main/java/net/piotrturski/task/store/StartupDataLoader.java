package net.piotrturski.task.store;

import java.util.concurrent.ConcurrentMap;

import lombok.experimental.UtilityClass;
import net.piotrturski.task.billing.MovieType;

// sample startup data provider
@UtilityClass
public class StartupDataLoader {

	public void loadData(ConcurrentMap<String, Movie> moviesByTapeId, ConcurrentMap<Movie, MovieType> movieTypes) {
		Movie matrix11 = new Movie();
		Movie spiderMan = new Movie();
		Movie spiderMan2 = new Movie();
		Movie outOfAfrica = new Movie();
		
		moviesByTapeId.put("matrix_01", matrix11);
		moviesByTapeId.put("matrix_02", matrix11);
		moviesByTapeId.put("spider", spiderMan);
		moviesByTapeId.put("spider2", spiderMan2);
		moviesByTapeId.put("africa", outOfAfrica);
		
		movieTypes.put(matrix11, MovieType.PREMIUM);
		movieTypes.put(spiderMan, MovieType.REGULAR);
		movieTypes.put(spiderMan2, MovieType.REGULAR);
		movieTypes.put(outOfAfrica, MovieType.OLD);
	}
	
}
