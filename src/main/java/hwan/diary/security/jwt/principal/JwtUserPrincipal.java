package hwan.diary.security.jwt.principal;

import java.security.Principal;

/**
 * Represent Authenticated user info(id)
 * Spring Security use this principal to identify the authenticated user
 *
 */
public class JwtUserPrincipal implements Principal{

    private final Long userId;

    public JwtUserPrincipal(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}
