package greencity.service;

import greencity.entity.NewsSubscription;
import greencity.repository.NewsSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service implementation for managing news subscriptions.
 * Handles the business logic for subscribing a user by email.
 * @author Saienko Volodymyr
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class NewsSubscriptionServiceImpl implements NewsSubscriptionService {

    private final NewsSubscriptionRepo subscriptionRepository;

    /**
     * Subscribes a user to the news updates by email.
     * It checks if the email is already subscribed, and if not, creates a new subscription.
     *
     * @param email the email address to subscribe
     * @throws IllegalArgumentException if the email is already subscribed
     */
    public void subscribe(String email) {

        if (subscriptionRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already subscribed");
        }

        var subscription = NewsSubscription
                .builder()
                .email(email)
                .createdAt(LocalDateTime.now())
                .build();

        subscriptionRepository.save(subscription);
    }
}