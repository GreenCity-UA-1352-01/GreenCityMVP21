package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilterDtoRequestMapperTest {

    private FilterDtoRequestMapper mapper;
    private UserFilterDtoRequest dto;
    private Filter entity;

    @BeforeEach
    void setUp() {
        mapper = new FilterDtoRequestMapper();
        dto = ModelUtils.getUserFilterDtoRequest();
        dto.setUserRole("ADMIN");
        dto.setName("Test");
        entity = ModelUtils.getFilter();
    }

    @Test
    void testConvert() {
        Filter actual = assertDoesNotThrow(() -> mapper.convert(dto));
        String[] actualValues = actual.getValues().split(";");
        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getSearchCriteria(), actualValues[0]);
        assertEquals(dto.getUserRole(), actualValues[1]);
        assertEquals(dto.getUserStatus(), actualValues[2]);
    }

    @Test
    void testConvert_whenNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.convert((UserFilterDtoRequest) null));
    }

    @Test
    void testConvert_whenSearchCriteriaNull() {
        dto.setSearchCriteria(null);
        assertThrows(NullPointerException.class, () -> mapper.convert(dto));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestConvertWithNull")
    void testConvert_whenOneFieldNull(String userRole, String name, String userStatus) {
        UserFilterDtoRequest item = UserFilterDtoRequest.builder()
                .searchCriteria(dto.getSearchCriteria())
                .userRole(userRole)
                .name(name)
                .userStatus(userStatus)
                .build();

        Filter actual = assertDoesNotThrow(() -> mapper.convert(item));
        String[] actualValues = actual.getValues().split(";");
        assertEquals(name, actual.getName());
        assertEquals(String.valueOf(userRole), actualValues[1]);
        assertEquals(String.valueOf(userStatus), actualValues[2]);
    }

    /**
     * Provides a stream of Arguments to test the testConvert_whenOneFieldNull method
     */
    public static Stream<Arguments> provideArgumentsForTestConvertWithNull() {
        UserFilterDtoRequest dto = ModelUtils.getUserFilterDtoRequest();

        return Stream.of(
                Arguments.of(null, dto.getName(), dto.getUserStatus()),
                Arguments.of(dto.getUserRole(), null, dto.getUserStatus()),
                Arguments.of(dto.getUserRole(), dto.getName(), null)
        );
    }

}