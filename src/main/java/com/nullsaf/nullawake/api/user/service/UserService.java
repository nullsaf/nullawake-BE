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

/**
 * 유저 관련 비즈니스 로직을 처리하는 서비스.
 *
 * 유저 정보 조회, 수정, 탈퇴 기능을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 정보를 조회한다.
     *
     * @param userId 사용자 ID
     * @return 유저 정보 응답
     */
    public UserInfoResponse getMyInfo(Long userId) {
        Users user = getUser(userId);

        return UserInfoResponse.from(user);
    }

    /**
     * 유저 정보를 수정한다.
     *
     * @param userId 사용자 ID
     * @param request 유저 정보 수정 요청
     * @return 수정된 유저 정보 응답
     */
    @Transactional
    public UserInfoResponse updateMyInfo(
            Long userId,
            UserUpdateRequest request
    ) {
        Users user = getUser(userId);

        user.updateProfile(request.getNickname(), request.getEmail());

        return UserInfoResponse.from(user);
    }

    /**
     * 유저를 탈퇴 처리한다.
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteMyInfo(Long userId) {
        Users user = getUser(userId);

        user.delete();
    }

    /**
     * 사용자 ID를 기반으로 유저를 조회한다.
     *
     * @param userId 사용자 ID
     * @return 조회된 유저 엔티티
     * @throws CustomException 유저가 존재하지 않는 경우
     */
    private Users getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
