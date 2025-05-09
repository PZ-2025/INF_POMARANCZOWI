package com.orange.bookmanagment.user.web.mapper;

import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.web.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user) {

        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserType().name(),
                user.getAvatarPath(),
                user.isVerified(),
                user.isBlocked(),
                TimeUtil.getTimeInStandardFormat(user.getCreatedAt()),
                TimeUtil.getTimeInStandardFormat(user.getUpdatedAt()),
                TimeUtil.getTimeInStandardFormat(user.getBlockedAt()),
                TimeUtil.getTimeInStandardFormat(user.getVerifiedAt())
        );
    }
}
