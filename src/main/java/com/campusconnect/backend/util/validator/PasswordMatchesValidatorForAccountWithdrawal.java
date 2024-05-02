package com.campusconnect.backend.util.validator;

import com.campusconnect.backend.user.dto.request.UserWithdrawalRequest;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatchesValidatorForAccountWithdrawal implements ConstraintValidator<PasswordMatches, UserWithdrawalRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(UserWithdrawalRequest userWithdrawalRequest, ConstraintValidatorContext constraintValidatorContext) {

        if (!userWithdrawalRequest.getCurrentPassword().equals(userWithdrawalRequest.getCheckCurrentPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_CHECK_CURRENT_PASSWORD);
        }
        return true;
    }
}
