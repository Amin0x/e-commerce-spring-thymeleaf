package com.alamin.ecommerce.statistics;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Override
    public Statistics saveStatistics(Statistics statistics) {
        return null;
    }

    @Override
    public List<Statistics> getAllStatistics() {
        return null;
    }

    @Override
    public Optional<Statistics> getStatisticsById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteStatistics(Long id) {

    }
}
