package com.example.fullstack_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@SpringBootApplication
public class FullstackSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullstackSpringbootApplication.class, args);
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
        return (String... args) -> repository.save(new SensorReading(25.5f, 60.2f, LocalDateTime.now()));
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

	private final SensorReadingRepository sensorReadingRepository;

	public SensorController(SensorReadingRepository sensorReadingRepository) {
		this.sensorReadingRepository = sensorReadingRepository;
	}

	// Get the latest sensor reading
	@GetMapping("/latest")
	public SensorReading getLatestReading() {
		return sensorReadingRepository.findTop10ByOrderByTimestampDesc().stream().findFirst().orElse(null);
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

	private final SensorReadingRepository sensorReadingRepository;

	public DashboardController(SensorReadingRepository sensorReadingRepository) {
		this.sensorReadingRepository = sensorReadingRepository;
	}

	@GetMapping("/")
	public String showDashboard(Model model) {
		// Fetch the latest reading
		model.addAttribute("latestReading", sensorReadingRepository.findTop10ByOrderByTimestampDesc().stream().findFirst().orElse(null));

		// Fetch historical data
		model.addAttribute("readings", sensorReadingRepository.findTop10ByOrderByTimestampDesc());

		// Return the view name "dashboard"
		return "dashboard"; // Ensure that you have a dashboard.html file in resources/templates
	}
}
