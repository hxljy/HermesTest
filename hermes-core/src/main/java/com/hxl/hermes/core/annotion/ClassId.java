package com.hxl.hermes.core.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassId {
    String value();
}
