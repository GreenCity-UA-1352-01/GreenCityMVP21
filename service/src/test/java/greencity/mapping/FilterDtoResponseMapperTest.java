package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilterDtoResponseMapperTest {

    private FilterDtoResponseMapper mapper;
    private UserFilterDtoResponse dto;
    private Filter entity;

    @BeforeEach
    void setUp() {
        mapper = new FilterDtoResponseMapper();
        dto = ModelUtils.getUserFilterDtoResponse();
        entity = ModelUtils.getFilter();
    }

    @Test
    void testConvert() {
        UserFilterDtoResponse actual = assertDoesNotThrow(() -> mapper.convert(entity));
        String[] expectedValues = entity.getValues().split(";");

        assertEquals(entity.getId(), actual.getId());
        assertEquals(entity.getName(), actual.getName());
        assertEquals(expectedValues[0], actual.getSearchCriteria());
        assertEquals(expectedValues[1], actual.getUserRole());
        assertEquals(expectedValues[2], actual.getUserStatus());
    }

    @Test
    void testConvert_whenNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.convert((Filter) null));
    }

    @Test
    void testConvert_whenValuesNull() {
        entity.setValues(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", " ; "})
    void testConvert_whenValuesSizeLessThanThree_shouldThrow(String values) {
        entity.setValues(values);

        assertThrows(IndexOutOfBoundsException.class, () -> mapper.convert(entity));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ; ; ", " ; ; ; "})
    void testConvert_whenValuesSizeThreeOrMore_shouldMapSuccessfully(String values) {
        entity.setValues(values);

        UserFilterDtoResponse actual = assertDoesNotThrow(() -> mapper.convert(entity));
        String[] expectedValues = values.split(";");

        assertEquals(entity.getId(), actual.getId());
        assertEquals(entity.getName(), actual.getName());
        assertEquals(expectedValues[0], actual.getSearchCriteria());
        assertEquals(expectedValues[1], actual.getUserRole());
        assertEquals(expectedValues[2], actual.getUserStatus());
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestConvertWithNull")
    void testConvert_whenOneFieldNull(Long id, String name) {
        Filter item = Filter.builder()
                .id(id)
                .name(name)
                .values(entity.getValues())
                .build();

        UserFilterDtoResponse actual = assertDoesNotThrow(() -> mapper.convert(item));

        assertEquals(id, actual.getId());
        assertEquals(name, actual.getName());
        String[] expectedValues = entity.getValues().split(";");
        assertEquals(expectedValues[0], actual.getSearchCriteria());
        assertEquals(expectedValues[1], actual.getUserRole());
        assertEquals(expectedValues[2], actual.getUserStatus());
    }

    @Test
    void testConvert_whenValuesContainInvalidData() {
        entity.setValues("criteria;INVALID_ROLE;INVALID_STATUS");

        UserFilterDtoResponse actual = assertDoesNotThrow(() -> mapper.convert(entity));

        assertEquals("criteria", actual.getSearchCriteria());
        assertEquals("INVALID_ROLE", actual.getUserRole());
        assertEquals("INVALID_STATUS", actual.getUserStatus());
    }

    /**
     * Provides a stream of Arguments to test the testConvert_whenOneFieldNull method
     */
    public static Stream<Arguments> provideArgumentsForTestConvertWithNull() {
        Filter entity = ModelUtils.getFilter();

        return Stream.of(
                Arguments.of(null, entity.getName()),
                Arguments.of(entity.getId(), null)
        );
    }

}