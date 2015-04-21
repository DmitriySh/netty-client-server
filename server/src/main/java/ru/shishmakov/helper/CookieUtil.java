package ru.shishmakov.helper;

import io.netty.handler.codec.http.Cookie;

import java.net.HttpCookie;
import java.util.Collection;

/**
 * Utility class makes hash code of HTTP Cookies.
 * <br/>
 * JDK default implementation is class {@link HttpCookie} and its method {@link HttpCookie#hashCode()}.
 * Rule is a hash sum of the parameters: {@code name + domain + path}.
 * Unfortunately implementation class of interface {@link Cookie} has its own rule about hashing.
 * It includes only {@code name} parameter to make a hash.
 *
 * @author Dmitriy Shishmakov
 * @see Cookie
 */
public class CookieUtil {

    /**
     * Uses hash implementation from method {@link Cookie#hashCode()}.
     *
     * @param cookies the collection of HTTP Cookies.
     * @return a hash code value for objects of {@link Cookie}.
     */
    public static int buildHash(final Collection<Cookie> cookies) {
        int hash = 0;
        for (Cookie cookie : cookies) {
            hash += cookie.hashCode();
        }
        return hash;
    }
}
