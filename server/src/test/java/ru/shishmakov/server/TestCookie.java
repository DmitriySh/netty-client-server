package ru.shishmakov.server;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import org.junit.Assert;
import org.junit.Test;
import ru.shishmakov.helper.CookieUtil;

import java.util.Arrays;
import java.util.List;

/**
 * JUnit test utilizing class {@link CookieUtil}.
 *
 * @author Dmitriy Shishmakov
 */
public class TestCookie extends TestBase {

    /**
     * Tests hash code values for multiple Cookies.
     * Hash code values of differently ordered cookies should be the equal.
     */
    @Test
    public void testHashCode() {
        final Cookie cookie1 = new DefaultCookie("name1", "value1");
        final Cookie cookie2 = new DefaultCookie("name2", "value2");
        final List<Cookie> cookieList1 = Arrays.asList(cookie1, cookie2);
        final List<Cookie> cookieList2 = Arrays.asList(cookie2, cookie1);

        final int hash1 = CookieUtil.buildHash(cookieList1);
        final int hash2 = CookieUtil.buildHash(cookieList2);
        logger.info("Cookie 1: name={}; value={}", cookie1.getName(), cookie1.getValue());
        logger.info("Cookie 2: name={}; value={}", cookie2.getName(), cookie2.getValue());
        Assert.assertEquals("Differently ordered collections of cookies have built unequal hash codes", hash1, hash2);
    }

}
