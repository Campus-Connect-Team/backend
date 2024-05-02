package com.campusconnect.backend.user.dto.request;

import com.campusconnect.backend.util.validator.PasswordMatches;
import lombok.Data;

@Data
@PasswordMatches
public class UserWithdrawalRequest {

    private String currentPassword;
    private String checkCurrentPassword;
}
