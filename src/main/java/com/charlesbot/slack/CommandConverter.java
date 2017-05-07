package com.charlesbot.slack;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.charlesbot.cli.Command;

public interface CommandConverter<C extends Command> extends Converter<C, List<String>> {

}
