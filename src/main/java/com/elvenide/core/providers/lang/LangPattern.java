package com.elvenide.core.providers.lang;

import com.elvenide.core.api.PublicAPI;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * ElvenideCore lang keys must follow the regex pattern: <b>[a-z0-9_.]+</b>.
 * @since 0.0.8
 */
@PublicAPI
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ METHOD, FIELD, PARAMETER, LOCAL_VARIABLE })
public @Pattern("[a-z0-9_.]+") @interface LangPattern {
}
