package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.NotifyUser;
import greencity.annotations.ValidEventImages;
import greencity.constant.HttpStatuses;
import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Create a new event")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CreateEventDtoResponse> createEvent(
            @Valid @RequestPart("event") CreateEventDto createEventDto,
            @Parameter(description = "Event images (JPG/PNG ≤ 10MB, max 5)")
            @ValidEventImages
            @RequestPart(required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {

        CreateEventDtoResponse response = eventService.createEvent(createEventDto, images, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Update events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UpdateEventDtoResponse> update(
            @Valid @RequestPart UpdateEventDtoRequest updateEventDtoRequest,
            @Parameter(description = "Event images (JPG/PNG ≤ 10MB, max 5)")
            @ValidEventImages
            @RequestPart(required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.ok(eventService.updateEvent(updateEventDtoRequest, images, user));
    }


    /**
     * Deletes an event by its ID.
     * Only the event organizer or an admin can delete the event.
     *
     * @param id   ID of the event to delete
     * @param user currently authenticated user
     * @return HTTP 200 if deleted successfully
     */
    @Operation(summary = "Delete event")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @CurrentUser UserVO user) {
        eventService.deleteById(id, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Cancel event")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/cancel/{id}")
    @NotifyUser
    public ResponseEntity<Void> cancelEvent(@PathVariable Long id, @Parameter(hidden = true)
    @CurrentUser UserVO user, @RequestParam String reason) {
        eventService.cancelEventById(id, user, reason);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Subscribe to event")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/subscribe/{id}")
    @NotifyUser
    public ResponseEntity<Void> subscribeEvent(@PathVariable Long id, @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventService.attendEvent(id, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Accept attender to event")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("{eventId}/accept-attender/{userId}")
    @NotifyUser
    public ResponseEntity<Void> acceptAttenderToEvent(@PathVariable Long eventId, @PathVariable Long userId, @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventService.acceptAttenderToEvent(eventId, userId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
