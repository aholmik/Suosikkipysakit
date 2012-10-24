package com.karhatsu.omatpysakit.domain;

import java.util.List;

public class Stop {

	private final int code;
	private List<Departure> departures;

	public Stop(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public List<Departure> getDepartures() {
		return departures;
	}

	public void setDepartures(List<Departure> departures) {
		this.departures = departures;
	}
}
