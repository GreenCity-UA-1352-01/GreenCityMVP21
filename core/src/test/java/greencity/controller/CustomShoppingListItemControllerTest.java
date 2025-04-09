package greencity.controller;

import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.service.CustomShoppingListItemService;
import greencity.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
//@ContextConfiguration
//@Import(SecurityConfig.class)
public class CustomShoppingListItemControllerTest {

    private static final String CONTROLLER_LINK = "/custom/shopping-list-items";

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;

    private Principal principal = getPrincipal();
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    public void getAllAvailableCustomShoppingListItems() {

    }

    @Test
    public void saveUserCustomShoppingListItems() {

    }

    @Test
    public void updateItemStatus() {

    }

    @Test
    public void updateItemStatusToDone() {

    }

    @Test
    public void bulkDeleteCustomShoppingListItems() {

    }

    @Test
    public void getAllCustomShoppingItemsByStatus() {

    }

}
