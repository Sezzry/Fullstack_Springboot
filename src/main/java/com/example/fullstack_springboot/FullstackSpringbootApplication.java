package com.example.sensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

@SpringBootApplication
public class FullstackSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(com.example.sensor.FullstackSpringbootApplication.class, args);
	}

	// Database configuration
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/sensor_data");
		dataSource.setUsername("root");
		dataSource.setPassword("Joeman339617!?");
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		return dataSource;
	}

	// Command line runner to test inserting a dummy record (Optional)
	@Bean
	public CommandLineRunner commandLineRunner(SensorReadingRepository repository) {
		return args -> {
			repository.save(new SensorReading(25.5f, 60.2f, LocalDateTime.now()));
		};
	}
}

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
class SensorReading {

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

// Repository interface for accessing sensor readings
interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
	List<SensorReading> findTop10ByOrderByTimestampDesc();
}

// REST Controller for providing API endpoints
@RestController
@RequestMapping("/api/sensor")
class SensorController {

	@Autowired
	private SensorReadingRepository sensorReadingRepository;

	// Get the latest sensor reading
	@GetMapping("/latest")
	public SensorReading getLatestReading() {
		return sensorReadingRepository.findTop10ByOrderByTimestampDesc().get(0);
	}

	// Get the last 10 sensor readings (history)
	@GetMapping("/history")
	public List<SensorReading> getSensorHistory() {
		return sensorReadingRepository.findTop10ByOrderByTimestampDesc();
	}
}

// Thymeleaf-based Web Controller for showing the dashboard
@Controller
class DashboardController {

	@Autowired
	private SensorReadingRepository sensorReadingRepository;

	@GetMapping("/")
	public String showDashboard(Model model) {
		// Fetch the latest reading
		model.addAttribute("latestReading", sensorReadingRepository.findTop10ByOrderByTimestampDesc().get(0));

		// Fetch historical data
		model.addAttribute("readings", sensorReadingRepository.findTop10ByOrderByTimestampDesc());

		// Return the view name "dashboard"
		return "dashboard";
	}
}
