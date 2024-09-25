package com.example.fullstack_springboot;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
public class SensorReading {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Float temperature;
	private Float humidity;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime timestamp;

	// Default constructor
	public SensorReading() {}

	// Parameterized constructor
	public SensorReading(Float temperature, Float humidity, LocalDateTime timestamp) {
		this.temperature = temperature;
		this.humidity = humidity;
		this.timestamp = timestamp;
	}
}
