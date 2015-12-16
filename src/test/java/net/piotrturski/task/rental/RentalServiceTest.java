package net.piotrturski.task.rental;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.monitoring.runtime.instrumentation.common.com.google.common.collect.Sets;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;

import net.piotrturski.task.api.Order;
import net.piotrturski.task.rental.RentalRepository;
import net.piotrturski.task.rental.RentalService;

@RunWith(ZohhakRunner.class)
public class RentalServiceTest {

	private static final String A_USER_ID = "user1";
	RentalRepository repository = SampleRepositoryFactory.makeStandardRepo();
	RentalService rentalService = new RentalService(Clock.systemDefaultZone(), repository);
	private Set<Order> orders = Sets.newHashSet(new Order("spider", 1), new Order("matrix_01", 1));

	@Before
	public void rent_movies() {
		rentalService.rent(orders, A_USER_ID);
		
	}
	
	@Test
	public void should_remove_track_rentals() {
		assertThat(repository.getRentalsByTapeId()).containsOnlyKeys("spider", "matrix_01");
	}
	
	@Test
	public void should_stop_track_rentals_on_return() {
		rentalService.returnTapes(Sets.newHashSet("matrix_01"));
		
		assertThat(repository.getRentalsByTapeId()).containsOnlyKeys("spider");
	}
	
	@TestWith({
		A_USER_ID + ", 3",
		"non-existent-user-id, 0 ",
	})
	public void should_track_customer_bonus(String userId, int expectedBonus) {
		
		assertThat(rentalService.checkBonus(userId)).isEqualTo(expectedBonus);
	}
	
}
