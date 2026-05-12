package com.nullsaf.nullawake.api.auth.service;

import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;

public interface OAuthClient {

    OAuthProvider getProvider();

    SocialUserInfo getUserInfo(String code);
}
