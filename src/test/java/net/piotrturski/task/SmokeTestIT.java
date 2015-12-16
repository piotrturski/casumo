package net.piotrturski.task;

import static com.jayway.restassured.RestAssured.given;
import static net.piotrturski.task.api.RestMvcTestUtils.toJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.Clock;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.googlecode.zohhak.api.runners.ZohhakRunner;

@RunWith(ZohhakRunner.class)
@SpringApplicationConfiguration(classes=Application.class)
@WebIntegrationTest("server.port:0")
public class SmokeTestIT {

	public static class Configuration {
		
		@Bean
		public Clock clock() {
			return Clock.systemDefaultZone();
		}
	}
	
	@ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	@Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();
	
	@Value("${local.server.port}")
    int port;
	
	@Test
	public void should_return_found_matches() {
		
		
		given()
			.contentType(APPLICATION_JSON_UTF8_VALUE)
			.body(toJson("[{'tapeId':'matrix_02', 'declaredDays':2}]"))
		.when()
			.post("http://localhost:"+port+"/rental-price")
		.then()
			.contentType(APPLICATION_JSON_UTF8_VALUE)
			.content(is("80"))
			.statusCode(200);
	}
	
}
