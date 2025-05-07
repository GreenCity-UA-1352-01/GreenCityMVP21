package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EcoFriendProfileDtoMapper;
import greencity.mapping.EcoFriendsResponseMapper;
import greencity.mapping.records.FriendProfileData;
import greencity.mapping.records.FriendWithMutuals;
import greencity.repository.EcoNewsRepo;
import greencity.repository.FriendRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final UserRepo userRepository;
    private final HabitAssignRepo habitAssignRepo;
    private final EcoNewsRepo ecoNewsRepository;
    private final EcoFriendsResponseMapper ecoFriendsResponseMapper;
    private final EcoFriendProfileDtoMapper ecoFriendProfileDtoMapper;

    @Override
    @Transactional(readOnly = true)
    public PageableDto<EcoFriendsResponse> getAllFriendsForUser(Long userId, Pageable pageable) {
        Page<User> friendsPage = friendRepository.findAllFriendsByUserId(userId, pageable);

        List<EcoFriendsResponse> friendDtos = friendsPage.getContent().stream()
                .map(friend -> ecoFriendsResponseMapper.convert(
                        new FriendWithMutuals(friend, countMutualFriends(userId, friend.getId()))))
                .toList();

        return new PageableDto<>(
                friendDtos,
                friendsPage.getTotalElements(),
                friendsPage.getNumber(),
                friendsPage.getTotalPages()
        );
    }

    @Override
    public EcoFriendProfileDto getFriendProfile(Long userId, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id " + friendId + " not found"));

        if (!friendRepository.isFriend(userId, friendId)) {
            throw new AccessDeniedException("User with id " + friendId + " is not your friend");
        }

        int habitsInProgress = habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(friendId);
        int habitsAcquired = friendRepository.countHabitAssignsByUserFriendIdAndStatusAcquired(friendId);
        long newsPublished = ecoNewsRepository.getAmountOfPublishedNewsByUserId(friendId);

        return ecoFriendProfileDtoMapper.convert(
                new FriendProfileData(friend, habitsInProgress, habitsAcquired, newsPublished)
        );
    }

    private int countMutualFriends(Long currentUserId, Long friendId) {
        return friendRepository.countMutualFriends(currentUserId, friendId);
    }
}
