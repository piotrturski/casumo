package net.piotrturski.task.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Order {

	String tapeId;
	int declaredDays;
	
}
