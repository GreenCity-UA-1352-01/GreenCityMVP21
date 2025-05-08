package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "Get all users friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @Parameters({
            @Parameter(name = "page", schema = @Schema(type = "int", minimum = "0", defaultValue = "0"),
                    description = "Page index you want to retrieve [0..N]. "
                            + "If page index is less than 0 or not specified then default value is used!")
    })
    @GetMapping
    public ResponseEntity<PageableDto<EcoFriendsResponse>> getAllFriends(@Parameter(hidden = true) @ApiIgnore Pageable pageable,
                                                                         @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        pageable = PageRequest.of(pageable.getPageNumber(), AppConstant.ALL_FRIENDS_RESPONSE_SIZE, pageable.getSort());
        PageableDto<EcoFriendsResponse> result = friendService.getAllFriendsForUser(userVO.getId(), pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get information about friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/friend/{friendId}")
    public ResponseEntity<EcoFriendProfileDto> getFriendProfile(@PathVariable Long friendId,
                                                                @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        EcoFriendProfileDto profile = friendService.getFriendProfile(userVO.getId(), friendId);
        return ResponseEntity.ok(profile);
    }
}
