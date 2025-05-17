package greencity.controller;

import greencity.dto.newssubscription.SubscribeNewsRequestDto;
import greencity.service.NewsSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param request the {@link SubscribeNewsRequestDto} containing the email address to subscribe
     * @return 200 OK if subscription is successful, or 400 Bad Request if an error occurs
     */
    @PostMapping
    @Operation(summary = "Subscribe to news",
        description = "Subscribe a user to news updates by providing an email address.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscribed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email or already subscribed")
    })
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscribeNewsRequestDto request) {
        try {
            newsSubscriptionService.subscribe(request.getEmail());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}