package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import greencity.mapping.CategoryDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryDtoMapperTest {

    private final CategoryDtoMapper mapper = new CategoryDtoMapper();

    @Test
    @DisplayName("Convert valid CategoryDto to Category")
    void testConvertCategoryDtoToCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setName("Eco");

        MappingContext<CategoryDto, Category> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(dto);

        Category result = mapper.convert(context);

        assertNotNull(result);
        assertEquals("Eco", result.getName());
    }

    @Test
    @DisplayName("Convert null CategoryDto returns NPE (expected due to lack of null-check)")
    void testConvertNullCategoryDto() {

        MappingContext<CategoryDto, Category> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(context));
    }

    @Test
    @DisplayName("Convert empty CategoryDto")
    void testConvertEmptyCategoryDto() {

        CategoryDto emptyDto = new CategoryDto();
        MappingContext<CategoryDto, Category> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(emptyDto);

        Category result = mapper.convert(context);

        assertNotNull(result);
        assertNull(result.getName());
    }
}