package de.hdu.pvs.crashfinder;

import com.google.common.base.Joiner;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Dominik Fay Created in October 2015.
 */
public class CrashFinderSeedStatementHelper {
	private final List<String> lines;

	public CrashFinderSeedStatementHelper(List<? extends String> seedStatementLines) {
		this.lines = Collections.unmodifiableList(seedStatementLines);
	}

	public String seedAsString() {
		if (!checkLines()) {
			throw new RuntimeException("Lines from seed statement file are "
					+ "not valid.");
		}
		return Joiner.on(":").join(lines);
	}

	public boolean checkLines() {
		if (lines.size() != 2) {
			return false;
		}
		String seedClass = lines.get(0);
		String lineNumberString = lines.get(1);
		if (seedClass.endsWith(".") || seedClass.contains("..")) {
			return false;
		}
		try {
			int lineNumber = Integer.parseInt(lineNumberString);
			if (lineNumber < 1) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
