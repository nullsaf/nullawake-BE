package com.nullsaf.nullawake.api.user.service;

import com.nullsaf.nullawake.api.user.dto.UserInfoResponse;
import com.nullsaf.nullawake.api.user.dto.UserUpdateRequest;
import com.nullsaf.nullawake.api.user.entity.Users;
import com.nullsaf.nullawake.api.user.repository.UserRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserInfoResponse getMyInfo(Long userId) {
        Users user = getUser(userId);

        return UserInfoResponse.from(user);
    }

    @Transactional
    public UserInfoResponse updateMyInfo(
            Long userId,
            UserUpdateRequest request
    ) {
        Users user = getUser(userId);

        user.updateProfile(request.getNickname(), request.getEmail());

        return UserInfoResponse.from(user);
    }

    @Transactional
    public void deleteMyInfo(Long userId) {
        Users user = getUser(userId);

        user.delete();
    }

    private Users getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
