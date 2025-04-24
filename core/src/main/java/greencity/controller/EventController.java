package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ImageValidation;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;


    @Operation(summary = "Update events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE,
    MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity <UpdateEventDtoResponse> update(
            @Valid @RequestPart UpdateEventDtoRequest updateEventDtoRequest,
            @Parameter(description = "Event images (JPG/PNG ≤ 10MB, max 5)")@ImageValidation
            @RequestPart(required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user){
        return ResponseEntity.ok(eventService.update(updateEventDtoRequest, images, user));
    }


}
