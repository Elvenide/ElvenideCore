package com.elvenide.core.providers.command;

import com.elvenide.core.api.PublicAPI;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotates a {@link SubCommand} setup builder inside a {@link SubGroup}.
 * <p>
 * Usually paired with another method annotated by {@link SubCommandHandler} with the same name value.
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 * @since 25.1
 * @apiNote
 * The method annotated by this annotation must have only a single parameter of type {@link SubCommandBuilder}.
 */
@PublicAPI
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
@Inherited
public @interface SubCommandSetup {
    String name();
}
