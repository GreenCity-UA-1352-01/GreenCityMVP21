package greencity.controller;

import greencity.service.NewsSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing news subscriptions.
 * Provides an endpoint to subscribe users by their email addresses.
 * @author Saienko Volodymyr
 * @version 1.0
 */
@RestController
@RequestMapping("/news_subscription")
@RequiredArgsConstructor
@Tag(name = "News Subscription", description = "API for subscribing users to news updates")
public class NewsSubscriptionController {

    private final NewsSubscriptionService newsSubscriptionService;

    /**
     * Subscribes a user to the news updates by email.
     *
     * @param email the email address to subscribe
     * @return 200 OK if subscription is successful, or 400 Bad Request if an error occurs
     */
    @PostMapping
    @Operation(summary = "Subscribe to news", description = "Subscribe a user to news updates by providing an email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscribed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email or already subscribed")
    })
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        try {
            newsSubscriptionService.subscribe(email);
            return ResponseEntity.ok("Subscribed successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}