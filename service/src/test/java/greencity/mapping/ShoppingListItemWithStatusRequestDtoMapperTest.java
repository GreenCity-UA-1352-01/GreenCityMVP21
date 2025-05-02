package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.UserShoppingListItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingListItemWithStatusRequestDtoMapperTest {
    private final ShoppingListItemWithStatusRequestDtoMapper mapper = new ShoppingListItemWithStatusRequestDtoMapper();

    @Test
    public void testConvertFromDtoToEntity() {
        ShoppingListItemWithStatusRequestDto dto = new ShoppingListItemWithStatusRequestDto();
        dto.setId(ModelUtils.getUserShoppingListItem().getId());
        dto.setStatus(ModelUtils.getUserShoppingListItem().getStatus());

        UserShoppingListItem result = mapper.convert(dto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getShoppingListItem()),
                () -> assertEquals(dto.getId(), result.getShoppingListItem().getId()),
                () -> assertEquals(dto.getStatus(), result.getStatus())
        );
    }

}
