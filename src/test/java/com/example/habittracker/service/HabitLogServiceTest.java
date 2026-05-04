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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitLogServiceTest {

    @Mock
    private HabitLogRepository habitLogRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitLogMapper habitLogMapper;

    @InjectMocks
    private HabitLogService habitLogService;

    @Test
    void createHabitLog_shouldCreateLogWhenHabitExists() {
        CreateHabitLogRequest request = new CreateHabitLogRequest(1L);
        Habit habit = createHabit(1L);
        HabitLog habitLogToSave = new HabitLog(habit, null);
        HabitLog savedHabitLog = createHabitLog(10L, habit, LocalDate.now());
        HabitLogResponseDto responseDto = new HabitLogResponseDto(10L, 1L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitLogRepository.existsByHabitIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(habitLogMapper.toEntity(request, habit)).thenReturn(habitLogToSave);
        when(habitLogRepository.save(habitLogToSave)).thenReturn(savedHabitLog);
        when(habitLogMapper.toResponseDto(savedHabitLog)).thenReturn(responseDto);

        HabitLogResponseDto result = habitLogService.createHabitLog(request);

        assertEquals(responseDto, result);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(habitLogRepository).existsByHabitIdAndDate(eq(1L), dateCaptor.capture());
        verify(habitLogRepository).save(habitLogToSave);
        assertEquals(dateCaptor.getValue(), habitLogToSave.getDate());
    }

    @Test
    void createHabitLog_shouldThrowWhenHabitDoesNotExist() {
        CreateHabitLogRequest request = new CreateHabitLogRequest(99L);
        when(habitRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitLogService.createHabitLog(request)
        );

        assertEquals("Habit not found with id: 99", exception.getMessage());
        verify(habitLogRepository, never()).save(any(HabitLog.class));
    }

    @Test
    void createHabitLog_shouldThrowWhenLogAlreadyExistsForToday() {
        CreateHabitLogRequest request = new CreateHabitLogRequest(1L);
        Habit habit = createHabit(1L);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitLogRepository.existsByHabitIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> habitLogService.createHabitLog(request)
        );

        assertEquals(
                "Habit log for habit id 1 already exists for date " + LocalDate.now(),
                exception.getMessage()
        );
        verify(habitLogRepository, never()).save(any(HabitLog.class));
    }

    @Test
    void createHabitLogsBulk_shouldSaveAllLogsWhenInputIsValid() {
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(
                new CreateHabitLogRequest(1L),
                new CreateHabitLogRequest(2L)
        ));
        Habit firstHabit = createHabit(1L);
        Habit secondHabit = createHabit(2L);
        LocalDate today = LocalDate.now();

        HabitLog savedFirstLog = createHabitLog(101L, firstHabit, today);
        HabitLog savedSecondLog = createHabitLog(102L, secondHabit, today);
        HabitLogResponseDto firstResponse = new HabitLogResponseDto(101L, 1L, today);
        HabitLogResponseDto secondResponse = new HabitLogResponseDto(102L, 2L, today);

        when(habitRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstHabit, secondHabit));
        when(habitLogRepository.findLoggedHabitIdsByHabitIdsAndDate(eq(List.of(1L, 2L)), any(LocalDate.class)))
                .thenReturn(List.of());
        when(habitLogRepository.saveAll(any())).thenReturn(List.of(savedFirstLog, savedSecondLog));
        when(habitLogMapper.toResponseDto(savedFirstLog)).thenReturn(firstResponse);
        when(habitLogMapper.toResponseDto(savedSecondLog)).thenReturn(secondResponse);

        BulkHabitLogResponseDto result = habitLogService.createHabitLogsBulk(request);

        assertNotNull(result);
        assertEquals(2, result.getLogs().size());
        assertEquals(firstResponse, result.getLogs().get(0));
        assertEquals(secondResponse, result.getLogs().get(1));

        ArgumentCaptor<List<HabitLog>> logsCaptor = ArgumentCaptor.forClass(List.class);
        verify(habitLogRepository).saveAll(logsCaptor.capture());
        List<HabitLog> logsToSave = logsCaptor.getValue();
        assertEquals(2, logsToSave.size());
        assertEquals(1L, logsToSave.get(0).getHabit().getId());
        assertEquals(2L, logsToSave.get(1).getHabit().getId());
        assertEquals(today, logsToSave.get(0).getDate());
        assertEquals(today, logsToSave.get(1).getDate());
    }

    @Test
    void createHabitLogsBulk_shouldThrowWhenBulkContainsDuplicateHabitIds() {
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(
                new CreateHabitLogRequest(1L),
                new CreateHabitLogRequest(1L)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> habitLogService.createHabitLogsBulk(request)
        );

        assertEquals("Habit id 1 is duplicated in bulk request", exception.getMessage());
        verify(habitRepository, never()).findAllById(any());
        verify(habitLogRepository, never()).saveAll(any());
    }

    @Test
    void createHabitLogsBulk_shouldThrowWhenOneHabitDoesNotExist() {
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(
                new CreateHabitLogRequest(1L),
                new CreateHabitLogRequest(2L)
        ));
        Habit firstHabit = createHabit(1L);

        when(habitRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstHabit));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitLogService.createHabitLogsBulk(request)
        );

        assertEquals("Habit not found with id: 2", exception.getMessage());
        verify(habitLogRepository, never()).saveAll(any());
    }

    @Test
    void createHabitLogsBulk_shouldThrowWhenSomeLogAlreadyExistsForToday() {
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(
                new CreateHabitLogRequest(1L),
                new CreateHabitLogRequest(2L)
        ));
        Habit firstHabit = createHabit(1L);
        Habit secondHabit = createHabit(2L);

        when(habitRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstHabit, secondHabit));
        when(habitLogRepository.findLoggedHabitIdsByHabitIdsAndDate(eq(List.of(1L, 2L)), any(LocalDate.class)))
                .thenReturn(List.of(2L));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> habitLogService.createHabitLogsBulk(request)
        );

        assertEquals(
                "Habit log for habit id 2 already exists for date " + LocalDate.now(),
                exception.getMessage()
        );
        verify(habitLogRepository, never()).saveAll(any());
    }

    @Test
    void createHabitLogsBulkWithoutTransaction_shouldStopOnMissingHabit() {
        CreateHabitLogRequest firstRequest = new CreateHabitLogRequest(1L);
        CreateHabitLogRequest secondRequest = new CreateHabitLogRequest(999L);
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(firstRequest, secondRequest));
        Habit firstHabit = createHabit(1L);
        HabitLog firstLogToSave = new HabitLog(firstHabit, null);
        HabitLog savedFirstLog = createHabitLog(201L, firstHabit, LocalDate.now());
        HabitLogResponseDto firstResponse = new HabitLogResponseDto(201L, 1L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(firstHabit));
        when(habitRepository.findById(999L)).thenReturn(Optional.empty());
        when(habitLogRepository.existsByHabitIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(habitLogMapper.toEntity(same(firstRequest), same(firstHabit))).thenReturn(firstLogToSave);
        when(habitLogRepository.save(firstLogToSave)).thenReturn(savedFirstLog);
        when(habitLogMapper.toResponseDto(savedFirstLog)).thenReturn(firstResponse);

        assertThrows(
                EntityNotFoundException.class,
                () -> habitLogService.createHabitLogsBulkWithoutTransaction(request)
        );

        verify(habitLogRepository, times(1)).save(firstLogToSave);
    }

    @Test
    void createHabitLogsBulkWithoutTransaction_shouldCreateAllLogsWhenValid() {
        CreateHabitLogRequest firstRequest = new CreateHabitLogRequest(1L);
        CreateHabitLogRequest secondRequest = new CreateHabitLogRequest(2L);
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(firstRequest, secondRequest));
        Habit firstHabit = createHabit(1L);
        Habit secondHabit = createHabit(2L);
        HabitLog firstLogToSave = new HabitLog(firstHabit, null);
        HabitLog secondLogToSave = new HabitLog(secondHabit, null);
        HabitLog savedFirstLog = createHabitLog(211L, firstHabit, LocalDate.now());
        HabitLog savedSecondLog = createHabitLog(212L, secondHabit, LocalDate.now());
        HabitLogResponseDto firstResponse = new HabitLogResponseDto(211L, 1L, LocalDate.now());
        HabitLogResponseDto secondResponse = new HabitLogResponseDto(212L, 2L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(firstHabit));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(secondHabit));
        when(habitLogRepository.existsByHabitIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(habitLogRepository.existsByHabitIdAndDate(eq(2L), any(LocalDate.class))).thenReturn(false);
        when(habitLogMapper.toEntity(same(firstRequest), same(firstHabit))).thenReturn(firstLogToSave);
        when(habitLogMapper.toEntity(same(secondRequest), same(secondHabit))).thenReturn(secondLogToSave);
        when(habitLogRepository.save(firstLogToSave)).thenReturn(savedFirstLog);
        when(habitLogRepository.save(secondLogToSave)).thenReturn(savedSecondLog);
        when(habitLogMapper.toResponseDto(savedFirstLog)).thenReturn(firstResponse);
        when(habitLogMapper.toResponseDto(savedSecondLog)).thenReturn(secondResponse);

        BulkHabitLogResponseDto result = habitLogService.createHabitLogsBulkWithoutTransaction(request);

        assertEquals(List.of(firstResponse, secondResponse), result.getLogs());
    }

    @Test
    void createHabitLogsBulkWithTransaction_shouldCreateAllLogsWhenValid() {
        CreateHabitLogRequest firstRequest = new CreateHabitLogRequest(1L);
        CreateHabitLogRequest secondRequest = new CreateHabitLogRequest(2L);
        CreateHabitLogsBulkRequest request = new CreateHabitLogsBulkRequest(List.of(firstRequest, secondRequest));
        Habit firstHabit = createHabit(1L);
        Habit secondHabit = createHabit(2L);
        HabitLog firstLogToSave = new HabitLog(firstHabit, null);
        HabitLog secondLogToSave = new HabitLog(secondHabit, null);
        HabitLog savedFirstLog = createHabitLog(301L, firstHabit, LocalDate.now());
        HabitLog savedSecondLog = createHabitLog(302L, secondHabit, LocalDate.now());
        HabitLogResponseDto firstResponse = new HabitLogResponseDto(301L, 1L, LocalDate.now());
        HabitLogResponseDto secondResponse = new HabitLogResponseDto(302L, 2L, LocalDate.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(firstHabit));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(secondHabit));
        when(habitLogRepository.existsByHabitIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(habitLogRepository.existsByHabitIdAndDate(eq(2L), any(LocalDate.class))).thenReturn(false);
        when(habitLogMapper.toEntity(same(firstRequest), same(firstHabit))).thenReturn(firstLogToSave);
        when(habitLogMapper.toEntity(same(secondRequest), same(secondHabit))).thenReturn(secondLogToSave);
        when(habitLogRepository.save(firstLogToSave)).thenReturn(savedFirstLog);
        when(habitLogRepository.save(secondLogToSave)).thenReturn(savedSecondLog);
        when(habitLogMapper.toResponseDto(savedFirstLog)).thenReturn(firstResponse);
        when(habitLogMapper.toResponseDto(savedSecondLog)).thenReturn(secondResponse);

        BulkHabitLogResponseDto result = habitLogService.createHabitLogsBulkWithTransaction(request);

        assertEquals(List.of(firstResponse, secondResponse), result.getLogs());
    }

    @Test
    void getAllHabitLogs_shouldReturnMappedDtos() {
        Habit firstHabit = createHabit(1L);
        Habit secondHabit = createHabit(2L);
        HabitLog firstLog = createHabitLog(10L, firstHabit, LocalDate.now());
        HabitLog secondLog = createHabitLog(11L, secondHabit, LocalDate.now());
        HabitLogResponseDto firstDto = new HabitLogResponseDto(10L, 1L, LocalDate.now());
        HabitLogResponseDto secondDto = new HabitLogResponseDto(11L, 2L, LocalDate.now());

        when(habitLogRepository.findAll()).thenReturn(List.of(firstLog, secondLog));
        when(habitLogMapper.toResponseDto(firstLog)).thenReturn(firstDto);
        when(habitLogMapper.toResponseDto(secondLog)).thenReturn(secondDto);

        List<HabitLogResponseDto> result = habitLogService.getAllHabitLogs();

        assertEquals(List.of(firstDto, secondDto), result);
    }

    @Test
    void getHabitLogById_shouldReturnMappedDto() {
        Habit habit = createHabit(1L);
        HabitLog habitLog = createHabitLog(10L, habit, LocalDate.now());
        HabitLogResponseDto dto = new HabitLogResponseDto(10L, 1L, LocalDate.now());

        when(habitLogRepository.findById(10L)).thenReturn(Optional.of(habitLog));
        when(habitLogMapper.toResponseDto(habitLog)).thenReturn(dto);

        HabitLogResponseDto result = habitLogService.getHabitLogById(10L);

        assertEquals(dto, result);
    }

    @Test
    void getHabitLogById_shouldThrowWhenMissing() {
        when(habitLogRepository.findById(10L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitLogService.getHabitLogById(10L)
        );

        assertEquals("Habit log not found with id: 10", exception.getMessage());
    }

    @Test
    void deleteHabitLog_shouldDeleteWhenLogExists() {
        when(habitLogRepository.existsById(5L)).thenReturn(true);

        habitLogService.deleteHabitLog(5L);

        verify(habitLogRepository).deleteById(5L);
    }

    @Test
    void deleteHabitLog_shouldThrowWhenLogDoesNotExist() {
        when(habitLogRepository.existsById(5L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitLogService.deleteHabitLog(5L)
        );

        assertEquals("Habit log not found with id: 5", exception.getMessage());
        verify(habitLogRepository, never()).deleteById(any(Long.class));
    }

    private Habit createHabit(Long id) {
        Habit habit = new Habit();
        habit.setId(id);
        return habit;
    }

    private HabitLog createHabitLog(Long id, Habit habit, LocalDate date) {
        HabitLog habitLog = new HabitLog();
        habitLog.setId(id);
        habitLog.setHabit(habit);
        habitLog.setDate(date);
        return habitLog;
    }
}
