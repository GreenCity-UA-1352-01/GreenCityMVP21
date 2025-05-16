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

class CustomShoppingListResponseDtoMapperTest {

    private CustomShoppingListResponseDtoMapper mapper;
    private CustomShoppingListItemResponseDto dto;
    private CustomShoppingListItem entity;

    @BeforeEach
    void setUp() {
        mapper = new CustomShoppingListResponseDtoMapper();
        dto = ModelUtils.getCustomShoppingListItemResponseDto();
        entity = ModelUtils.getCustomShoppingListItem();
    }

    @Test
    void testConvert() {
        CustomShoppingListItemResponseDto actual =
                assertDoesNotThrow(() -> mapper.convert(entity));
        assertEquals(dto.getId(), actual.getId());
        assertEquals(dto.getText(), actual.getText());
        assertEquals(dto.getStatus(), actual.getStatus());
    }

    @Test
    void testConvert_whenNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.convert((CustomShoppingListItem) null));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestConvertWithNull")
    void testConvert_whenOneFieldNull(Long id, String text, ShoppingListItemStatus status) {
        CustomShoppingListItem item = CustomShoppingListItem.builder()
                .id(id)
                .text(text)
                .status(status)
                .build();

        CustomShoppingListItemResponseDto actual = assertDoesNotThrow(() -> mapper.convert(item));
        assertEquals(id, actual.getId());
        assertEquals(text, actual.getText());
        assertEquals(status, actual.getStatus());
    }

    @Test
    void testMapAllToList() {
        List<CustomShoppingListItem> items = List.of(entity, entity, entity);

        List<CustomShoppingListItemResponseDto> actual =
                assertDoesNotThrow(() -> mapper.mapAllToList(items));
        actual.forEach(item -> assertEquals(dto, item));
    }

    @Test
    void testMapAllToList_whenNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.mapAllToList(null));
    }

    @Test
    void testMapAllToList_whenEmptyList() {
        List<CustomShoppingListItemResponseDto> actual =
                assertDoesNotThrow(() -> mapper.mapAllToList(Collections.emptyList()));
        assertTrue(actual.isEmpty());
    }

    /**
     * Provides a stream of Arguments to test the testConvert_whenOneFieldNull method
     */
    public static Stream<Arguments> provideArgumentsForTestConvertWithNull() {
        CustomShoppingListItem entity = ModelUtils.getCustomShoppingListItem();

        return Stream.of(
                Arguments.of(null, entity.getText(), entity.getStatus()),
                Arguments.of(entity.getId(), null, entity.getStatus()),
                Arguments.of(entity.getId(), entity.getText(), null)
        );
    }

}