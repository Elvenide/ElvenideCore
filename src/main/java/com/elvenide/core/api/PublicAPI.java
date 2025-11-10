package com.elvenide.core.api;

import java.lang.annotation.*;

/**
 * Annotates code that is considered public API, i.e. usable in an external plugin or library.
 * Can annotate methods, constructors, fields, classes, interfaces, enums, records, record components, and other annotations.
 * <p>
 * In an ouroboros fashion, since this annotation is itself public API, it annotates itself with <code>@PublicAPI</code>.
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 */
@PublicAPI
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.RECORD_COMPONENT, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface PublicAPI {
}
