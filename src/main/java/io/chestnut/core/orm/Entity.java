package io.chestnut.core.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

	String name();

	String cacheOwner() default "";

}
