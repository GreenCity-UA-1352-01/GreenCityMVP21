package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import greencity.mapping.CategoryDtoResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryDtoResponseMapperTest {

    private final CategoryDtoResponseMapper mapper = new CategoryDtoResponseMapper();

    @Test
    @DisplayName("Convert valid Category to CategoryDtoResponse")
    void testConvertValidCategory() {

        Category category = Category.builder()
                .id(1L)
                .name("Lifestyle")
                .build();

        MappingContext<Category, CategoryDtoResponse> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(category);

        CategoryDtoResponse result = mapper.convert(context);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Lifestyle", result.getName());
    }

    @Test
    @DisplayName("Convert null Category throws NPE (as null check not in mapper)")
    void testConvertNullCategory() {

        MappingContext<Category, CategoryDtoResponse> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(context));
    }

    @Test
    @DisplayName("Convert Category with null fields")
    void testConvertCategoryWithNullFields() {

        Category category = Category.builder()
                .id(null)
                .name(null)
                .build();

        MappingContext<Category, CategoryDtoResponse> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(category);

        CategoryDtoResponse result = mapper.convert(context);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }
}