package hwan.diary.security.jwt.principal;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * JwtAuthenticationToken contains authentication info, principal
 *
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtUserPrincipal principal;

    public JwtAuthenticationToken(JwtUserPrincipal principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return principal.getName();
    }
}
