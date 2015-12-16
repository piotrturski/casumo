package net.piotrturski.task.api;

import static net.piotrturski.task.api.RestMvcTestUtils.getJson;
import static net.piotrturski.task.api.RestMvcTestUtils.jsonContent;
import static net.piotrturski.task.api.RestMvcTestUtils.postJson;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.monitoring.runtime.instrumentation.common.com.google.common.collect.Sets;
import com.googlecode.zohhak.api.Coercion;

import net.piotrturski.task.api.FacadeController;
import net.piotrturski.task.api.Order;
import net.piotrturski.task.billing.BillingService;
import net.piotrturski.task.rental.RentalService;

public class FacadeControllerTest {
	
	BillingService billingService = mock(BillingService.class);
	RentalService rentalService = mock(RentalService.class);
	

	@Test
	public void should_parse_input_and_serialize_result_of_surcharge() throws Exception {
		
		when(billingService.calculateSurcharge(Sets.newHashSet("a", "bc"))).thenReturn(BigDecimal.TEN);
		
		mvc().perform(getJson("/surcharge?tapeIds=a,bc"))
		
		.andExpect(status().isOk())
		.andExpect(jsonContent("10"));
	}
	
	@Test
	public void should_parse_input_and_serialize_result_of_rental_price() throws Exception {
		
		Set<Order> orders = Sets.newHashSet(new Order("a", 7), new Order("b", 2));
		when(billingService.calculateRentalPrice(orders)).thenReturn(BigDecimal.TEN);
		
		mvc().perform(postJson("/rental-price", "[{'tapeId':'a', 'declaredDays':7}, {'tapeId':'b', 'declaredDays':2}]"))
		
		.andExpect(status().isOk())
		.andExpect(jsonContent("10"));
	}

	
	@Test
	public void should_parse_input_of_rent() throws Exception {
		
		Set<Order> orders = Sets.newHashSet(new Order("a", 7), new Order("b", 2));
		
		mvc().perform(postJson("/rent/some-id", 
				"[{'tapeId':'a', 'declaredDays':7}, {'tapeId':'b', 'declaredDays':2}]"))
		
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(rentalService).rent(orders, "some-id");
	}
	
	@Test
	public void should_parse_input_of_return_tapes() throws Exception {
		
		mvc().perform(postJson("/return-tapes?tapeIds=a,bc", ""))
		
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(rentalService).returnTapes(Sets.newHashSet("a", "bc"));;
	}

	@Test
	public void should_return_error_msg_on_malformed_input() throws Exception {
		
		mvc().perform(postJson("/rent/some-id", 
				"asda"))
		
		.andExpect(status().isBadRequest())
		.andExpect(content().string(""));
	}
	
	/**
	 * Creates a MockMvc that is backed by the provided backend 
	 */
	private MockMvc mvc() {
		FacadeController view = new FacadeController(rentalService, billingService);
		return MockMvcBuilders.standaloneSetup(view)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.alwaysExpect(forwardedUrl(null))
				//.alwaysExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
				//.alwaysExpect(content().encoding(UTF_8))
				.build();
	}
	
	@Coercion
	public String toJson(String input) {
		return RestMvcTestUtils.toJson(input);
	}
	
}
