package com.elvenide.core.providers.lang;

import com.elvenide.core.api.PublicAPI;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * ElvenideCore lang key placeholders must follow the regex pattern: <b>%([0-9]+[$]|&lt;)*[-#+ 0,(]*[0-9]*([.][0-9]+)?[nbBhHsScCfdoxXeEgGaA]|%[tT][a-zA-Z]|{}</b>
 * <p></p>
 * In other words, it must be a valid <code>String.format()</code> placeholder or a custom <code>{}</code> placeholder.
 * @since 0.0.8
 */
@PublicAPI
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ METHOD, FIELD, PARAMETER, LOCAL_VARIABLE })
public @Pattern("%([0-9]+[$]|<)*[-#+ 0,(]*[0-9]*([.][0-9]+)?[nbBhHsScCfdoxXeEgGaA]|%[tT][a-zA-Z]|\\{}") @interface LangPlaceholderPattern {
}
