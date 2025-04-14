package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomShoppingListMapperTest {

    private static CustomShoppingListMapper mapper;
    private static CustomShoppingListItemResponseDto dto;
    private static CustomShoppingListItem entity;

    @BeforeAll
    static void setUp() {
        mapper = new CustomShoppingListMapper();
        dto = ModelUtils.getCustomShoppingListItemResponseDto();
        entity = ModelUtils.getCustomShoppingListItem();
    }

    @Test
    void testConvert() {
        CustomShoppingListItem actual = assertDoesNotThrow(() -> mapper.convert(dto));
        assertEquals(entity, actual);
    }

    @Test
    void testMapAllToList() {
        List<CustomShoppingListItemResponseDto> items = List.of(dto, dto, dto);

        List<CustomShoppingListItem> actual =
                assertDoesNotThrow(() -> mapper.mapAllToList(items));
        actual.forEach(item -> assertEquals(entity, item));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestConvertWithNull")
    void testConvert_whenOneFieldNull(Long id, String text, ShoppingListItemStatus status) {
        CustomShoppingListItemResponseDto item = CustomShoppingListItemResponseDto.builder()
                .id(id)
                .text(text)
                .status(status)
                .build();

        assertDoesNotThrow(() -> mapper.convert(item));
    }

    /**
     * Provides a stream of Arguments to test the testConvert_whenOneFieldNull method
     */
    public static Stream<Arguments> provideArgumentsForTestConvertWithNull() {
        return Stream.of(
                Arguments.of(null, dto.getText(), dto.getStatus()),
                Arguments.of(dto.getId(), null, dto.getStatus()),
                Arguments.of(dto.getId(), dto.getText(), null)
        );
    }

}