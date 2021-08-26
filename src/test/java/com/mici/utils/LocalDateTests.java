package com.mici.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

class LocalDateTests {

	@Test
	void test() {
		LocalDate quintaFeira = LocalDate.parse("2021-08-26");
		assertEquals(4, quintaFeira.getDayOfWeek().getValue());
	}
	
	@Test
	void deveRetornarRangeDeDatasDeDomingoAteQuinta() {
		LocalDate quintaFeira = LocalDate.parse("2021-08-26");
		int dayOfWeek = quintaFeira.getDayOfWeek().getValue();
		LocalDate domingo = quintaFeira.minusDays(dayOfWeek);
		assertEquals(7, domingo.getDayOfWeek().getValue());
		assertEquals(4, quintaFeira.getDayOfWeek().getValue());
	}
	
	@Test
	void deveRetornarRangeDeDatasDeDomingoAteSabado() {
		LocalDate sabado = LocalDate.parse("2021-08-21");
		var dayOfWeek = sabado.getDayOfWeek().getValue();
		var domingo = sabado.minusDays(dayOfWeek);
		assertEquals(7, domingo.getDayOfWeek().getValue());
		assertEquals("2021-08-15", domingo.format(DateTimeFormatter.ISO_DATE));
		assertEquals(6, sabado.getDayOfWeek().getValue());
		
	}

}
