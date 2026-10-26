package com.alamin.ecommerce.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin REST statistics.
 */
@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping
    public ResponseEntity<Statistics> create(@RequestBody Statistics statistics) {
        return ResponseEntity.ok(statisticsService.saveStatistics(statistics));
    }

    @GetMapping
    public ResponseEntity<List<Statistics>> list() {
        return ResponseEntity.ok(statisticsService.getAllStatistics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Statistics> get(@PathVariable Long id) {
        return statisticsService.getStatisticsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Statistics> update(@PathVariable Long id,
                                             @RequestBody Statistics statistics) {
        if (statisticsService.getStatisticsById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        statistics.setId(id);
        return ResponseEntity.ok(statisticsService.saveStatistics(statistics));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (statisticsService.getStatisticsById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        statisticsService.deleteStatistics(id);
        return ResponseEntity.noContent().build();
    }
}
