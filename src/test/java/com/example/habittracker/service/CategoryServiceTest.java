package com.example.habittracker.service;

import com.example.habittracker.cache.HabitSearchCache;
import com.example.habittracker.dto.CategoryResponseDto;
import com.example.habittracker.dto.CreateCategoryRequest;
import com.example.habittracker.dto.UpdateCategoryRequest;
import com.example.habittracker.mapper.CategoryMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private HabitSearchCache habitSearchCache;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_shouldReturnMappedDtos() {
        Category first = createCategory(1L, "Health", "desc1");
        Category second = createCategory(2L, "Study", "desc2");
        CategoryResponseDto firstDto = new CategoryResponseDto(1L, "Health", "desc1");
        CategoryResponseDto secondDto = new CategoryResponseDto(2L, "Study", "desc2");

        when(categoryRepository.findAll()).thenReturn(List.of(first, second));
        when(categoryMapper.toResponseDto(first)).thenReturn(firstDto);
        when(categoryMapper.toResponseDto(second)).thenReturn(secondDto);

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertEquals(List.of(firstDto, secondDto), result);
    }

    @Test
    void getCategoryById_shouldReturnMappedDto() {
        Category category = createCategory(1L, "Health", "desc");
        CategoryResponseDto dto = new CategoryResponseDto(1L, "Health", "desc");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDto(category)).thenReturn(dto);

        CategoryResponseDto result = categoryService.getCategoryById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getCategoryById_shouldThrowWhenMissing() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getCategoryById(1L)
        );

        assertEquals("Category not found with id: 1", exception.getMessage());
    }

    @Test
    void createCategory_shouldSaveAndInvalidateCache() {
        CreateCategoryRequest request = new CreateCategoryRequest("Health", "desc");
        Category category = createCategory(null, "Health", "desc");
        Category savedCategory = createCategory(1L, "Health", "desc");
        CategoryResponseDto dto = new CategoryResponseDto(1L, "Health", "desc");

        when(categoryRepository.existsByName("Health")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toResponseDto(savedCategory)).thenReturn(dto);

        CategoryResponseDto result = categoryService.createCategory(request);

        assertEquals(dto, result);
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void createCategory_shouldThrowWhenNameExists() {
        CreateCategoryRequest request = new CreateCategoryRequest("Health", "desc");
        when(categoryRepository.existsByName("Health")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> categoryService.createCategory(request)
        );

        assertEquals("Category with name 'Health' already exists", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldUpdateFieldsAndInvalidateCache() {
        Category category = createCategory(1L, "Health", "old");
        UpdateCategoryRequest request = new UpdateCategoryRequest("Fitness", "new");
        CategoryResponseDto dto = new CategoryResponseDto(1L, "Fitness", "new");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Fitness")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDto(category)).thenReturn(dto);

        CategoryResponseDto result = categoryService.updateCategory(1L, request);

        assertEquals(dto, result);
        assertEquals("Fitness", category.getName());
        assertEquals("new", category.getDescription());
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void updateCategory_shouldThrowWhenNameTaken() {
        Category category = createCategory(1L, "Health", "old");
        UpdateCategoryRequest request = new UpdateCategoryRequest("Fitness", "new");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Fitness")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> categoryService.updateCategory(1L, request)
        );

        assertEquals("Name 'Fitness' is already taken", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_shouldDetachHabitsDeleteCategoryAndInvalidateCache() {
        Category category = createCategory(1L, "Health", "desc");
        Habit firstHabit = new Habit();
        firstHabit.setId(10L);
        Habit secondHabit = new Habit();
        secondHabit.setId(11L);
        firstHabit.getCategories().add(category);
        secondHabit.getCategories().add(category);
        category.getHabits().add(firstHabit);
        category.getHabits().add(secondHabit);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertEquals(0, category.getHabits().size());
        assertEquals(0, firstHabit.getCategories().size());
        assertEquals(0, secondHabit.getCategories().size());
        verify(categoryRepository).delete(category);
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void deleteCategory_shouldThrowWhenMissing() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.deleteCategory(1L)
        );

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository, never()).delete(any());
    }

    private Category createCategory(Long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}
