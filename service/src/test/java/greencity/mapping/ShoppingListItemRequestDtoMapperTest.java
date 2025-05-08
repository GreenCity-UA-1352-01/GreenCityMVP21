package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingListItemRequestDtoMapperTest {
    private final ShoppingListItemRequestDtoMapper mapper = new ShoppingListItemRequestDtoMapper();

    @Test
    public void testConvertFromDtoToEntity() {
        ShoppingListItemRequestDto dto = new ShoppingListItemRequestDto();
        dto.setId(ModelUtils.getUserShoppingListItem().getId());

        UserShoppingListItem result = mapper.convert(dto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(dto.getId(), result.getShoppingListItem().getId()),
                () -> assertEquals(ShoppingListItemStatus.ACTIVE, result.getStatus())
        );
    }
}
