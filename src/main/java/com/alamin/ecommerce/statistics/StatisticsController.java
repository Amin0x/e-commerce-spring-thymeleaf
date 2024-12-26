package com.alamin.ecommerce.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // Create a new statistic
    @PostMapping
    public ResponseEntity<Statistics> createStatistics(@RequestBody Statistics statistics) {
        Statistics createdStatistics = statisticsService.saveStatistics(statistics);
        return new ResponseEntity<>(createdStatistics, HttpStatus.CREATED);
    }

    // Get all statistics
    @GetMapping
    public ResponseEntity<List<Statistics>> getAllStatistics() {
        List<Statistics> statisticsList = statisticsService.getAllStatistics();
        return new ResponseEntity<>(statisticsList, HttpStatus.OK);
    }

    // Get a single statistic by ID
    @GetMapping("/{id}")
    public ResponseEntity<Statistics> getStatisticsById(@PathVariable Long id) {
        Optional<Statistics> statistics = statisticsService.getStatisticsById(id);
        return statistics.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a statistic by ID
    @PutMapping("/{id}")
    public ResponseEntity<Statistics> updateStatistics(@PathVariable Long id, @RequestBody Statistics statistics) {
        Optional<Statistics> existingStatistics = statisticsService.getStatisticsById(id);
        if (existingStatistics.isPresent()) {
            statistics.setId(id);  // Ensure the ID is set to the provided ID
            Statistics updatedStatistics = statisticsService.saveStatistics(statistics);
            return new ResponseEntity<>(updatedStatistics, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a statistic by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatistics(@PathVariable Long id) {
        Optional<Statistics> existingStatistics = statisticsService.getStatisticsById(id);
        if (existingStatistics.isPresent()) {
            statisticsService.deleteStatistics(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
