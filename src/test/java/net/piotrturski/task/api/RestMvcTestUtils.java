package net.piotrturski.task.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

public class RestMvcTestUtils {

	public static MockHttpServletRequestBuilder postJson(String path, String requestBody) {
		return MockMvcRequestBuilders.post(path)
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(requestBody));
	}
	
	public static MockHttpServletRequestBuilder getJson(String path) {
		return MockMvcRequestBuilders.get(path)
				.contentType(APPLICATION_JSON_UTF8);
	}
	
	/**
	 * strict json comparison
	 */
	public static ResultMatcher jsonContent(String json) {
		return content().string(sameJsonAs(json));
	}

	public static SameJSONAs<? super String> sameJsonAs(String json) {
		return sameJSONAs(toJson(json));
	}
	
	public static ResultMatcher unorderedJsonContent(String json) {
		return content().string(sameJsonAs(json).allowingAnyArrayOrdering());
	}
	
	public static String toJson(String input) {
		return input.replace('\'', '"');
	}
}
