package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.friend.FriendCardDtoResponse;
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
    public ResponseEntity<List<FriendCardDtoResponse>> getAllFriends(@Parameter(hidden = true) @CurrentUser UserVO user) {
        List<FriendCardDtoResponse> friends = friendService.getAllFriendsForUser(user.getId());
        return ResponseEntity.ok(friends);
    }
}
