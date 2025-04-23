package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomShoppingListMapperTest {

    private CustomShoppingListMapper mapper;
    private CustomShoppingListItemResponseDto dto;
    private CustomShoppingListItem entity;

    @BeforeEach
    void setUp() {
        mapper = new CustomShoppingListMapper();
        dto = ModelUtils.getCustomShoppingListItemResponseDto();
        entity = ModelUtils.getCustomShoppingListItem();
    }

    @Test
    void testConvert() {
        CustomShoppingListItem actual = assertDoesNotThrow(() -> mapper.convert(dto));
        assertEquals(entity.getId(), actual.getId());
        assertEquals(entity.getText(), actual.getText());
        assertEquals(entity.getStatus(), actual.getStatus());
    }

    @Test
    void testConvert_whenNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.convert((CustomShoppingListItemResponseDto) null));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestConvertWithNull")
    void testConvert_whenOneFieldNull(Long id, String text, ShoppingListItemStatus status) {
        CustomShoppingListItemResponseDto item = CustomShoppingListItemResponseDto.builder()
                .id(id)
                .text(text)
                .status(status)
                .build();

        CustomShoppingListItem actual = assertDoesNotThrow(() -> mapper.convert(item));
        assertEquals(id, actual.getId());
        assertEquals(text, actual.getText());
        assertEquals(status, actual.getStatus());
    }

    @Test
    void testMapAllToList() {
        List<CustomShoppingListItemResponseDto> items = List.of(dto, dto, dto);

        List<CustomShoppingListItem> actual =
                assertDoesNotThrow(() -> mapper.mapAllToList(items));
        actual.forEach(item -> assertEquals(entity, item));
    }

    @Test
    void testMapAllToList_whenNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.mapAllToList(null));
    }

    @Test
    void testMapAllToList_whenEmptyList() {
        List<CustomShoppingListItem> actual =
                assertDoesNotThrow(() -> mapper.mapAllToList(Collections.emptyList()));
        assertTrue(actual.isEmpty());
    }

    /**
     * Provides a stream of Arguments to test the testConvert_whenOneFieldNull method
     */
    public static Stream<Arguments> provideArgumentsForTestConvertWithNull() {
        CustomShoppingListItemResponseDto dto = ModelUtils.getCustomShoppingListItemResponseDto();

        return Stream.of(
                Arguments.of(null, dto.getText(), dto.getStatus()),
                Arguments.of(dto.getId(), null, dto.getStatus()),
                Arguments.of(dto.getId(), dto.getText(), null)
        );
    }

}