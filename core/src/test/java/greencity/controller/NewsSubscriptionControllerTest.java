package greencity.controller;

import greencity.service.NewsSubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NewsSubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NewsSubscriptionService newsSubscriptionService;

    @InjectMocks
    private NewsSubscriptionController newsSubscriptionController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsSubscriptionController)
                .build();
    }


    @Test
    public void shouldSubscribeSuccessfully() throws Exception {
        String email = "test@example.com";
        doNothing().when(newsSubscriptionService).subscribe(email);

        String jsonBody = """
                {
                    "email": "%s"
                }
                """.formatted(email);

        mockMvc.perform(post("/news_subscription")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenEmailAlreadySubscribed() throws Exception {
        String email = "test@example.com";
        doThrow(new IllegalArgumentException("Email already subscribed")).when(newsSubscriptionService).subscribe(email);

        String jsonBody = """
                {
                    "email": "%s"
                }
                """.formatted(email);

        mockMvc.perform(post("/news_subscription")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already subscribed"));
    }
}