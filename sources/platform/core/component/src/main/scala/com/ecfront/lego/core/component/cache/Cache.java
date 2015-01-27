package com.ecfront.lego.core.component.cache;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    boolean useResouce() default true;

    boolean useAppId() default true;

    boolean useUserId() default true;

    long expire() default 86400000; //one day

}
