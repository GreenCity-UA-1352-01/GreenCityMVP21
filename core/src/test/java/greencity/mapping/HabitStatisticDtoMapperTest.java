package greencity.mapping;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.enums.HabitRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HabitStatisticDtoMapperTest {

    private HabitStatisticDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HabitStatisticDtoMapper();
    }

    @Test
    void convert_shouldMapAllFieldsCorrectly() {

        HabitAssign habitAssign = mock(HabitAssign.class);
        when(habitAssign.getId()).thenReturn(42L);

        var nowDate = ZonedDateTime.now();

        HabitStatistic entity = HabitStatistic.builder()
                .id(1L)
                .amountOfItems(10)
                .createDate(nowDate)
                .habitRate(HabitRate.DEFAULT)
                .habitAssign(habitAssign)
                .build();

        HabitStatisticDto dto = mapper.convert(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(10, dto.getAmountOfItems());
        assertEquals(nowDate, dto.getCreateDate());
        assertEquals(HabitRate.DEFAULT, dto.getHabitRate());
        assertEquals(42L, dto.getHabitAssignId());
    }

    @Test
    void convert_givenNullInput_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitStatistic) null));
    }

    @Test
    void convert_givenNullHabitAssign_shouldThrowNullPointerException() {

        HabitStatistic entity = HabitStatistic.builder()
                .id(2L)
                .amountOfItems(0)
                .createDate(null)
                .habitRate(HabitRate.DEFAULT)
                .habitAssign(null)
                .build();

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @Test
    void convert_shouldReturnDtoEqualToExpected_whenAllFieldsSet() {

        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setId(55L);

        ZonedDateTime nowDate = ZonedDateTime.now();

        HabitStatistic entity = HabitStatistic.builder()
                .id(10L)
                .amountOfItems(12)
                .createDate(nowDate)
                .habitRate(HabitRate.DEFAULT)
                .habitAssign(habitAssign)
                .build();

        HabitStatisticDto expectedDto = HabitStatisticDto.builder()
                .id(10L)
                .amountOfItems(12)
                .createDate(nowDate)
                .habitRate(HabitRate.DEFAULT)
                .habitAssignId(55L)
                .build();

        HabitStatisticDto actualDto = mapper.convert(entity);

        assertEquals(expectedDto, actualDto);
    }
}