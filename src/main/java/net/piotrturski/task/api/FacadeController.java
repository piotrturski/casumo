package net.piotrturski.task.api;

import static com.netflix.hystrix.exception.HystrixRuntimeException.FailureType.COMMAND_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.exception.HystrixRuntimeException;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.piotrturski.task.billing.BillingService;
import net.piotrturski.task.rental.RentalService;

@Slf4j
@RestController
@RequestMapping(produces=APPLICATION_JSON_UTF8_VALUE)
@AllArgsConstructor(onConstructor=@__(@Autowired))
public class FacadeController {

	@NonNull private final RentalService rentalService;
	@NonNull private final BillingService billingService;
	
	@SuppressWarnings("serial") private static class Orders extends HashSet<Order>{};

	@RequestMapping(value="/rent/{customerId}", method=RequestMethod.POST)
	public void rent(@RequestBody Orders orders, @PathVariable String customerId) {
		rentalService.rent(orders, customerId);
	}
	
	@RequestMapping(value="/return-tapes", method=RequestMethod.POST)
	public void returnTapes(@RequestParam Set<String> tapeIds) {
		rentalService.returnTapes(tapeIds);
	}
	
	@RequestMapping(value="/rental-price", method=RequestMethod.POST)
	public BigDecimal calculateRentalPrice(@RequestBody Orders orders) {
		return billingService.calculateRentalPrice(orders);
	}
	
	@RequestMapping(value="/surcharge", method=RequestMethod.GET)
	public BigDecimal calculateSurcharge(@RequestParam Set<String> tapeIds) {
		return billingService.calculateSurcharge(tapeIds);
	}
	
	@ExceptionHandler
	@ResponseStatus(BAD_REQUEST)
	public void parsingError(HttpMessageNotReadableException e) {}
	
	@ExceptionHandler
	public String hystrixError(HttpServletResponse response, HystrixRuntimeException e) {
		if (e.getFailureType() == COMMAND_EXCEPTION) {
			log.error("error inside hystrix command", e);
			response.setStatus(INTERNAL_SERVER_ERROR.value());
			return "Internal error";
		}
		response.setStatus(SERVICE_UNAVAILABLE.value());
		return "System is too busy. Try again later";
	}
	
}
