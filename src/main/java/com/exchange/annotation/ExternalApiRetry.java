package com.exchange.annotation;

import com.exchange.exception.BadRequestException;
import com.exchange.exception.NotFoundException;
import com.exchange.exception.UnauthorizedException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(noRetryFor = {UnauthorizedException.class, BadRequestException.class,
    NotFoundException.class},
    maxAttemptsExpression = "${retry.external-api.max-attempts}",
    backoff = @Backoff(delayExpression = "${retry.external-api.delay}"))
public @interface ExternalApiRetry {

}
