package com.elvenide.core.providers.lang;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * ElvenideCore lang key placeholders must follow the regex pattern: <b>%[bBsScC]|%[,]*[0-9]*[.]*[0-9]*[fd]</b>.
 * @since 0.0.8
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ METHOD, FIELD, PARAMETER, LOCAL_VARIABLE })
public @Pattern("%[bBsScC]|%[,]*[0-9]*[.]*[0-9]*[fd]") @interface LangPlaceholderPattern {
}
