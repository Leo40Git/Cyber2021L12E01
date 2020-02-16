package com.leo.cyber2021l12e01;

import androidx.annotation.NonNull;

/**
 * Stores a name and an ID. Intended for displaying a list of columns from a database while storing each column's ID.
 * Created by Leo40Git on 16/02/2020.
 */
public final class NameIDPair {
	private String name;
	private int id;

	public NameIDPair(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@NonNull
	@Override
	public String toString() {
		return name;
	}
}
