package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.entity.ShoppingListItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingListItemResponseDtoMapperTest {
    private final ShoppingListItemResponseDtoMapper mapper = new ShoppingListItemResponseDtoMapper();

    @Test
    @DisplayName("Convert shopping list item entity to response DTO")
    void testConvertFromEntityToDto() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();

        ShoppingListItemResponseDto dto = mapper.convert(entity);

        assertAll(
                () -> assertNotNull(dto),
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertNotNull(dto.getTranslations()),
                () -> assertEquals(entity.getTranslations().size(), dto.getTranslations().size())
        );
    }

    @Test
    @DisplayName("Verify translations are correctly mapped")
    void testTranslationsAreMapped() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();

        ShoppingListItemResponseDto dto = mapper.convert(entity);

        for (int i = 0; i < entity.getTranslations().size(); i++) {
            assertEquals(entity.getTranslations().get(i).getId(), dto.getTranslations().get(i).getId());
            assertEquals(entity.getTranslations().get(i).getContent(), dto.getTranslations().get(i).getContent());
        }
    }

    @Test
    @DisplayName("Convert null shopping list item throws NPE")
    void testConvertNullEntity() {
        assertThrows(NullPointerException.class, () -> mapper.convert((ShoppingListItem) null));
    }
}
