package com.leo.androidutils;

/**
 * A container object which may or may not contain a {@code int} value.
 * If a value is present, {@code isPresent()} will return {@code true} and {@code getAsInt()} will return the value.<br>
 *
 * <b>Note:</b> This is a partial copy of {@link java.util.OptionalInt}, since that class wasn't added until API 29.<br>
 *
 * Created by Leo40Git on 16/02/2020.
 */
public final class OptionalInt {
	/**
	 * Common instance for {@code empty()}.
	 */
	private static final OptionalInt EMPTY = new OptionalInt();
	/**
	 * If true then the value is present, otherwise indicates no value is present
	 */
	private final boolean isPresent;
	private final int value;
	/**
	 * Construct an empty instance.
	 */
	private OptionalInt() {
		this.isPresent = false;
		this.value = 0;
	}
	/**
	 * Returns an empty {@code OptionalInt} instance.  No value is present for this
	 * OptionalInt.
	 *
	 *  @return an empty {@code OptionalInt}
	 */
	public static OptionalInt empty() {
		return EMPTY;
	}
	/**
	 * Construct an instance with the value present.
	 *
	 * @param value the int value to be present
	 */
	private OptionalInt(int value) {
		this.isPresent = true;
		this.value = value;
	}
	/**
	 * Return an {@code OptionalInt} with the specified value present.
	 *
	 * @param value the value to be present
	 * @return an {@code OptionalInt} with the value present
	 */
	public static OptionalInt of(int value) {
		return new OptionalInt(value);
	}
	/**
	 * If a value is present in this {@code OptionalInt}, returns the value,
	 * otherwise throws {@code NoSuchElementException}.
	 *
	 * @return the value held by this {@code OptionalInt}
	 * @throws RuntimeException if there is no value present
	 *
	 * @see OptionalInt#isPresent()
	 */
	public int getAsInt() {
		if (!isPresent) {
			throw new RuntimeException("No value present");
		}
		return value;
	}
	/**
	 * Return {@code true} if there is a value present, otherwise {@code false}.
	 *
	 * @return {@code true} if there is a value present, otherwise {@code false}
	 */
	public boolean isPresent() {
		return isPresent;
	}
}
