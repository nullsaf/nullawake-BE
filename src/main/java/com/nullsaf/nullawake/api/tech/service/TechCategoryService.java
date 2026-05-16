package com.nullsaf.nullawake.api.tech.service;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.dto.TechStackSelectionRequest;
import com.nullsaf.nullawake.api.tech.dto.TechStackSelectionResponse;
import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.nullsaf.nullawake.api.tech.entity.UserTechStack;
import com.nullsaf.nullawake.api.tech.repository.techcategory.TechCategoryRepository;
import com.nullsaf.nullawake.api.tech.repository.techstack.TechStackRepository;
import com.nullsaf.nullawake.api.tech.repository.usertechstack.UserTechStackRepository;
import com.nullsaf.nullawake.api.user.entity.Users;
import com.nullsaf.nullawake.api.user.repository.UserRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카테고리 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TechCategoryService {

    private final TechCategoryRepository techCategoryRepository;
    private final TechStackRepository techStackRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final UserRepository userRepository;

    /**
     * 전체 카테고리 조회 카테고리명, 각 카테고리에 포함된 스택의 개수를 조회한다,
     *
     * @return 카테고리 리스트
     */
    @Transactional(readOnly = true)
    public TechCategoryResponse getTechCategories() {
        try {
            List<TechCategoryDto> techCategoryDtoList =
                techCategoryRepository.findActiveCategoriesWithStackCount();

            log.info("[TechCategoryService] 활성 기술 카테고리 목록 조회 완료 - categoryCount={}",
                techCategoryDtoList.size());

            return new TechCategoryResponse(techCategoryDtoList);
        } catch (Exception e) {
            log.error("[TechCategoryService] 기술 카테고리 조회 중 예외 발생", e);
            throw new CustomException(ErrorCode.TECH_CATEGORY_QUERY_FAILED);
        }
    }

    /**
     * 카테고리별 스택 리스트 조회
     *
     * @param userId
     * @param techCategoryId
     * @return 카테고리 별 스택 리스트
     */
    @Transactional(readOnly = true)
    public TechStackListResponse getTechStacksByCategory(Long userId, Long techCategoryId) {
        log.info("[TechCategoryService] 카테고리별 기술 스택 조회 시작 - userId={}, techCategoryId={}",
            userId, techCategoryId);

        try {
            TechStackListResponse response =
                techCategoryRepository.findTechStacksByCategoryIdAndUserId(techCategoryId, userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TECH_CATEGORY_NOT_FOUND));

            log.info("[TechCategoryService] 카테고리별 기술 스택 조회 완료 - userId={}, categoryId={}, stackCount={}",
                userId,
                techCategoryId,
                response.techStackList().size()
            );

            return response;

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[TechCategoryService] 기술 스택 조회 중 예외 발생 - userId={}, categoryId={}",
                userId, techCategoryId, e);
            throw new CustomException(ErrorCode.TECH_STACK_QUERY_FAILED);
        }
    }

    /**
     * 사용자가 선택한 기술 스택 저장
     * @param userId 사용자 ID
     * @param techCategoryId 카테고리 ID
     * @param request 사용자가 선택한 기술 스택
     * @return TechStackSelectionResponse
     */
    @Transactional
    public TechStackSelectionResponse saveSelectedTechStacks(
        Long userId, Long techCategoryId, TechStackSelectionRequest request
    ) {
        try {
            // request 에서 techStackId 추출
            List<Long> techStackIds = request.techStackList().stream()
                .map(TechStackSelectionRequest.TechStackSelection::techStackId)
                .distinct()
                .toList();

            // 기존 categoryId와 연관된 스택 전체 삭제 (사용자 별)
            userTechStackRepository.deleteByUserIdAndCategoryId(userId, techCategoryId);

            // techStackId가 비어있다면 올바르지 않은 요청
            if (techStackIds.isEmpty()) {
                log.warn("[TechCategoryService] 유효하지 않은 기술 스택 Request 형식 - userId = {}, requestedStackCount={}", userId, techStackIds.size());
                throw new CustomException(ErrorCode.TECH_STACK_INVALID_REQUEST);
            }

            // 실제 활성화된 기술 스택 리스트
            List<TechStack> techStacks =
                techStackRepository.findActiveTechStacksByIdsAndCategoryId(
                    techStackIds,
                    techCategoryId
                );


            if (techStacks.size() != techStackIds.size()) {
                log.warn("[TechCategoryService] 유효하지 않은 기술 스택 포함 - userId={}, techCategoryId={}, requestedCount={}, foundCount={}",
                    userId,
                    techCategoryId,
                    techStackIds.size(),
                    techStacks.size()
                );

                throw new CustomException(ErrorCode.TECH_STACK_NOT_FOUND);
            }


            // selected 여부 Map 생성
            Map<Long, Boolean> selectedMap = request.techStackList().stream()
                .collect(Collectors.toMap(
                    TechStackSelectionRequest.TechStackSelection::techStackId,
                    TechStackSelectionRequest.TechStackSelection::selected,
                    (existing, replacement) -> replacement
                ));


            // 사용자 프록시 조회
            Users user = userRepository.getReferenceById(userId);

            // 사용자가 선택한 기술 스택 저장
            List<UserTechStack> userTechStacks = techStacks.stream()
                .map(techStack -> UserTechStack.builder()
                    .user(user)
                    .techStack(techStack)
                    .selected(Boolean.TRUE.equals(selectedMap.get(techStack.getTechStackId())))
                    .build())
                .toList();

            userTechStackRepository.saveAll(userTechStacks);

            // 사용자가 선택한 기술 스택 개수
            int selectedCount = (int) userTechStacks.stream()
                .filter(UserTechStack::isSelected)
                .count();

            log.info("[TechCategoryService] 기술 스택 선택 저장 완료 - userId={}, techCategoryId={}, savedCount={}, selectedCount={}",
                userId,
                techCategoryId,
                userTechStacks.size(),
                selectedCount
            );

            return new TechStackSelectionResponse(techCategoryId, selectedCount);

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[TechCategoryService] 기술 스택 선택 저장 중 예외 발생 - userId={}, categoryId={}",
                userId,
                techCategoryId,
                e
            );

            throw new CustomException(ErrorCode.TECH_STACK_SELECTION_SAVE_FAILED);
        }
    }

    /**
     * 사용자가 선택한 기술 스택 수정
     * @param userId 사용자 ID
     * @param techCategoryId 카테고리 ID
     * @param request 사용자가 선택한 기술 스택
     * @return TechStackSelectionResponse
     */
    @Transactional
    public TechStackSelectionResponse updateSelectedTechStacks(
        Long userId, Long techCategoryId, TechStackSelectionRequest request
    ) {
        try {
            // request 에서 techStackId 추출
            List<Long> techStackIds = request.techStackList().stream()
                .map(TechStackSelectionRequest.TechStackSelection::techStackId)
                .distinct()
                .toList();

            // 기존 categoryId와 연관된 스택 전체 삭제 (사용자 별)
            List<TechStack> techStacks =
                techStackRepository.findActiveTechStacksByIdsAndCategoryId(
                    techStackIds,
                    techCategoryId
                );

            // techStackId가 비어있다면 올바르지 않은 요청
            if (techStackIds.isEmpty()) {
                log.warn("[TechCategoryService] 유효하지 않은 기술 스택 Request 형식 - userId = {}, requestedStackCount={}", userId, techStackIds.size());
                throw new CustomException(ErrorCode.TECH_STACK_INVALID_REQUEST);
            }

            // selected 여부 Map 생성
            Map<Long, Boolean> selectedMap = request.techStackList().stream()
                .collect(Collectors.toMap(
                    TechStackSelectionRequest.TechStackSelection::techStackId,
                    TechStackSelectionRequest.TechStackSelection::selected,
                    (oldValue, newValue) -> newValue
                ));

            // 기존 선택 데이터 조회
            List<UserTechStack> existingSelections =
                userTechStackRepository.findByUserIdAndTechStackIds(
                    userId,
                    techStackIds
                );

            // 기존 선택 데이터를 Map으로 변환
            Map<Long, UserTechStack> existingMap = existingSelections.stream()
                .collect(Collectors.toMap(
                    selection -> selection.getTechStack().getTechStackId(),
                    selection -> selection
                ));

            Users user = userRepository.getReferenceById(userId);

            List<UserTechStack> newSelections = new ArrayList<>();

            // 기존 데이터는 수정, 없으면 신규 생성
            for (TechStack techStack : techStacks) {
                Long techStackId = techStack.getTechStackId();
                boolean requestedSelected =
                    Boolean.TRUE.equals(selectedMap.get(techStackId));

                UserTechStack existing = existingMap.get(techStackId);

                if (existing != null) {
                    if (existing.isSelected() != requestedSelected) {
                        existing.updateSelected(requestedSelected);
                    }
                } else {
                    newSelections.add(
                        UserTechStack.builder()
                            .user(user)
                            .techStack(techStack)
                            .selected(requestedSelected)
                            .build()
                    );
                }
            }

            userTechStackRepository.saveAll(newSelections);

            // 사용자가 선택한 기술 스택 개수
            int selectedCount = (int) userTechStackRepository
                .findByUserIdAndTechStackIds(userId, techStackIds)
                .stream()
                .filter(UserTechStack::isSelected)
                .count();

            return new TechStackSelectionResponse(
                techCategoryId,
                selectedCount
            );

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TECH_STACK_SELECTION_UPDATE_FAILED);
        }
    }
}
