package com.example.habittracker.service;

import com.example.habittracker.cache.HabitSearchCache;
import com.example.habittracker.cache.HabitSearchCacheKey;
import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.dto.UserWithHabitResponseDto;
import com.example.habittracker.mapper.HabitMapper;
import com.example.habittracker.mapper.UserWithHabitMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.CategoryRepository;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
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
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitMapper habitMapper;

    @Mock
    private UserWithHabitMapper userWithHabitMapper;

    @Mock
    private HabitSearchCache habitSearchCache;

    @InjectMocks
    private HabitService habitService;

    @Test
    void createHabit_shouldSaveHabitWithoutCategories() {
        CreateHabitRequest request = new CreateHabitRequest("Run", "Morning run", 1L, List.of());
        User user = createUser(1L, "john", "john@mail.com");
        Habit habit = createHabit(null, "Run", "Morning run");
        Habit savedHabit = createHabit(10L, "Run", "Morning run");
        HabitResponseDto dto = createHabitResponseDto(10L, "Run");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(habitRepository.existsByName("Run")).thenReturn(false);
        when(habitMapper.toEntity(request, List.of())).thenReturn(habit);
        when(habitRepository.save(habit)).thenReturn(savedHabit);
        when(habitMapper.toResponseDto(savedHabit)).thenReturn(dto);

        HabitResponseDto result = habitService.createHabit(request);

        assertEquals(dto, result);
        assertEquals(user, habit.getUser());
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void createHabit_shouldSaveHabit_whenCategoryIdsIsNull() {
        CreateHabitRequest request = new CreateHabitRequest("Run", "Morning run", 1L, null);
        User user = createUser(1L, "john", "john@mail.com");
        Habit habit = createHabit(null, "Run", "Morning run");
        Habit savedHabit = createHabit(10L, "Run", "Morning run");
        HabitResponseDto dto = createHabitResponseDto(10L, "Run");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(habitRepository.existsByName("Run")).thenReturn(false);
        when(habitMapper.toEntity(request, List.of())).thenReturn(habit);
        when(habitRepository.save(habit)).thenReturn(savedHabit);
        when(habitMapper.toResponseDto(savedHabit)).thenReturn(dto);

        HabitResponseDto result = habitService.createHabit(request);

        assertEquals(dto, result);
        verify(categoryRepository, never()).findAllById(any());
    }

    @Test
    void createHabit_shouldSaveHabitWithCategories_whenAllCategoriesFound() {
        CreateHabitRequest request = new CreateHabitRequest("Run", "Morning run", 1L, List.of(1L, 2L));
        User user = createUser(1L, "john", "john@mail.com");
        Category cat1 = createCategory(1L, "Health");
        Category cat2 = createCategory(2L, "Sport");
        List<Category> categories = List.of(cat1, cat2);
        Habit habit = createHabit(null, "Run", "Morning run");
        Habit savedHabit = createHabit(10L, "Run", "Morning run");
        HabitResponseDto dto = createHabitResponseDto(10L, "Run");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(habitRepository.existsByName("Run")).thenReturn(false);
        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(categories);
        when(habitMapper.toEntity(request, categories)).thenReturn(habit);
        when(habitRepository.save(habit)).thenReturn(savedHabit);
        when(habitMapper.toResponseDto(savedHabit)).thenReturn(dto);

        HabitResponseDto result = habitService.createHabit(request);

        assertEquals(dto, result);
        verify(categoryRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void updateHabit_shouldNotUpdateName_whenNameIsNull() {
        Habit habit = createHabit(1L, "Old", "old desc");
        UpdateHabitRequest request = new UpdateHabitRequest(null, "new desc", null);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(habit);
        when(habitMapper.toResponseDto(habit)).thenReturn(createHabitResponseDto(1L, "Old"));

        habitService.updateHabit(1L, request);

        assertEquals("Old", habit.getName()); // имя не изменилось
        verify(habitRepository, never()).existsByName(any());
    }

    @Test
    void updateHabit_shouldNotUpdateName_whenNameIsSame() {
        Habit habit = createHabit(1L, "Run", "old desc");
        UpdateHabitRequest request = new UpdateHabitRequest("Run", "new desc", null);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(habit);
        when(habitMapper.toResponseDto(habit)).thenReturn(createHabitResponseDto(1L, "Run"));

        habitService.updateHabit(1L, request);

        assertEquals("Run", habit.getName());
        verify(habitRepository, never()).existsByName(any());
    }

    @Test
    void updateHabit_shouldNotUpdateCategories_whenCategoryIdsIsNull() {
        Habit habit = createHabit(1L, "Run", "desc");
        UpdateHabitRequest request = new UpdateHabitRequest(null, null, null);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(habit);
        when(habitMapper.toResponseDto(habit)).thenReturn(createHabitResponseDto(1L, "Run"));

        habitService.updateHabit(1L, request);

        // categories = null, updateEntity вызывается с null
        verify(habitMapper).updateEntity(same(habit), same(request), eq(null));
    }

    @Test
    void searchHabitsByUserAndCategoryJpql_shouldFilterOutMissingHabits() {
        Pageable pageable = PageRequest.of(0, 10);
        // Явно указываем totalElements = 2, content = [1L, 2L]
        Page<Long> idsPage = new PageImpl<>(List.of(1L, 2L), pageable, 2);

        // Репозиторий вернул только habit с id=2, habit с id=1 отсутствует
        Habit habit2 = createHabit(2L, "Read", "desc");
        List<Habit> fetchedHabits = List.of(habit2);
        HabitResponseDto dto2 = createHabitResponseDto(2L, "Read");

        when(habitSearchCache.get(any(HabitSearchCacheKey.class))).thenReturn(null);
        when(habitRepository.findHabitIdsByUserAndCategoryJpql("john", "health", pageable)).thenReturn(idsPage);
        when(habitRepository.findAllWithUserAndCategoriesByIdIn(List.of(1L, 2L))).thenReturn(fetchedHabits);
        when(habitMapper.toResponseDto(habit2)).thenReturn(dto2);

        Page<HabitResponseDto> result = habitService.searchHabitsByUserAndCategoryJpql("john", "health", pageable);

        // Должен вернуться только habit с id=2
        assertEquals(1, result.getContent().size());
        assertEquals(2L, result.getContent().get(0).getId());
        // Общее количество элементов остаётся 2 (как в idsPage)
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void createHabit_shouldThrowWhenUserMissing() {
        CreateHabitRequest request = new CreateHabitRequest("Run", "Morning run", 1L, List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitService.createHabit(request)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void createHabit_shouldThrowWhenNameExists() {
        CreateHabitRequest request = new CreateHabitRequest("Run", "Morning run", 1L, List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L, "john", "john@mail.com")));
        when(habitRepository.existsByName("Run")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> habitService.createHabit(request)
        );

        assertEquals("Habit with name 'Run' already exists", exception.getMessage());
    }

    @Test
    void createHabit_shouldThrowWhenSomeCategoriesMissing() {
        CreateHabitRequest request = new CreateHabitRequest("Run", "Morning run", 1L, List.of(1L, 2L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L, "john", "john@mail.com")));
        when(habitRepository.existsByName("Run")).thenReturn(false);
        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(createCategory(1L, "Health")));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitService.createHabit(request)
        );

        assertEquals("Some categories were not found", exception.getMessage());
    }

    @Test
    void getAllHabits_shouldReturnMappedDtos() {
        Habit first = createHabit(1L, "Run", "d1");
        Habit second = createHabit(2L, "Read", "d2");
        HabitResponseDto firstDto = createHabitResponseDto(1L, "Run");
        HabitResponseDto secondDto = createHabitResponseDto(2L, "Read");

        when(habitRepository.findAll()).thenReturn(List.of(first, second));
        when(habitMapper.toResponseDto(first)).thenReturn(firstDto);
        when(habitMapper.toResponseDto(second)).thenReturn(secondDto);

        List<HabitResponseDto> result = habitService.getAllHabits();

        assertEquals(List.of(firstDto, secondDto), result);
    }

    @Test
    void getHabitById_shouldReturnMappedDto() {
        Habit habit = createHabit(1L, "Run", "desc");
        HabitResponseDto dto = createHabitResponseDto(1L, "Run");

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitMapper.toResponseDto(habit)).thenReturn(dto);

        HabitResponseDto result = habitService.getHabitById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getHabitById_shouldThrowWhenMissing() {
        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitService.getHabitById(1L)
        );

        assertEquals("Habit not found with id: 1", exception.getMessage());
    }

    @Test
    void updateHabit_shouldUpdateAndInvalidateCache() {
        Habit habit = createHabit(1L, "Old", "old desc");
        UpdateHabitRequest request = new UpdateHabitRequest("New", "new desc", List.of(1L));
        Category category = createCategory(1L, "Health");
        HabitResponseDto dto = createHabitResponseDto(1L, "New");

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.existsByName("New")).thenReturn(false);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(habitRepository.save(habit)).thenReturn(habit);
        when(habitMapper.toResponseDto(habit)).thenReturn(dto);

        HabitResponseDto result = habitService.updateHabit(1L, request);

        assertEquals(dto, result);
        assertEquals("New", habit.getName());
        assertEquals("new desc", habit.getDescription());
        verify(habitMapper).updateEntity(same(habit), same(request), eq(List.of(category)));
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void updateHabit_shouldThrowWhenNameTaken() {
        Habit habit = createHabit(1L, "Old", "old desc");
        UpdateHabitRequest request = new UpdateHabitRequest("New", "new desc", null);

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.existsByName("New")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> habitService.updateHabit(1L, request)
        );

        assertEquals("Name 'New' is already taken", exception.getMessage());
    }

    @Test
    void updateHabit_shouldPassEmptyCategoriesWhenRequestContainsEmptyList() {
        Habit habit = createHabit(1L, "Run", "desc");
        UpdateHabitRequest request = new UpdateHabitRequest(null, null, List.of());
        HabitResponseDto dto = createHabitResponseDto(1L, "Run");

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(habit);
        when(habitMapper.toResponseDto(habit)).thenReturn(dto);

        HabitResponseDto result = habitService.updateHabit(1L, request);

        assertEquals(dto, result);
        verify(habitMapper).updateEntity(same(habit), same(request), eq(new ArrayList<>()));
    }

    @Test
    void updateHabit_shouldThrowWhenSomeCategoriesMissing() {
        Habit habit = createHabit(1L, "Run", "desc");
        UpdateHabitRequest request = new UpdateHabitRequest(null, null, List.of(1L, 2L));

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(createCategory(1L, "Health")));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitService.updateHabit(1L, request)
        );

        assertEquals("Some categories were not found", exception.getMessage());
    }

    @Test
    void deleteHabit_shouldDeleteAndInvalidateCache() {
        when(habitRepository.existsById(1L)).thenReturn(true);

        habitService.deleteHabit(1L);

        verify(habitRepository).deleteById(1L);
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void deleteHabit_shouldThrowWhenMissing() {
        when(habitRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> habitService.deleteHabit(1L)
        );

        assertEquals("Habit not found with id: 1", exception.getMessage());
    }

    @Test
    void getHabitsWithProblem_shouldReturnMappedDtos() {
        Habit habit = createHabit(1L, "Run", "desc");
        HabitResponseDto dto = createHabitResponseDto(1L, "Run");

        when(habitRepository.findAll()).thenReturn(List.of(habit));
        when(habitMapper.toResponseDto(habit)).thenReturn(dto);

        List<HabitResponseDto> result = habitService.getHabitsWithProblem();

        assertEquals(List.of(dto), result);
    }

    @Test
    void getHabitsOptimized_shouldReturnMappedDtos() {
        Habit habit = createHabit(1L, "Run", "desc");
        HabitResponseDto dto = createHabitResponseDto(1L, "Run");

        when(habitRepository.findAllOptimized()).thenReturn(List.of(habit));
        when(habitMapper.toResponseDto(habit)).thenReturn(dto);

        List<HabitResponseDto> result = habitService.getHabitsOptimized();

        assertEquals(List.of(dto), result);
    }

    @Test
    void searchHabitsByUserAndCategoryJpql_shouldReturnCachedPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<HabitResponseDto> cachedPage = new PageImpl<>(List.of(createHabitResponseDto(1L, "Run")));

        when(habitSearchCache.get(any(HabitSearchCacheKey.class))).thenReturn(cachedPage);

        Page<HabitResponseDto> result = habitService.searchHabitsByUserAndCategoryJpql(" john ", " health ", pageable);

        assertEquals(cachedPage, result);
        verify(habitRepository, never()).findHabitIdsByUserAndCategoryJpql(any(), any(), any());
    }

    @Test
    void searchHabitsByUserAndCategoryJpql_shouldReturnEmptyPageWhenNoIdsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Long> emptyIdsPage = new PageImpl<>(List.of(), pageable, 0);

        when(habitSearchCache.get(any(HabitSearchCacheKey.class))).thenReturn(null);
        when(habitRepository.findHabitIdsByUserAndCategoryJpql("", "", pageable)).thenReturn(emptyIdsPage);

        Page<HabitResponseDto> result = habitService.searchHabitsByUserAndCategoryJpql(null, null, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(habitSearchCache).put(any(HabitSearchCacheKey.class), eq(result));
    }

    @Test
    void searchHabitsByUserAndCategoryJpql_shouldPreserveOrderAndCacheResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Long> idsPage = new PageImpl<>(List.of(2L, 1L), pageable, 2);
        Habit firstFetched = createHabit(1L, "Run", "desc");
        Habit secondFetched = createHabit(2L, "Read", "desc");
        HabitResponseDto firstDto = createHabitResponseDto(1L, "Run");
        HabitResponseDto secondDto = createHabitResponseDto(2L, "Read");

        when(habitSearchCache.get(any(HabitSearchCacheKey.class))).thenReturn(null);
        when(habitRepository.findHabitIdsByUserAndCategoryJpql("john", "health", pageable)).thenReturn(idsPage);
        when(habitRepository.findAllWithUserAndCategoriesByIdIn(List.of(2L, 1L))).thenReturn(List.of(firstFetched, secondFetched));
        when(habitMapper.toResponseDto(secondFetched)).thenReturn(secondDto);
        when(habitMapper.toResponseDto(firstFetched)).thenReturn(firstDto);

        Page<HabitResponseDto> result = habitService.searchHabitsByUserAndCategoryJpql("john", "health", pageable);

        assertEquals(List.of(secondDto, firstDto), result.getContent());
        verify(habitSearchCache).put(any(HabitSearchCacheKey.class), eq(result));
    }

    @Test
    void searchHabitsByUserAndCategoryNative_shouldUseTrimmedFiltersAndCacheResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Habit habit = createHabit(1L, "Run", "desc");
        Page<Habit> habitsPage = new PageImpl<>(List.of(habit), pageable, 1);
        HabitResponseDto dto = createHabitResponseDto(1L, "Run");

        when(habitSearchCache.get(any(HabitSearchCacheKey.class))).thenReturn(null);
        when(habitRepository.searchByUserAndCategoryNative("john", "health", pageable)).thenReturn(habitsPage);
        when(habitMapper.toResponseDto(habit)).thenReturn(dto);

        Page<HabitResponseDto> result = habitService.searchHabitsByUserAndCategoryNative(" john ", " health ", pageable);

        assertEquals(List.of(dto), result.getContent());
        verify(habitSearchCache).put(any(HabitSearchCacheKey.class), eq(result));
    }

    @Test
    void saveUserAndHabitWithoutTransaction_shouldSaveBothAndInvalidateCache() {
        User savedUser = createUser(1L, "john", "john@mail.com");
        Habit savedHabit = createHabit(2L, "Run", "Morning run");
        UserWithHabitResponseDto dto = new UserWithHabitResponseDto(
                1L, "john", "john@mail.com", 2L, "Run", "Morning run"
        );

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(habitRepository.save(any(Habit.class))).thenReturn(savedHabit);
        when(userWithHabitMapper.toResponseDto(savedUser, savedHabit)).thenReturn(dto);

        UserWithHabitResponseDto result = habitService.saveUserAndHabitWithoutTransaction(
                "john",
                "john@mail.com",
                "Run",
                "Morning run"
        );

        assertEquals(dto, result);
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void saveUserAndHabitWithTransaction_shouldDelegateToSameLogic() {
        User savedUser = createUser(1L, "john", "john@mail.com");
        Habit savedHabit = createHabit(2L, "Run", "Morning run");
        UserWithHabitResponseDto dto = new UserWithHabitResponseDto(
                1L, "john", "john@mail.com", 2L, "Run", "Morning run"
        );

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(habitRepository.save(any(Habit.class))).thenReturn(savedHabit);
        when(userWithHabitMapper.toResponseDto(savedUser, savedHabit)).thenReturn(dto);

        UserWithHabitResponseDto result = habitService.saveUserAndHabitWithTransaction(
                "john",
                "john@mail.com",
                "Run",
                "Morning run"
        );

        assertEquals(dto, result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private Habit createHabit(Long id, String name, String description) {
        Habit habit = new Habit();
        habit.setId(id);
        habit.setName(name);
        habit.setDescription(description);
        habit.setCategories(new ArrayList<>());
        habit.setUser(createUser(1L, "john", "john@mail.com"));
        return habit;
    }

    private HabitResponseDto createHabitResponseDto(Long id, String name) {
        return new HabitResponseDto(id, name, "desc", 1L, "john", List.of());
    }
}
