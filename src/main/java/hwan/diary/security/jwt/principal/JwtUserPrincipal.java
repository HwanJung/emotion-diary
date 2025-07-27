package hwan.diary.security.jwt.principal;

/**
 * Represent Authenticated user info(id)
 * Spring Security use this principal to identify the authenticated user
 *
 * @param id
 */
public record JwtUserPrincipal(
    Long id
) { }
