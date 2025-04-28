package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * Finds users that are not friends of current user yet.
     *
     * @param name               ordered letters sequence of name of user to search
     * @param isTheSameCity      if true, limits the search to users from the same city as the requester
     * @param isFriendsOfFriends if true, limits the search to users who are friends of the requester's friends
     * @param user               current user
     * @author Rostyslav Zadyraichuk
     */
    @Operation(summary = "Get users that are not friends of current user yet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @Parameters({
        @Parameter(name = "name", schema = @Schema(type = "string", pattern = "^[a-zA-Zа-яА-Я. ]{1,30}$"),
            description = "Must be 1–30 characters long. "
                + "Only alphabetic letters (A–Z, a–z), dots (.), and spaces are allowed. "
                + "The input will be used to generate an ordered character search pattern."),
        @Parameter(name = "theSameCity", schema = @Schema(type = "boolean", defaultValue = "false"),
            description = "If true, limits the search to users from the same city as the requester."),
        @Parameter(name = "friendsOfFriends", schema = @Schema(type = "boolean", defaultValue = "false"),
            description = "If true, limits the search to users who are friends of the requester's friends."),
        @Parameter(name = "page", schema = @Schema(type = "int", minimum = "0", defaultValue = "0"),
            description = "Page index you want to retrieve [0..N]. "
                + "If page index is less than 0 or not specified then default value is used!")
    })
    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableDto<SearchFriendDtoResponse>> searchFriends(
        @RequestParam(value = "name") @Valid @Pattern(regexp = "^[a-zA-Zа-яА-Я. ]{1,30}$") String name,
        @RequestParam(value = "theSameCity", required = false, defaultValue = "false") Boolean isTheSameCity,
        @RequestParam(value = "friendsOfFriends", required = false, defaultValue = "false") Boolean isFriendsOfFriends,
        @Parameter(hidden = true) @ApiIgnore Pageable pageable,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        pageable = PageRequest.of(pageable.getPageNumber(), AppConstant.FRIENDS_RESPONSE_SIZE, pageable.getSort());
        return ResponseEntity.status(HttpStatus.OK)
            .body(friendService.searchNewFriends(name, isTheSameCity, isFriendsOfFriends, user, pageable));
    }

    /**
     * Adds a user friend.
     *
     * <p>
     * This method is idempotent, so if the friend is already added, then the method will do nothing.
     *
     * @param friendId the ID of the user to add as a friend
     *
     * @author Rostyslav Zadyraichuk
     */
    @Operation(summary = "Add new user friend")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @Parameter(name = "friendId", description = "Friend's id. Cannot be empty and must be greater than 0.")
    @PostMapping("/{friendId}")
    public void addFriend(@PathVariable("friendId") @Valid @Positive Long friendId,
                          @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        friendService.addFriend(currentUser.getId(), friendId);
    }
}
