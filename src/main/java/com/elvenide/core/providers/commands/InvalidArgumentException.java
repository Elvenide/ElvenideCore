package com.elvenide.core.providers.commands;

import org.jetbrains.annotations.ApiStatus;

/**
 * Error thrown internally in ElvenideCore to specifically identify argument-related issues.
 */
@ApiStatus.Internal
public class InvalidArgumentException extends RuntimeException {

    InvalidArgumentException(String message, String name) {
        super(message.formatted(name));
    }

}
