package net.piotrturski.task.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.runner.RunWith;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;

import net.piotrturski.task.billing.MovieType;

@RunWith(ZohhakRunner.class)
public class MovieTypeTest {

	@TestWith({
		"PREMIUM, 1, 40",
		"PREMIUM, 2, 80",
		"PREMIUM, 5, 200",
		
		"REGULAR, 1, 30",
		"REGULAR, 3, 30",
		"REGULAR, 4, 60",
		"REGULAR, 7, 150",
		
		"OLD, 1, 30",
		"OLD, 5, 30",
		"OLD, 6, 60",
		"OLD, 9, 150",
	})
	public void should_calculate_rental_price(MovieType movieType, int declaredNumberOfDays, BigDecimal expectedPrice) {
		
		assertThat(movieType.calculatePrice(declaredNumberOfDays)).isEqualTo(expectedPrice);
	}
	
	@TestWith({
		"REGULAR, 0",
		"REGULAR, -1",		
	})
	public void should_reject_wrong_rental_time(MovieType movieType, int declaredNumberOfDays) {
	
		assertThatThrownBy(()-> movieType.calculatePrice(declaredNumberOfDays))
											.isInstanceOf(IllegalArgumentException.class);
	}
	
}
