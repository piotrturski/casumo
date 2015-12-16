package net.piotrturski.task.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Set;

import com.google.common.base.MoreObjects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.piotrturski.task.api.Order;
import net.piotrturski.task.billing.MovieType;

@Service
@AllArgsConstructor(onConstructor= @__(@Autowired))
public class RentalService {
	
	private final Clock clock;
	private final RentalRepository rentalRepository;
	
	public void rent(Set<Order> orders, String customerId) {
		
		final LocalDate today = LocalDate.now(clock);
		
		orders.stream().forEach(order -> 
					rentalRepository.getRentalsByTapeId().put(order.getTapeId(), new Rental(order, today)));
		
		int bonus = orders.stream()
							.map(o -> rentalRepository.toMovieType(o.getTapeId()))
							.mapToInt(MovieType::bonusForRental)
							.sum();
		
		rentalRepository.getBonusPerClient().compute(customerId, 
				(custId, currentBonus) -> MoreObjects.firstNonNull(currentBonus, 0) + bonus);
	}
	
	public void returnTapes(Set<String> tapeIds) {
		tapeIds.forEach(rentalRepository.getRentalsByTapeId()::remove);
	}

	public int checkBonus(String userId) {
		return rentalRepository.getBonusPerClient().getOrDefault(userId, 0);
	}
	
}
