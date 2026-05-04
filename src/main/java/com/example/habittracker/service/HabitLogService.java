package com.example.habittracker.service;

import com.example.habittracker.dto.BulkHabitLogResponseDto;
import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.CreateHabitLogsBulkRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.mapper.HabitLogMapper;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitLog;
import com.example.habittracker.repository.HabitLogRepository;
import com.example.habittracker.repository.HabitRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final HabitLogMapper habitLogMapper;

    public HabitLogService(HabitLogRepository habitLogRepository,
                           HabitRepository habitRepository,
                           HabitLogMapper habitLogMapper) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
        this.habitLogMapper = habitLogMapper;
    }

    @Transactional
    public HabitLogResponseDto createHabitLog(CreateHabitLogRequest request) {
        Habit habit = habitRepository.findById(request.getHabitId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Habit not found with id: " + request.getHabitId()
                ));

        LocalDate today = LocalDate.now();
        validateLogDoesNotExistForToday(request.getHabitId(), today);

        HabitLog habitLog = habitLogMapper.toEntity(request, habit);
        habitLog.setDate(today);
        HabitLog savedHabitLog = habitLogRepository.save(habitLog);

        return habitLogMapper.toResponseDto(savedHabitLog);
    }

    @Transactional
    public BulkHabitLogResponseDto createHabitLogsBulk(CreateHabitLogsBulkRequest request) {
        LocalDate today = LocalDate.now();
        List<Long> habitIds = request.getLogs().stream()
                .map(CreateHabitLogRequest::getHabitId)
                .toList();

        validateNoDuplicateHabitIds(habitIds);
        Map<Long, Habit> habitsById = getHabitsById(habitIds);
        validateLogsDoNotExistForDate(habitIds, today);

        List<HabitLog> habitLogsToSave = habitIds.stream()
                .map(habitId -> new HabitLog(habitsById.get(habitId), today))
                .toList();

        List<HabitLogResponseDto> savedLogs = habitLogRepository.saveAll(habitLogsToSave).stream()
                .map(habitLogMapper::toResponseDto)
                .toList();

        return new BulkHabitLogResponseDto(savedLogs);
    }

    public BulkHabitLogResponseDto createHabitLogsBulkWithoutTransaction(CreateHabitLogsBulkRequest request) {
        return createHabitLogsOneByOne(request);
    }

    @Transactional
    public BulkHabitLogResponseDto createHabitLogsBulkWithTransaction(CreateHabitLogsBulkRequest request) {
        return createHabitLogsOneByOne(request);
    }

    @Transactional(readOnly = true)
    public List<HabitLogResponseDto> getAllHabitLogs() {
        List<HabitLog> habitLogs = habitLogRepository.findAll();

        return habitLogs.stream()
                .map(habitLogMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public HabitLogResponseDto getHabitLogById(Long id) {
        HabitLog habitLog = getHabitLogByIdEntity(id);
        return habitLogMapper.toResponseDto(habitLog);
    }

    @Transactional
    public void deleteHabitLog(Long id) {
        if (!habitLogRepository.existsById(id)) {
            throw new EntityNotFoundException("Habit log not found with id: " + id);
        }
        habitLogRepository.deleteById(id);
    }

    private HabitLog getHabitLogByIdEntity(Long id) {
        return habitLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habit log not found with id: " + id));
    }

    private BulkHabitLogResponseDto createHabitLogsOneByOne(CreateHabitLogsBulkRequest request) {
        LocalDate today = LocalDate.now();
        List<HabitLogResponseDto> savedLogs = request.getLogs().stream()
                .map(logRequest -> createHabitLogForDate(logRequest, today))
                .toList();

        return new BulkHabitLogResponseDto(savedLogs);
    }

    private HabitLogResponseDto createHabitLogForDate(CreateHabitLogRequest request, LocalDate date) {
        Habit habit = habitRepository.findById(request.getHabitId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Habit not found with id: " + request.getHabitId()
                ));

        validateLogDoesNotExistForToday(request.getHabitId(), date);

        HabitLog habitLog = habitLogMapper.toEntity(request, habit);
        habitLog.setDate(date);

        HabitLog savedHabitLog = habitLogRepository.save(habitLog);
        return habitLogMapper.toResponseDto(savedHabitLog);
    }

    private void validateLogDoesNotExistForToday(Long habitId, LocalDate date) {
        if (habitLogRepository.existsByHabitIdAndDate(habitId, date)) {
            throw new EntityExistsException(
                    "Habit log for habit id " + habitId + " already exists for date " + date
            );
        }
    }

    private Map<Long, Habit> getHabitsById(List<Long> habitIds) {
        Map<Long, Habit> habitsById = habitRepository.findAllById(habitIds).stream()
                .collect(LinkedHashMap::new, (map, habit) -> map.put(habit.getId(), habit), Map::putAll);

        if (habitsById.size() == habitIds.size()) {
            return habitsById;
        }

        Long missingHabitId = habitIds.stream()
                .filter(habitId -> !habitsById.containsKey(habitId))
                .findFirst()
                .orElse(null);

        throw new EntityNotFoundException("Habit not found with id: " + missingHabitId);
    }

    private void validateLogsDoNotExistForDate(List<Long> habitIds, LocalDate date) {
        List<Long> loggedHabitIds = habitLogRepository.findLoggedHabitIdsByHabitIdsAndDate(habitIds, date);
        if (!loggedHabitIds.isEmpty()) {
            throw new EntityExistsException(
                    "Habit log for habit id " + loggedHabitIds.get(0) + " already exists for date " + date
            );
        }
    }

    private void validateNoDuplicateHabitIds(List<Long> habitIds) {
        Set<Long> seenHabitIds = new LinkedHashSet<>();
        for (Long habitId : habitIds) {
            if (!seenHabitIds.add(habitId)) {
                throw new IllegalArgumentException("Habit id " + habitId + " is duplicated in bulk request");
            }
        }
    }
}
