package com.inuteamflow.server.domain.user.dto.response;

import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.domain.user.enums.Department;
import com.inuteamflow.server.domain.user.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInfoResponse {

    private Long userId;

    private String username;

    private String email;

    private String studentNumber;

    private String name;

    private Role role;

    private Department department;

    private Boolean isSchoolVerified;

    private String imageUrl;

    public static MyInfoResponse create(
            User user,
            String imageUrl
    ) {
        return new MyInfoResponse(
              user.getUserId(),
              user.getUsername(),
              user.getEmail(),
              user.getStudentNumber(),
              user.getName(),
              user.getRole(),
              user.getDepartment(),
              user.getIsSchoolVerified(),
              imageUrl
      );
    }

}
