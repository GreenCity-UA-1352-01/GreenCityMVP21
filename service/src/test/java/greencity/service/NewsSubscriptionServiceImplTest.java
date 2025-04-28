package greencity.service;

import greencity.entity.NewsSubscription;
import greencity.repository.NewsSubscriptionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsSubscriptionServiceImplTest {
    @Mock
    private NewsSubscriptionRepo subscriptionRepository;

    @InjectMocks
    private NewsSubscriptionServiceImpl newsSubscriptionService;

    @Test
    void shouldSubscribeSuccessfully() {

        String email = "test@example.com";
        newsSubscriptionService.subscribe(email);

        verify(subscriptionRepository, times(1)).save(any(NewsSubscription.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadySubscribed() {
        String email = "test@example.com";
        when(subscriptionRepository.existsByEmail(email)).thenReturn(true);

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            newsSubscriptionService.subscribe(email);
        });

        assertEquals("Email already subscribed", exception.getMessage());
    }
}
