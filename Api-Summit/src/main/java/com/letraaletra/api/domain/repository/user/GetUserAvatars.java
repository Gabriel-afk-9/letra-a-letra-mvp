package com.letraaletra.api.domain.repository.user;

import com.letraaletra.api.domain.avatar.Avatar;

import java.util.List;

public interface GetUserAvatars {
    List<Avatar> getAvatars(String userId);
}
