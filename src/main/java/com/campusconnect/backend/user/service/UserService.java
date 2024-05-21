package com.campusconnect.backend.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.campusconnect.backend.authentication.repository.AuthenticationRepository;
import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.repository.BoardImageRepository;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.board.service.BoardService;
import com.campusconnect.backend.comment.repository.CommentRepository;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.config.redis.CacheKey;
import com.campusconnect.backend.favorite.domain.Favorite;
import com.campusconnect.backend.favorite.repository.FavoriteRepository;
import com.campusconnect.backend.reply.repository.ReplyRepository;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.domain.UserImageInitializer;
import com.campusconnect.backend.user.domain.UserRole;
import com.campusconnect.backend.user.dto.request.*;
import com.campusconnect.backend.user.dto.response.*;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.email.service.EmailService;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.jwt.*;
import com.campusconnect.backend.util.validator.PasswordMatchesValidator;
import com.campusconnect.backend.util.validator.PasswordMatchesValidatorForAccountWithdrawal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final FavoriteRepository favoriteRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final BoardService boardService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordMatchesValidator passwordMatchesValidator;
    private final PasswordMatchesValidatorForAccountWithdrawal passwordMatchesValidatorForAccountWithdrawal;
    private final JwtProvider jwtProvider;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket")
    private String bucket;

    @Value("${jwt.secret-key}")
    private String secretKey;

    private static final Long accessTokenExpiredMs = 1000 * 60 * 30L;  // 30 Minutes
    private static final Long refreshTokenExpiredMs = 1000 * 60 * 2L;  // 2 minutes

    @Transactional
    public UserSignUpResponse createUser(UserSignUpRequest userSignUpRequest, MultipartFile multipartFile) throws IOException {
        // 학번, 이메일 중복, 인증 번호 유효성 검증
        checkDuplicationUser(userSignUpRequest.getStudentNumber());
        checkDuplicationEmail(userSignUpRequest.getEmail());
        validateAuthenticationCode(userSignUpRequest.getEmail(), userSignUpRequest.getAuthenticationNumber());

        // 이미지 업로드 및 URL 생성
        validateUserProfileImage(userSignUpRequest, multipartFile);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String image = userSignUpRequest.getImage();

        String name = userSignUpRequest.getName();
        String studentNumber = userSignUpRequest.getStudentNumber();
        String email = userSignUpRequest.getEmail() + "@sungkyul.ac.kr";
        String password = passwordEncoder.encode(userSignUpRequest.getPassword());
        String college = userSignUpRequest.getCollege();
        String department = userSignUpRequest.getDepartment();

        User user = User.builder()
                .image(image)
                .name(name)
                .studentNumber(studentNumber)
                .email(email)
                .password(password)
                .college(college)
                .department(department)
                .role(UserRole.USER)
                .build();

        authenticationRepository.deleteAllByEmail(email);
        log.info(image);
        userRepository.save(user);

        return UserSignUpResponse.builder()
                .userId(user.getId())
                .studentNumber(user.getStudentNumber())
                .college(user.getCollege())
                .department(user.getDepartment())
                .name(user.getName())
                .responseCode(ErrorCode.SUCCESS_SIGN_UP.getDescription())
                .build();
    }

    private void validateUserProfileImage(UserSignUpRequest userSignUpRequest, MultipartFile multipartFile) throws IOException {
        String imageUrl;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            imageUrl = s3Uploader.upload(multipartFile, "user");
        } else {
            imageUrl = UserImageInitializer.getDefaultImageUrl();
        }
        userSignUpRequest.setImage(imageUrl);
    }

    /** 중복된 이메일인지 체크한다. */
    public void checkDuplicationEmail(String email) {
        if (userRepository.isEmailPrefixDuplicated(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /** 중복된 사용자가 존재하는지 체크한다. */
    public void checkDuplicationUser(String studentNumber) {
        if (userRepository. findByStudentNumber(studentNumber).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_STUDENT_NUMBER);
        }
    }

    /** 중복된 학번인지 체크한다. */
    public void validateDuplicateStudentNumber(String studentNumber) {
        if (studentNumber.length() != 8) {
            throw new CustomException(ErrorCode.INVALID_STUDENT_NUMBER);
        }
        if (userRepository.findByStudentNumber(studentNumber).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_STUDENT_NUMBER);
        }
    }

    /** 중복된 이메일인지 체크한다. */
    public void validateDuplicateEmail(String email) {
        if (userRepository.isEmailPrefixDuplicated(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /** 인증 코드 일치 검증 (인증 확인 시)*/
    public void validateAuthenticationCode(String email, String authenticationNumber) {
        if (!authenticationRepository.isCorrectAuthenticationNumber(email, authenticationNumber)) {
            throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_CODE);
        } else {
            log.info("인증번호가 일치합니다.");
        }
    }

    /** 로그인 처리 */
    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        User findUser = userRepository.findByStudentNumber(userLoginRequest.getStudentNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.FAIL_LOGIN));

        String studentNumber = userLoginRequest.getStudentNumber();
        if (!findUser.getStudentNumber().equals(studentNumber)) {
            throw new CustomException(ErrorCode.FAIL_LOGIN);
        }

        String password = userLoginRequest.getPassword();
        if (!passwordEncoder.matches(password, findUser.getPassword())) {
            throw new CustomException(ErrorCode.FAIL_LOGIN);
        }

        String accessToken = jwtProvider.createAccessToken(studentNumber, secretKey, accessTokenExpiredMs);
        String refreshToken = jwtProvider.createRefreshToken(studentNumber, secretKey, refreshTokenExpiredMs);
        saveRefreshToken(studentNumber, refreshToken);

        return UserLoginResponse.builder()
                .studentNumber(studentNumber)
                .department(findUser.getDepartment())
                .name(findUser.getName())
                .userProfileImage(findUser.getImage())
                .responseCode(ErrorCode.SUCCESS_LOGIN.getDescription())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 로그인 시 Refresh Token을 저장
    private void saveRefreshToken(String studentNumber, String refreshToken) {
        refreshTokenRedisRepository.save(RefreshToken.builder()
                .studentNumber(studentNumber)
                .refreshToken(refreshToken)
                .remainingMilliSeconds(refreshTokenExpiredMs)
                .build());
    }

    /** 로그아웃 처리 */
    @Transactional
    @CacheEvict(value = CacheKey.STUDENT_NUMBER, key = "#studentNumber")
    public UserLogoutResponse logout(String accessToken, String refreshToken, String studentNumber) {
        Long expirationMs = jwtProvider.getAccessTokenExpiredMs(accessToken);
        refreshTokenRedisRepository.deleteById(studentNumber);
        saveLogoutAccessToken(accessToken, studentNumber, expirationMs);

        return UserLogoutResponse.builder()
                .studentNumber(studentNumber)
                .responseCode(ErrorCode.SUCCESS_LOGOUT.getDescription())
                .build();
    }

    // 로그아웃 시 이전 Access Token으로 로그인하지 못하도록 처리
    private void saveLogoutAccessToken(String accessToken, String studentNumber, Long expirationMs) {
        logoutAccessTokenRedisRepository.save(LogoutAccessToken.builder()
                        .accessToken(accessToken)
                        .studentNumber(studentNumber)
                        .remainingMilliSeconds(expirationMs)
                .build());
    }

    /** Access Token 만료 시, Refresh Token을 통해 Access Token 재발급 */
    @Transactional
    public UserReissueResponse reissue(String accessToken, String refreshToken) {
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        if (jwtProvider.isRefreshTokenExpired(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_OR_NOT_EXISTED_REFRESH_TOKEN);
        }

        if (!refreshTokenRedisRepository.existsById(studentNumber)) {
            throw new CustomException(ErrorCode.NOT_EXISTED_REFRESH_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(studentNumber, secretKey, accessTokenExpiredMs);
        return UserReissueResponse.builder()
                .studentNumber(studentNumber)
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .responseCode(ErrorCode.SUCCESS_REISSUE_ACCESS_TOKEN.getDescription())
                .build();
    }

    /** 로그인 페이지 - 비밀번호 찾기 */
    @Transactional
    public UserFindPasswordResponse findPassword(UserFindPasswordRequest userFindPasswordRequest) {
        User findUser = userRepository.findByStudentNumber(userFindPasswordRequest.getStudentNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String temporalPassword = emailService.sendTemporalPassword(userFindPasswordRequest);
        updateUserPasswordToTempPassword(findUser.getStudentNumber(), temporalPassword);

        return UserFindPasswordResponse.builder()
                .studentNumber(userFindPasswordRequest.getStudentNumber())
                .responseCode(ErrorCode.SUCCESS_TEMPORAL_PASSWORD.getDescription())
                .build();
    }

    /** 임시 비밀번호 메일 발송 후 해당 회원의 비밀번호를 임시 비밀번호로 변경 */
    public void updateUserPasswordToTempPassword(String studentNumber, String temporalPassword) {
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        findUser.updateCurrentPassword(passwordEncoder.encode(temporalPassword));
    }

    /** 마이 페이지 조회 */
    public UserMyProfileAllResponse getMyProfile(String studentNumber) {
        // 기본 프로필 영역
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        UserBasicProfileResponse basicProfileResponse = UserBasicProfileResponse.builder()
                .college(findUser.getCollege())
                .department(findUser.getDepartment())
                .studentNumber(findUser.getStudentNumber())
                .name(findUser.getName())
                .email(findUser.getEmail())
                .image(findUser.getImage())
                .build();

        List<UserBasicProfileResponse> basicProfileResponses = Collections.singletonList(basicProfileResponse);

        // 관심 상품 리스트 영역
        List<Favorite> userFavoriteList = favoriteRepository.findUserFavoriteList(findUser.getStudentNumber());

        List<MyFavoriteListResponse> myFavoriteListResponses = userFavoriteList.stream()
                .map(favorite -> MyFavoriteListResponse.builder()
                        .sellerImage(favorite.getBoard().getUser().getImage())
                        .sellerDepartment(favorite.getBoard().getUser().getDepartment())
                        .sellerName(favorite.getBoard().getUser().getName())
                        .favoriteCount(favorite.getBoard().getFavoriteCount())
                        .commentCount(favorite.getBoard().getCommentCount())
                        .boardTitle(favorite.getBoard().getTitle())
                        .tradeStatus(favorite.getBoard().getTradeStatus())
                        .build())
                .collect(Collectors.toList());
        int favoriteListCount = userFavoriteList.size();

        // 작성한 판매 게시글 리스트 영역
        List<Board> createdBoardList = boardRepository.findBoardsByStudentNumber(studentNumber);

        List<CreatedBoardListResponse> createdBoardListResponses = createdBoardList.stream()
                .map(board -> CreatedBoardListResponse.builder()
                        .tradeStatus(board.getTradeStatus())
                        .boardTitle(board.getTitle())
                        .build())
                .collect(Collectors.toList());
        int createdBoardListCount = createdBoardList.size();

        // 작성한 댓글에 대한 판매자 게시글 리스트 영역
        Integer createdAllCommentCount = commentRepository.findByStudentNumber(studentNumber);
        List<Board> boardsByUserComments = commentRepository.findBoardsByUserComments(studentNumber);

        List<MyCommentsListWithSellerBoard> myCommentsListWithSellerBoardList = boardsByUserComments.stream()
                .map(board -> {
                    Integer commentCount = commentRepository.countByBoardIdAndUserStudentNumber(board.getId(), studentNumber);
                    return MyCommentsListWithSellerBoard.builder()
                            .createdCommentCount(commentCount)
                            .sellerProfileImage(board.getUser().getImage())
                            .sellerName(board.getUser().getName())
                            .sellerDepartment(board.getUser().getDepartment())
                            .boardTitle(board.getTitle())
                            .build();
                })
                .collect(Collectors.toList());

        return UserMyProfileAllResponse.builder()
                .basicProfileResponses(basicProfileResponses)
                .favoriteListCount(favoriteListCount)
                .myFavoriteListResponses(myFavoriteListResponses)
                .createdBoardCount(createdBoardListCount)
                .createdBoardListResponses(createdBoardListResponses)
                .createdAllCommentCount(createdAllCommentCount)
                .myCommentsListWithSellerBoards(myCommentsListWithSellerBoardList)
                .build();
    }

    /** 마이 페이지 조회 - 기본 프로필 영역 수정 */
    @Transactional
    public UserBasicProfileEditResponse updateMyBasicProfile(String studentNumber,
                                                           UserBasicProfileEditRequest userBasicProfileEditRequest,
                                                           MultipartFile multipartFile) throws IOException {
        // 존재하는 사용자인지 확인
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 변경 사항이 있는지 확인
        boolean changesDetected = false;

        // 멀티파트 파일로 이미지만 변경된 경우 처리
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String imageUrl = s3Uploader.upload(multipartFile, "user");
            String previousImage = findUser.getImage().replace("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/user/", "");
            s3Uploader.deleteToUserProfileImage(previousImage);
            findUser.editProfileImage(imageUrl);
            changesDetected = true;

            // 수정 전 기존 이미지가 있고, 이를 변경하지 않는 경우
        } else if (multipartFile != null && ((findUser.getImage().replace("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/user/", "")).equals(multipartFile.getName()))) {
            findUser.editProfileImage(multipartFile.getName());
        } else {
            // 만약 기존 이미지가 있었으나 기본 프로필로 설정한다면, 버킷에서 기존에 설정되어 있던 이미지는 삭제
            if (findUser.getImage().equals(findUser.getImage())) {
                String previousImage = findUser.getImage().replace("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/user/", "");
                s3Uploader.deleteToUserProfileImage(previousImage);
            }
            findUser.setProfileImageToBasicImage();
        }

        // UserBasicProfileEditRequest에 의한 정보 변경
        findUser.editMyBasicProfile(userBasicProfileEditRequest.getCollege(),
                userBasicProfileEditRequest.getDepartment(),
                userBasicProfileEditRequest.getName());

        return UserBasicProfileEditResponse.builder()
                .userId(findUser.getId())
                .name(findUser.getName())
                .college(findUser.getCollege())
                .department(findUser.getDepartment())
                .responseCode(ErrorCode.SUCCESS_EDIT_MY_BASIC_PROFILE.getDescription())
                .build();
    }

    /** 마이 페이지 - 비밀번호 수정 */
    @Transactional
    public UserPasswordUpdateResponse updateUserPassword(String studentNumber, UserPasswordUpdateRequest userPasswordUpdateRequest) {
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        validateForPasswordUpdateProcess(userPasswordUpdateRequest, findUser);

        String encodedEditPassword = passwordEncoder.encode(userPasswordUpdateRequest.getEditPassword());
        findUser.updateCurrentPassword(encodedEditPassword);

        return UserPasswordUpdateResponse.builder()
                .userId(findUser.getId())
                .responseCode(ErrorCode.SUCCESS_UPDATE_PASSWORD.getDescription())
                .build();
    }

    private void validateForPasswordUpdateProcess(UserPasswordUpdateRequest userPasswordUpdateRequest, User findUser) {
        if (!passwordEncoder.matches(userPasswordUpdateRequest.getCurrentPassword(), findUser.getPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_CURRENT_PASSWORD);
        }

        if (!passwordMatchesValidator.isValid(userPasswordUpdateRequest, null)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_EDIT_PASSWORD);
        }
    }

    /** 마이 페이지 - 회원 탈퇴 */
    @Transactional
    public UserWithdrawalResponse withdrawalAccount(String studentNumber, UserWithdrawalRequest userWithdrawalRequest) {
        // 탈퇴할 회원정보와, 해당 회원이 작성한 게시글을 모두 조회
        User withdrawalUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<Board> boardList = boardRepository.findBoards(withdrawalUser.getId());

        // 탈퇴 전 사용자 비밀번호 확인
        validateForAccountWithdrawal(userWithdrawalRequest, withdrawalUser);

        // 본인의 관심 게시글 내역 삭제 후 관련된 게시글의 관심 수까지 최신화
        List<Long> deleteMyCheckedFavoritesAndGetRelatedToBoardIds = favoriteRepository.deleteMyCheckedFavoritesAndGetRelatedToBoardIds(withdrawalUser.getId());
        for (Long deleteMyCheckedFavoritesAndGetRelatedToBoardId : deleteMyCheckedFavoritesAndGetRelatedToBoardIds) {
            Board findBoard = boardRepository.findById(deleteMyCheckedFavoritesAndGetRelatedToBoardId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

            findBoard.decreaseFavoriteCount();
        }

        // 회원탈퇴 시, 본인이 작성한 답글 내역 모두 삭제
        replyRepository.deleteAllByMyStudentNumber(studentNumber);
        // 본인이 작성한 댓글 내역 모두 삭제하고, 삭제된 댓글의 게시글 ID의 댓글 수 최신화
        updateBoardsCommentCountAfterDeletingUserComments(studentNumber);

        // 본인의 게시글 이미지, 게시글 삭제
        boardService.deleteFromS3BucketToMultipleBoards(boardList);
        for (Board board : boardList) {
            boardImageRepository.deleteAllByBoardId(board.getId());
        }
        boardService.deleteAllBoard(withdrawalUser.getId());

        // 회원 정보 삭제
        userRepository.delete(withdrawalUser);
        String userProfileImage = withdrawalUser.getImage().replace("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/user/", "");
        s3Uploader.deleteToUserProfileImage(userProfileImage);

        return UserWithdrawalResponse.builder()
                .userId(withdrawalUser.getId())
                .responseCode(ErrorCode.SUCCESS_WITHDRAWAL_USER.getDescription())
                .build();
    }

    private void updateBoardsCommentCountAfterDeletingUserComments(String studentNumber) {
        Map<Long, Long> affectedBoardIdsWithCount = commentRepository.deleteByUserStudentNumberAndReturnBoardIds(studentNumber);

        for (Map.Entry<Long, Long> entry : affectedBoardIdsWithCount.entrySet()) {
            Long boardId = entry.getKey();
            Long deletedCommentsCount = entry.getValue();

            Board findBoard = boardRepository.findById(boardId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

            // 댓글 수 최신화
            findBoard.deleteCommentAndDecreaseCommentCount(deletedCommentsCount.intValue());
        }
    }

    private void validateForAccountWithdrawal(UserWithdrawalRequest userWithdrawalRequest, User findUser) {
        if (!passwordEncoder.matches(userWithdrawalRequest.getCurrentPassword(), findUser.getPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_CURRENT_PASSWORD);
        }

        if (!passwordMatchesValidatorForAccountWithdrawal.isValid(userWithdrawalRequest, null)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_CHECK_CURRENT_PASSWORD);
        }
    }
}
