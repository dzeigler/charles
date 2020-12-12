package com.charlesbot.slack;

import com.charlesbot.cli.Command;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

public interface CommandConverter<C extends Command> extends Converter<C, List<String>> {

}
