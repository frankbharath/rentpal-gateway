package com.rentpal.gateway.configuration;/*
 * @author frank
 * @created 11 Dec,2020 - 9:42 PM
 */

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class CustomOidcUserService extends OidcUserService {
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest){
        OidcUser oidcUser = super.loadUser(userRequest);
        System.out.println(oidcUser.getClaims());
        return oidcUser;
    }
}
