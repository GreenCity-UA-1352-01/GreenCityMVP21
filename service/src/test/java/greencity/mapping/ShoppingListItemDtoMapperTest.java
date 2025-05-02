package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingListItemDtoMapperTest {
    private final ShoppingListItemDtoMapper mapper = new ShoppingListItemDtoMapper();

    @Test
    public void testConvertFromEntityToDto() {
        ShoppingListItemTranslation entity = ModelUtils.getShoppingListItemTranslations().get(0);

        ShoppingListItemDto result = mapper.convert(entity);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(entity.getShoppingListItem().getId(), result.getId()),
                () -> assertEquals(entity.getContent(), result.getText()),
                () -> assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus())
        );
    }
}
