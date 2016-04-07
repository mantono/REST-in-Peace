package com.mantono.webserver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mantono.webserver.rest.Delete;
import com.mantono.webserver.rest.Get;
import com.mantono.webserver.rest.Post;
import com.mantono.webserver.rest.Put;

/**
 * This annotation is used by classes indicating that they act as a controller,
 * responding to any of the HTTP verbs {@link Get}, {@link Post}, {@link Put}
 * and {@link Delete}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller{}
