package cn.master.luna.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@Slf4j
public class FailureEvents {
    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent badCredentials) {
        AuthenticationException exception = badCredentials.getException();
        if (exception instanceof InvalidBearerTokenException) {
            log.error(exception.getMessage());
        }
    }
}
