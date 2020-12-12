package com.charlesbot;

import java.util.Set;
import org.springframework.core.convert.converter.Converter;

public class Converters {

	private Set<Converter<?,?>> converters;

	public Set<Converter<?,?>> getConverters() {
		return converters;
	}

	public void setConverters(Set<Converter<?,?>> converters) {
		this.converters = converters;
	}
}
