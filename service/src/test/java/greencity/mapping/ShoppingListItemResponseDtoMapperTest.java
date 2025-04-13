package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.entity.ShoppingListItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingListItemResponseDtoMapperTest {
    private final ShoppingListItemResponseDtoMapper mapper = new ShoppingListItemResponseDtoMapper();

    @Test
    public void testConvertFromEntityToDto() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();

        ShoppingListItemResponseDto dto = mapper.convert(entity);

        assertAll(
                () -> assertNotNull(dto),
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertNotNull(dto.getTranslations()),
                () -> assertEquals(entity.getTranslations().size(), dto.getTranslations().size())
        );
    }

    ;

    @Test
    public void testTranslationsAreMapped() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();

        ShoppingListItemResponseDto dto = mapper.convert(entity);

        for (int i = 0; i < entity.getTranslations().size(); i++) {
            assertEquals(entity.getTranslations().get(i).getId(), dto.getTranslations().get(i).getId());
            assertEquals(entity.getTranslations().get(i).getContent(), dto.getTranslations().get(i).getContent());
        }
    }
}
