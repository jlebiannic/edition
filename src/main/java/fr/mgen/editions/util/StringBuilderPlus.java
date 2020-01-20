package fr.mgen.editions.util;

public class StringBuilderPlus {
	private java.lang.StringBuilder sb;

	public StringBuilderPlus() {
		sb = new java.lang.StringBuilder();
    }

	public StringBuilderPlus appendLine(String str) {
		sb.append(str).append(System.getProperty("line.separator"));
		return this;
	}

	public StringBuilderPlus append(String str) {
		sb.append(str != null ? str : "");
		return this;
	}


	@Override
	public String toString() {
		return sb.toString();
	}
}
