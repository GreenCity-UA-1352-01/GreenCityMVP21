package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.NotifyUser;
import greencity.constant.HttpStatuses;
import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.annotations.ValidEventImages;
import greencity.service.EventCommentService;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventCommentService eventCommentService;

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


    /**
     * Like an event by its ID.
     * Only for authorized users.
     *
     * @param eventId ID of the event to like
     * @param user    currently authenticated user
     * @return HTTP 200 if liked successfully
     */

    @Operation(summary = "Like an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @NotifyUser
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeEvent(@PathVariable("id") Long eventId,
                                          @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventService.likeEvent(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Unlike an event by its ID.
     * Only for authorized users.
     *
     * @param eventId ID of the event to unlike
     * @param user    currently authenticated user
     * @return HTTP 200 if unliked successfully
     */
    @Operation(summary = "Unlike an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeEvent(@PathVariable("id") Long eventId,
                                            @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventService.unlikeEvent(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Obtain event")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = EventVO.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<EventVO> getEventById(@PathVariable Long eventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.findById(eventId));
    }

    @Operation(summary = "Like comment of event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @NotifyUser
    @PostMapping("/{eventId}/comments/like/{commentId}")
    public ResponseEntity<Void> likeEventComment(@PathVariable("eventId") Long eventId,
                                                 @PathVariable("commentId") Long commentId,
                                                 @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.likeComment(user, commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
