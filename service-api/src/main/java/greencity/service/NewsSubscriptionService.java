package greencity.service;

/**
 * Service interface for managing news subscriptions.
 * Defines the contract for subscribing a user by email.
 */
public interface NewsSubscriptionService {

    /**
     * Subscribes a user to the news updates by their email address.
     *
     * @param email the email address to subscribe
     * @throws IllegalArgumentException if the email is already subscribed
     */
    void subscribe(String email);
}
