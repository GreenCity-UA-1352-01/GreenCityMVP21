package greencity.controller;

import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationsService;

    /**
     * Updates the status of a notification.
     *
     * @param updateNotificationStatusRequestDto the request body containing the notification ID and the new status
     * @return HTTP 200 OK if the status was successfully updated
     */
    @PutMapping("/status")
    @Operation(
            summary = "Update notification status",
            description = "Updates the status of a specific notification by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateNotificationStatus(@RequestBody @Valid UpdateNotificationStatusRequestDto updateNotificationStatusRequestDto) {
        notificationsService.updateNotificationStatus(
                updateNotificationStatusRequestDto.getId(),
                updateNotificationStatusRequestDto.getStatus()
        );
        return ResponseEntity.ok().build();
    }
}