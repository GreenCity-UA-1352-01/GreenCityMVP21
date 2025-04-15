package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import greencity.mapping.CustomHabitMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomHabitMapperTest {

    private final CustomHabitMapper mapper = new CustomHabitMapper();

    @Test
    @DisplayName("Convert valid AddCustomHabitDtoRequest to Habit")
    void testConvertValidDto() {

        AddCustomHabitDtoRequest dto = AddCustomHabitDtoRequest.builder()
                .image("https://example.com/image.jpg")
                .complexity(2)
                .defaultDuration(30)
                .build();

        MappingContext<AddCustomHabitDtoRequest, Habit> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(dto);

        Habit result = mapper.convert(context);

        assertNotNull(result);
        assertEquals("https://example.com/image.jpg", result.getImage());
        assertEquals(2, result.getComplexity());
        assertEquals(30, result.getDefaultDuration());
        assertTrue(result.getIsCustomHabit());
    }

    @Test
    @DisplayName("Convert null AddCustomHabitDtoRequest throws NPE")
    void testConvertNullDto() {

        MappingContext<AddCustomHabitDtoRequest, Habit> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(context));
    }

    @Test
    @DisplayName("Convert AddCustomHabitDtoRequest with null fields")
    void testConvertDtoWithNullFields() {

        AddCustomHabitDtoRequest dto = AddCustomHabitDtoRequest.builder()
                .image(null)
                .complexity(null)
                .defaultDuration(null)
                .build();

        MappingContext<AddCustomHabitDtoRequest, Habit> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(dto);

        Habit result = mapper.convert(context);

        assertNotNull(result);
        assertNull(result.getImage());
        assertNull(result.getComplexity());
        assertNull(result.getDefaultDuration());
        assertTrue(result.getIsCustomHabit());
    }
}
