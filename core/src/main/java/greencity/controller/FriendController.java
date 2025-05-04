package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * Deletes the friendship between the current user and the specified friend.
     *
     * @param friendId the ID of the friend to be removed
     * @param userVO   the current authenticated user (injected automatically)
     * @return HTTP 200 if the friendship was deleted,
     *         or HTTP 404 if no such friendship exists
     */
    @DeleteMapping("/{friendId}")
    @Operation(
            summary = "Delete a friendship between two users",
            description = "Removes the friendship connection between the current authenticated user and the user with the given friendId"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content),
            @ApiResponse(responseCode = "500", description = HttpStatuses.INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Void> removeFriend(
            @PathVariable Long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO
    ) {
        friendService.removeFriend(userVO.getId(), friendId);
        return ResponseEntity.ok().build();
    }

}
