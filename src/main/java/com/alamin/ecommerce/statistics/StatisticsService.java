package com.alamin.ecommerce.statistics;

import java.util.List;
import java.util.Optional;

public interface StatisticsService {
    Statistics saveStatistics(Statistics statistics);

    List<Statistics> getAllStatistics();

    Optional<Statistics> getStatisticsById(Long id);

    void deleteStatistics(Long id);
}
