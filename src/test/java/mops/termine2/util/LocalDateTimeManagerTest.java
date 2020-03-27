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
	public void toStringTest202003211414() {
		LocalDateTime test = LocalDateTime.of(2020, 3, 21, 14, 14);
		String expected = "Sa. 21.03.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012201414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 20, 14, 14);
		String expected = "So. 20.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012211414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 21, 14, 14);
		String expected = "Mo. 21.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012221414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 22, 14, 14);
		String expected = "Di. 22.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012231414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 23, 14, 14);
		String expected = "Mi. 23.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012241414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 24, 14, 14);
		String expected = "Do. 24.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTest202012251414() {
		LocalDateTime test = LocalDateTime.of(2020, 12, 25, 14, 14);
		String expected = "Fr. 25.12.2020 14:14";
		
		String result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void toStringTestList() {
		LocalDateTime ldt1 = LocalDateTime.of(2020, 3, 21, 14, 14);
		String expected1 = "Sa. 21.03.2020 14:14";
		LocalDateTime ldt2 = LocalDateTime.of(2020, 12, 25, 14, 14);
		String expected2 = "Fr. 25.12.2020 14:14";
		List<LocalDateTime> test = Arrays.asList(ldt1, ldt2);
		List<String> expected = Arrays.asList(expected1, expected2);
		
		List<String> result = LocalDateTimeManager.toString(test);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void testVergangen1() {
		LocalDateTime ldt = LocalDateTime.now().minusDays(1);		
		assertThat(LocalDateTimeManager.istVergangen(ldt)).isEqualTo(true);
	}
	
	@Test
	public void testVergangen2() {
		LocalDateTime ldt = LocalDateTime.now().plusDays(1);		
		assertThat(LocalDateTimeManager.istVergangen(ldt)).isEqualTo(false);
	}
	
	@Test
	public void testZukuenftig1() {
		LocalDateTime ldt = LocalDateTime.now().minusDays(1);		
		assertThat(LocalDateTimeManager.istZukuenftig(ldt)).isEqualTo(false);
	}
	
	@Test
	public void testZukuenftig2() {
		LocalDateTime ldt = LocalDateTime.now().plusDays(1);		
		assertThat(LocalDateTimeManager.istZukuenftig(ldt)).isEqualTo(true);
	}
	
	@Test
	public void fruehestesDatum() {
		List<LocalDateTime> input = new ArrayList<>(
			Arrays.asList(
				LocalDateTime.of(9, 1, 1, 1, 1),
				LocalDateTime.of(1, 1, 1, 1, 1),
				LocalDateTime.of(7, 1, 1, 1, 1),
				null,
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(2, 1, 1, 1, 1)
			)
		);
		
		LocalDateTime expected = LocalDateTime.of(1, 1, 1, 1, 1);
		
		LocalDateTime result = LocalDateTimeManager.bekommeFruehestesDatum(input);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void fruehestesDatumNull() {
		List<LocalDateTime> input = null;
		
		LocalDateTime result = LocalDateTimeManager.bekommeFruehestesDatum(input);
		
		assertThat(result).isNull();
	}
	
	@Test
	public void fruehestesDatumLeer() {
		List<LocalDateTime> input = new ArrayList<>();
		
		LocalDateTime result = LocalDateTimeManager.bekommeFruehestesDatum(input);
		
		assertThat(result).isNull();
	}
	
	@Test
	public void spaetestesDatum() {
		List<LocalDateTime> input = new ArrayList<>(
			Arrays.asList(
				LocalDateTime.of(1, 1, 1, 1, 1),
				LocalDateTime.of(7, 1, 1, 1, 1),
				LocalDateTime.of(9, 1, 1, 1, 1),
				null,
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(2, 1, 1, 1, 1)
			)
		);
		
		LocalDateTime expected = LocalDateTime.of(9, 1, 1, 1, 1);
		
		LocalDateTime result = LocalDateTimeManager.bekommeSpaetestesDatum(input);
		
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void spaetestesDatumNull() {
		List<LocalDateTime> input = null;
		
		LocalDateTime result = LocalDateTimeManager.bekommeSpaetestesDatum(input);
		
		assertThat(result).isNull();
	}
	
	@Test
	public void spaetestesDatumLeer() {
		List<LocalDateTime> input = new ArrayList<>();
		
		LocalDateTime result = LocalDateTimeManager.bekommeSpaetestesDatum(input);
		
		assertThat(result).isNull();
	}
	
	@Test
	public void filtereUngueltigeDaten() {
		List<LocalDateTime> input = new ArrayList<>(
			Arrays.asList(
				LocalDateTime.of(1, 1, 1, 1, 1),
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(7, 1, 1, 1, 1),
				LocalDateTime.of(9, 1, 1, 1, 1),
				LocalDateTime.of(9, 1, 1, 1, 1),
				null,
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(2, 1, 1, 1, 1)
			)
		);
		
		List<LocalDateTime> expected = new ArrayList<>(
			Arrays.asList(
				LocalDateTime.of(1, 1, 1, 1, 1),
				LocalDateTime.of(3, 1, 1, 1, 1),
				LocalDateTime.of(7, 1, 1, 1, 1),
				LocalDateTime.of(9, 1, 1, 1, 1),
				LocalDateTime.of(2, 1, 1, 1, 1)
			)
		);
		
		ArrayList<LocalDateTime> result = LocalDateTimeManager.filterUngueltigeDaten(input);
		
		assertThat(result).isEqualTo(expected);
	}
	
}
