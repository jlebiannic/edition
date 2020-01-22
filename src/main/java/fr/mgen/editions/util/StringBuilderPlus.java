package fr.mgen.editions.util;

import lombok.Getter;

public class StringBuilderPlus {
	@Getter
	private java.lang.StringBuilder sb;

	public StringBuilderPlus() {
		sb = new java.lang.StringBuilder();
    }

	public StringBuilderPlus appendLine(String str) {
		sb.append(str).append(System.getProperty("line.separator"));
		return this;
	}

	public StringBuilderPlus appendLine() {
		sb.append(System.getProperty("line.separator"));
		return this;
	}

	public StringBuilderPlus append(String str) {
		sb.append(str != null ? str : "");
		return this;
	}

	public StringBuilderPlus append(StringBuilder sbToAppend) {
		sb.append(sbToAppend);
		return this;
	}


	@Override
	public String toString() {
		return sb.toString();
	}

	public int length() {
		return sb.length();
	}
}
