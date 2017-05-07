package com.charlesbot.slack;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.brsanthu.dataexporter.DataExporter;
import com.brsanthu.dataexporter.model.Row;
import com.brsanthu.dataexporter.model.StringColumn;
import com.brsanthu.dataexporter.output.texttable.TextTableExporter;
import com.google.common.base.Preconditions;

public class TableUtils {

	private static final int MAX_MESSAGE_LENGTH = 4000;
	private static final String BLOCK_QUOTE_STRING = "```";
	private static final int MAX_TABLE_LENGTH = MAX_MESSAGE_LENGTH - (BLOCK_QUOTE_STRING.length() * 2);
	
	private DataExporter exporter;
	private StringWriter stringWriter;
	private int rowLength;
	private boolean columnsAdded;
	private boolean rowsAdded;
	
	public TableUtils() {
		stringWriter = new StringWriter();
		exporter = new TextTableExporter(stringWriter);
	}
	
	public TableUtils addColumns(StringColumn[] columns) {
		Preconditions.checkArgument(columnsAdded == false);
		exporter.addColumns(columns);
		int numberOfColumnSeparators = columns.length + 1;
		int numberOfNewLines = 1;
		rowLength = numberOfColumnSeparators + Arrays.stream(columns).mapToInt(c -> c.getWidth()).sum() + numberOfNewLines;
		columnsAdded = true;
		return this;
	}
	
	public TableUtils addBeanRows(List<?> rows) {
		Preconditions.checkArgument(rowsAdded == false);
		exporter.addBeanRows(rows);
		rowsAdded = true;
		return this;
	}
	
	public TableUtils addRows(List<Row> rows) {
		Preconditions.checkArgument(rowsAdded == false);
		exporter.addRows(rows.toArray(new Row[rows.size()]));
		rowsAdded = true;
		return this;
	}
	
	public List<String> buildTableOutput() {
		Preconditions.checkArgument(columnsAdded);
		Preconditions.checkArgument(rowsAdded);
		exporter.finishExporting();
		
		String table = stringWriter.toString();

		List<String> outputs = new ArrayList<>();
		for (String splitTable : splitTable(table, rowLength)) {
			StringBuilder sb = new StringBuilder();
			sb.append("```");
			sb.append(splitTable);
			sb.append("```");
			outputs.add(sb.toString());
		}
		return outputs;
	}

	private static List<String> splitTable(String table, int rowLength) {
		List<String> tables = new ArrayList<>();
		if (table.length() > MAX_TABLE_LENGTH) {
			int numberOfRows = Math.floorDiv(MAX_TABLE_LENGTH, rowLength);
			int splitIndex = numberOfRows * rowLength - 1;
			String splitTable = table.substring(0, splitIndex);
			tables.add(splitTable);
			tables.addAll(splitTable(table.substring(splitIndex), rowLength));
		} else {
			tables.add(table);
		}
		return tables;
	}
}
