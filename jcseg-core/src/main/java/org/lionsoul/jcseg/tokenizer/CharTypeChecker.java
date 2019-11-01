package org.lionsoul.jcseg.tokenizer;

@FunctionalInterface
public interface CharTypeChecker {
	public boolean is(int c);
}
