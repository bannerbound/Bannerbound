package me.bannerbound.com.api.managers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TickManagerListenerID {
    String value();

    TickManagerListenerSide side() default TickManagerListenerSide.BOTH;
}
