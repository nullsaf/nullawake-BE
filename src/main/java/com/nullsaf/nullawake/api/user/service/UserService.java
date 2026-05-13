package com.nullsaf.nullawake.api.user.service;

import com.nullsaf.nullawake.api.auth.service.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    public UserInfoResponse getMyInfo(String authorizationHeader) {
        Long userId = getUserIdFromHeader(authorizationHeader);
        Users user = getActiveUser(userId);

        return UserInfoResponse.from(user);
    }

    @Transactional
    public UserInfoResponse updateMyInfo(
            String authorizationHeader,
            UserUpdateRequest request
    ) {
        Long userId = getUserIdFromHeader(authorizationHeader);
        Users user = getActiveUser(userId);

        user.updateProfile(request.getNickname(), request.getEmail());

        return UserInfoResponse.from(user);
    }

    @Transactional
    public void deleteMyInfo(String authorizationHeader) {
        Long userId = getUserIdFromHeader(authorizationHeader);
        Users user = getActiveUser(userId);

        user.delete();
    }

    private Users getActiveUser(Long userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Long getUserIdFromHeader(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);

        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return jwtTokenProvider.getUserId(token);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return authorizationHeader.substring(7);
    }
}
