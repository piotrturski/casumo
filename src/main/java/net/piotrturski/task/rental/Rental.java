package net.piotrturski.task.rental;

import java.time.LocalDate;

import com.google.common.base.Preconditions;

import net.piotrturski.task.api.Order;

public class Rental {

	private final LocalDate payedInclusive;

	public Rental(Order order, LocalDate today) {
		Preconditions.checkArgument(order.getDeclaredDays() > 0, "Declared days must be positive");
		payedInclusive = today.plusDays(order.getDeclaredDays());
	}

	public LocalDate getPayedInclusive() {
		return payedInclusive;
	}

}
