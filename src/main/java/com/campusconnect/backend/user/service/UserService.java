package com.campusconnect.backend.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.campusconnect.backend.authentication.repository.AuthenticationRepository;
import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.board.service.BoardService;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.favorite.domain.Favorite;
import com.campusconnect.backend.favorite.repository.FavoriteRepository;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.domain.UserRole;
import com.campusconnect.backend.user.dto.request.*;
import com.campusconnect.backend.user.dto.response.*;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.jwt.JwtProvider;
import com.campusconnect.backend.util.validator.PasswordMatchesValidator;
import com.campusconnect.backend.util.validator.PasswordMatchesValidatorForAccountWithdrawal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;
    private final BoardService boardService;
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

    private Long expiredMs = 1000 * 60 * 10L;

    @Transactional
    public User createUser(UserSignUpRequest userSignUpRequest, MultipartFile multipartFile) throws IOException {
        // 학번, 이메일 중복 검증
        checkDuplicationUser(userSignUpRequest.getStudentNumber());
        checkDuplicationEmail(userSignUpRequest.getEmail());

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
        return userRepository.save(user);
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
    public void validateDuplicateStudentNumber(String userStudentNumberRequest) {
        if (userRepository.findByStudentNumber(userStudentNumberRequest).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_STUDENT_NUMBER);
        }
    }

    /** 중복된 이메일인지 체크한다. */
    public void validateDuplicateEmail(String email) {
        if (userRepository.isEmailPrefixDuplicated(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /** 인증 코드 일치 검증 */
    public void validateAuthenticationCode(EmailAuthenticationRequest emailAuthenticationRequest) {
        String email = emailAuthenticationRequest.getEmail();
        String authenticationNumber = emailAuthenticationRequest.getAuthenticationNumber();

        if (!authenticationRepository.isCorrectAuthenticationNumber(email, authenticationNumber)) {
            throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_CODE);
        } else {
            log.info("인증번호가 일치합니다.");
        }
    }

    /** 로그인 처리 */
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

        String token = jwtProvider.createToken(studentNumber, secretKey, expiredMs);
        return new UserLoginResponse(findUser.getStudentNumber(), token, expiredMs);
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
                        .chatCount(favorite.getBoard().getChatCount())
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

        return UserMyProfileAllResponse.builder()
                .basicProfileResponses(basicProfileResponses)
                .favoriteListCount(favoriteListCount)
                .myFavoriteListResponses(myFavoriteListResponses)
                .createdBoardCount(createdBoardListCount)
                .createdBoardListResponses(createdBoardListResponses)
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

        validateForAccountWithdrawal(userWithdrawalRequest, withdrawalUser);
        userRepository.delete(withdrawalUser);
        String userProfileImage = withdrawalUser.getImage().replace("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/user/", "");
        s3Uploader.deleteToUserProfileImage(userProfileImage);
        boardService.deleteFromS3BucketToMultipleBoards(boardList);

        return UserWithdrawalResponse.builder()
                .userId(withdrawalUser.getId())
                .responseCode(ErrorCode.SUCCESS_WITHDRAWAL_USER.getDescription())
                .build();
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
