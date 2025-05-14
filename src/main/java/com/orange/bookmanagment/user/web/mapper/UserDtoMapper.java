package com.orange.bookmanagment.user.web.mapper;

import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.web.model.UserDto;
import org.springframework.stereotype.Component;

/**
 * Maper odpowiedzialny za konwersję encji {@link User} do obiektu DTO {@link UserDto},
 * który może być bezpiecznie przesyłany do klienta w odpowiedziach HTTP.
 */
@Component
public class UserDtoMapper {

    /**
     * Konwertuje encję użytkownika do obiektu DTO.
     *
     * @param user obiekt encji {@link User}
     * @return obiekt DTO {@link UserDto}
     */
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
