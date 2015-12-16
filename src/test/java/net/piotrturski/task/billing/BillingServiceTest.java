package net.piotrturski.task.billing;

import static io.codearte.catchexception.shade.mockito.Mockito.when;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Sets;
import com.googlecode.zohhak.api.Coercion;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;

import io.codearte.catchexception.shade.mockito.Mockito;
import net.piotrturski.task.api.Order;
import net.piotrturski.task.billing.BillingService;
import net.piotrturski.task.rental.RentalRepository;
import net.piotrturski.task.rental.RentalService;
import net.piotrturski.task.rental.SampleRepositoryFactory;

@RunWith(ZohhakRunner.class)
public class BillingServiceTest {

	RentalRepository repository = SampleRepositoryFactory.makeStandardRepo();
	Clock clock = Mockito.mock(Clock.class);
	RentalService rentalService = new RentalService(clock, repository);
	BillingService billingService = new BillingService(clock, repository);
	Instant initialTime = Instant.ofEpochMilli(0);
	
	@Before
	public void prepareClock() {
		when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
		when(clock.instant()).thenReturn(initialTime);
	}

	@Test
	public void should_calculate_total_price() {
		
		Set<Order> orders = Sets.newHashSet(new Order("matrix_02", 1),
											new Order("spider", 5),
											new Order("spider2", 2),
											new Order("africa", 7));
		

		assertThat(billingService.calculateRentalPrice(orders)).isEqualTo(BigDecimal.valueOf(250));
	}

	@Test
	public void should_calculate_surcharge() {
		
		Set<Order> orders = Sets.newHashSet(new Order("matrix_02", 10),
											new Order("spider", 11),
											new Order("africa", 30));
		
		rentalService.rent(orders, "some user id");
		when(clock.instant()).thenReturn(initialTime.plus(12, DAYS));

		HashSet<String> returnedLate = Sets.newHashSet("matrix_02", "spider");
		assertThat(billingService.calculateSurcharge(returnedLate)).isEqualTo(BigDecimal.valueOf(110));
	}

	@TestWith({
		"spider",
		"non existent"
	})
	public void should_reject_surcharge_calculation_for_not_rented_tape(String tapeId) {
		
		assertThatThrownBy(() -> billingService.calculateSurcharge(Sets.newHashSet(tapeId)))
		
									.isInstanceOf(IllegalArgumentException.class)
									.hasMessageContaining(tapeId);
	}
	
	@TestWith({
		"20, 21, 1",
		"20, 23, 3",
		"20, 20, 0",
		"20, 19, 0",
	})
	public void should_calculate_dalay(LocalDate payedInclusive, LocalDate today, int expectedDelay) {
		assertThat(billingService.daysOfDelay(payedInclusive, today)).isEqualTo(expectedDelay);
	}
	
	@Coercion
	public LocalDate toDate(String dayNumber) {
		return LocalDate.ofEpochDay(Integer.parseInt(dayNumber));
	}
	
}
