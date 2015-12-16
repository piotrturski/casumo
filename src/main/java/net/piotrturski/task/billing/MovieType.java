package net.piotrturski.task.billing;

import java.math.BigDecimal;

import com.google.common.base.Preconditions;

public enum MovieType {

	PREMIUM(1, 40, 2), REGULAR(3, 30, 1), OLD(5, 30, 1);

	private final int initialDays;
	private final int pricePerDay; // it's private & we don't need BigDecimal now
	private final int bonus;
	
	private MovieType(int initialDays, int pricePerDay, int bonus) {
		this.initialDays = initialDays;
		this.pricePerDay = pricePerDay;
		this.bonus = bonus;
	}
	
	public BigDecimal calculatePrice(int numberOfDays) {
		Preconditions.checkArgument(0 < numberOfDays, "Rental time must be positive");
		
		return BigDecimal.valueOf(Math.max(1L + numberOfDays - initialDays, 1) * pricePerDay); // longs to avoid overflow
	}
	
	public BigDecimal calcuateSurcharge(int additionalDays) {
		Preconditions.checkArgument(0 <= additionalDays, "Delay cannot be negative");
		
		return BigDecimal.valueOf( ((long)additionalDays) * pricePerDay); // longs to avoid overflow
	}
	
	public int bonusForRental() {
		return bonus;
	}
	
}
