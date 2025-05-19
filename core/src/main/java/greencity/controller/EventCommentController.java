package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.NotifyUser;
import greencity.constant.HttpStatuses;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @Operation(summary = "Add new/reply comment.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = EventCommentDtoResponse.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("{eventId}")
    @NotifyUser
    public ResponseEntity<EventCommentDtoResponse> save(@PathVariable Long eventId,
                                                        @Valid @RequestBody AddEventCommentDtoRequest comment,
                                                        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(eventCommentService.save(eventId, comment, user));
    }

    @Operation(summary = "Update comment.")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = EventCommentDtoResponse.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("{commentId}")
    @NotifyUser
    public ResponseEntity<EventCommentDtoResponse> save(@PathVariable Long commentId,
                                                        @Valid @RequestBody EditEventCommentDtoRequest comment,
                                                        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(eventCommentService.update(commentId, comment, user));
    }

    @Operation(summary = "Like comment of event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("{commentId}/like")
    @NotifyUser
    public ResponseEntity<Void> likeEventComment(@PathVariable("commentId") Long commentId,
                                                 @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.likeComment(commentId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Unlike comment of event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("{commentId}/like")
    public ResponseEntity<Void> unlikeEventComment(@PathVariable("commentId") Long commentId,
                                                   @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.unlikeComment(commentId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
