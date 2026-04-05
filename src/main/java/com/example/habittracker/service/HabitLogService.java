package com.example.habittracker.service;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.mapper.HabitLogMapper;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitLog;
import com.example.habittracker.repository.HabitLogRepository;
import com.example.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        LocalDate today = LocalDate.now();

        HabitLog habitLog = new HabitLog(habit, today);
        HabitLog savedHabitLog = habitLogRepository.save(habitLog);

        return habitLogMapper.toResponseDto(savedHabitLog);
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
            throw new RuntimeException("Лог не найден с id: " + id);
        }
        habitLogRepository.deleteById(id);
    }

    private HabitLog getHabitLogByIdEntity(Long id) {
        return habitLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Лог не найден с id: " + id));
    }
}