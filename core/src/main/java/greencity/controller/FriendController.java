package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<EcoFriendsResponse>> getAllFriends(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        List<EcoFriendsResponse> friends = friendService.getAllFriendsForUser(userVO.getId());
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/friend/{friendId}")
    public ResponseEntity<EcoFriendProfileDto> getFriendProfile(@PathVariable Long friendId,
                                                                @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        EcoFriendProfileDto profile = friendService.getFriendProfile(userVO.getId(), friendId);
        return ResponseEntity.ok(profile);
    }
}
