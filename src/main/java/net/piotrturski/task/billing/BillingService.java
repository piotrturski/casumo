package net.piotrturski.task.billing;

import static java.time.temporal.ChronoUnit.DAYS;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import net.piotrturski.task.api.Order;
import net.piotrturski.task.rental.Rental;
import net.piotrturski.task.rental.RentalRepository;

@Service
@AllArgsConstructor(onConstructor= @__(@Autowired))
public class BillingService {

	private final Clock clock;
	private final RentalRepository rentalRepository;

	public BigDecimal calculateRentalPrice(Set<Order> orders) {
		return orders.stream()
					.map(this::priceForOrder)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	private BigDecimal priceForOrder(Order order) {
		return rentalRepository.toMovieType(order.getTapeId()).calculatePrice(order.getDeclaredDays());
	}

	public BigDecimal calculateSurcharge(Set<String> tapeIds) {
		final LocalDate today = LocalDate.now(clock);
		
		return tapeIds.stream()
				.map(tapeId -> surchargeForReturnedTape(tapeId, today))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	
	private BigDecimal surchargeForReturnedTape(String tapeId, LocalDate today) {
		
		Rental rental = rentalRepository.getRentalsByTapeId().get(tapeId);
		Preconditions.checkArgument(rental != null, "tape: '%s' was not rented", tapeId);
		LocalDate payedInclusive = rental.getPayedInclusive();
		int daysOfDelay = daysOfDelay(payedInclusive, today);
		
		MovieType movieType = rentalRepository.toMovieType(tapeId);
		return movieType.calcuateSurcharge(daysOfDelay);
	}
	
	int daysOfDelay(LocalDate payedInclusive, LocalDate today) {
		return (int) Math.max(DAYS.between(payedInclusive, today), 0);
	}
	
}
