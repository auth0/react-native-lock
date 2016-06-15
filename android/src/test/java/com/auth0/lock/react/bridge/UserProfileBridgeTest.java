/*
 * UserProfileBridgeTest.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.lock.react.bridge;

import com.auth0.core.UserProfile;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SimpleArray;
import com.facebook.react.bridge.SimpleMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/*
    If we need to test something that uses a native lib, for example Arguments.createMap() we'll
    have to add this before the class declaration:

    @PrepareForTest({Arguments.class})
    @PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
    @RunWith(RobolectricTestRunner.class)

    and inside the class:

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void prepareModules() {
        PowerMockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray()).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return new SimpleArray();
                    }
                });

        Mockito.when(Arguments.createMap()).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return new SimpleMap();
                    }
                });
    }
 */

//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)

@PrepareForTest({Arguments.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@RunWith(RobolectricTestRunner.class)
public class UserProfileBridgeTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void prepareModules() {
        PowerMockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray()).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return new SimpleArray();
                    }
                });

        Mockito.when(Arguments.createMap()).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return new SimpleMap();
                    }
                });
    }

    @Test
    public void testAll() throws Exception {
        Map<String, Object> userProfileMap = new HashMap<>();
        userProfileMap.put("email", "email-value");
        userProfileMap.put("user_id", "id-value");
        userProfileMap.put("name", "name-value");
        userProfileMap.put("nickname", "nickname-value");
        userProfileMap.put("picture", "picture-value");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        userProfileMap.put("created_at", sdf.format(now));
        userProfileMap.put("user_metadata", Collections.singletonMap("role", "admin"));
        UserProfile userProfile = new UserProfile(userProfileMap);

        UserProfileBridge userProfileBridge = new UserProfileBridge(userProfile);
        ReadableMap map = userProfileBridge.toMap();

        assertThat(map.getString("email"), is(equalTo("email-value")));
        assertThat(map.getString("userId"), is(equalTo("id-value")));
        assertThat(map.getString("name"), is(equalTo("name-value")));
        assertThat(map.getString("nickname"), is(equalTo("nickname-value")));
        assertThat(map.getString("createdAt"), is(equalTo(sdf.format(now))));
        assertThat(map.getString("picture"), is(equalTo("picture-value")));
        assertThat(map.getMap("userMetadata"), is(notNullValue()));
    }
}