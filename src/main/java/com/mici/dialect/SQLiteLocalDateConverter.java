package com.mici.dialect;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SQLiteLocalDateConverter implements AttributeConverter<LocalDate, String>{

	@Override
	public String convertToDatabaseColumn(LocalDate date) {
		return date.format(DateTimeFormatter.ISO_DATE);
	}

	@Override
	public LocalDate convertToEntityAttribute(String dbDate) {
		return LocalDate.parse(dbDate);
	}

}
