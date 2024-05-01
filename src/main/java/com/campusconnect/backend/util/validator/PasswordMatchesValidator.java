package com.campusconnect.backend.util.validator;

import com.campusconnect.backend.user.dto.request.UserPasswordUpdateRequest;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserPasswordUpdateRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(UserPasswordUpdateRequest userPasswordUpdateRequest, ConstraintValidatorContext constraintValidatorContext) {

        if (!userPasswordUpdateRequest.getEditPassword().equals(userPasswordUpdateRequest.getCheckEditPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_EDIT_PASSWORD);
        }
        return true;
    }
}
