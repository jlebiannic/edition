package fr.mgen.editions.util;

public class StringBuilderPlus {
	public static final String LINE_SEP = System.getProperty("line.separator");
	private java.lang.StringBuilder sb;

	public StringBuilderPlus() {
		sb = new java.lang.StringBuilder();
    }

	public StringBuilderPlus appendLine(String str) {
		sb.append(str).append(LINE_SEP);
		return this;
	}

	public StringBuilderPlus appendLine() {
		sb.append(LINE_SEP);
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

}
