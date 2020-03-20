package mops.termine2.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeManagerTest {
	
	
	@Test
	public void sortList() {
		List<LocalDateTime> input = new ArrayList<>(
			Arrays.asList(
				LocalDateTime.of(9, 1, 1, 1, 1),
				LocalDateTime.of(1, 1, 1, 1, 1),
				LocalDateTime.of(7, 1, 1, 1, 1),
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(2, 1, 1, 1, 1)
			)
		);
		
		List<LocalDateTime> expected = new ArrayList<>(
			Arrays.asList(
				LocalDateTime.of(1, 1, 1, 1, 1),
				LocalDateTime.of(2, 1, 1, 1, 1),
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(7, 1, 1, 1, 1),
				LocalDateTime.of(9, 1, 1, 1, 1)
			)
		);
		
		LocalDateTimeManager.sortTermine(input);
		
		assertThat(input).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202003201414() {
		LocalDateTime test = LocalDateTime.of(2020, 3, 20, 14, 14);
		String expected = "Fr. 20.03.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012201414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 20, 14, 14);
		String expected = "Fr. 20.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
}
