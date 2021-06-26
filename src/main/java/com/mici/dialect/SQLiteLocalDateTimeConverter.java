package com.mici.dialect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;

public class SQLiteLocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

	@Override
	public String convertToDatabaseColumn(LocalDateTime date) {
		return date.format(DateTimeFormatter.ISO_DATE_TIME);
	}

	@Override
	public LocalDateTime convertToEntityAttribute(String dbDate) {
		return LocalDateTime.parse(dbDate);
	}

}
