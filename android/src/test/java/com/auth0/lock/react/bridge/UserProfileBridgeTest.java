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

import com.auth0.core.UserIdentity;
import com.auth0.core.UserProfile;
import com.auth0.lock.react.bridge.utils.Randomizer;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SimpleArray;
import com.facebook.react.bridge.SimpleMap;
import com.google.common.collect.Maps;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
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

@PrepareForTest({Arguments.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@RunWith(RobolectricTestRunner.class)
public class UserProfileBridgeTest {

    private static final String EMAIL = "email";
    private static final String USER_ID = "userId";
    private static final String NAME = "name";
    private static final String NICKNAME = "nickname";
    private static final String CREATED_AT = "createdAt";
    private static final String PICTURE = "picture";
    private static final String USER_METADATA = "userMetadata";
    private static final String INFO_AUTH0_COM = "info@auth0.com";
    private static final String APP_METADATA = "appMetadata";
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    public Randomizer randomizer;
    private SimpleDateFormat sdf;

    @Before
    public void prepareModules() {
        randomizer = new Randomizer();
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
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void shouldBridgeBasicProfile() throws Exception {
        final UserProfile profile = basic(INFO_AUTH0_COM);
        final ReadableMap map = bridge(profile);
        assertThat(map.getString(EMAIL), equalTo(profile.getEmail()));
        assertThat(map.getString(USER_ID), equalTo(profile.getId()));
        assertThat(map.getString(NAME), equalTo(profile.getName()));
        assertThat(map.getString(NICKNAME), equalTo(profile.getNickname()));
        assertThat(sdf.parse(map.getString(CREATED_AT)), equalTo(profile.getCreatedAt()));
        assertThat(map.getString(PICTURE), equalTo(profile.getPictureURL()));
    }

    @Test
    public void shouldBridgeMetadata() throws Exception {
        final HashMap<String, Object> user = Maps.newHashMap();
        user.put("first_name", "John");
        user.put("last_name", "Doe");
        user.put("friend_count", 14);

        final HashMap<String, Object> app = Maps.newHashMap();
        app.put("subscription", "bronze");

        final UserProfile profile = withMetadata(INFO_AUTH0_COM, user, app);
        final ReadableMap map = bridge(profile);

        assertThat(map.getString(EMAIL), equalTo(INFO_AUTH0_COM));

        final ReadableMap userMetadata = map.getMap(USER_METADATA);
        assertThat(userMetadata.getString("first_name"), equalTo("John"));
        assertThat(userMetadata.getString("last_name"), equalTo("Doe"));
        assertThat(userMetadata.getInt("friend_count"), equalTo(14));

        final ReadableMap appMetadata = map.getMap(APP_METADATA);
        assertThat(appMetadata.getString("subscription"), equalTo("bronze"));
    }

    @Test
    public void shouldBridgeNonBasicProfile() throws Exception {
        final UserProfile profile = withOtherRootAttributes(INFO_AUTH0_COM);
        final ReadableMap map = bridge(profile);

        assertThat(map.getString(EMAIL), equalTo(profile.getEmail()));
        assertThat(map.getInt("logins_count"), equalTo(profile.getExtraInfo().get("logins_count")));
    }

    @Test
    public void shouldBridgeIdentity() throws Exception {
        final UserProfile profile = withIdentities(INFO_AUTH0_COM, "facebook");
        final ReadableMap map = bridge(profile);

        assertThat(map.getString(EMAIL), equalTo(profile.getEmail()));

        final ReadableArray identities = map.getArray("identities");
        assertThat(identities.size(), is(1));

        final ReadableMap identityMap = identities.getMap(0);
        final UserIdentity identity = profile.getIdentities().get(0);
        assertThat(identityMap.getString("provider"), equalTo(identity.getProvider()));
        assertThat(identityMap.getString("connection"), equalTo(identity.getConnection()));
        assertThat(identityMap.getString("userId"), equalTo(identity.getId()));
        assertThat(identityMap.getBoolean("social"), equalTo(identity.isSocial()));
        assertThat(identityMap.getMap("profileData"), is(notNullValue()));
    }

    private ReadableMap bridge(UserProfile profile) {
        return new UserProfileBridge(profile).toMap();
    }

    private Map<String, Object> defaultJson(String email) {
        Map<String, Object> json = new HashMap<>();
        json.put("email", email);
        json.put("user_id", randomizer.string());
        json.put("name", randomizer.string());
        json.put("nickname", randomizer.string());
        json.put("picture", randomizer.string());
        json.put("created_at", randomizer.isoDate());
        return json;
    }

    private Map<String, Object> identityJson(String provider) {
        Map<String, Object> json = new HashMap<>();
        json.put("provider", provider);
        json.put("user_id", randomizer.string());
        json.put("connection", randomizer.string());
        json.put("isSocial", true);
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", "John Doe");
        json.put("profileData", profile);
        return json;
    }

    private UserProfile basic(String email) {
        return new UserProfile(defaultJson(email));
    }

    private UserProfile withOtherRootAttributes(String email) {
        final Map<String, Object> json = defaultJson(email);
        json.put("logins_count", randomizer.integer());
        return new UserProfile(json);
    }

    private UserProfile withIdentities(String email, String...providers) {
        final Map<String, Object> json = defaultJson(email);
        List<Map<String, Object>> identities = new ArrayList<>();
        for (String provider: providers) {
            identities.add(identityJson(provider));
        }
        json.put("identities", identities);
        return new UserProfile(json);
    }

    private UserProfile withMetadata(String email, Map<String, Object> userMetadata, Map<String, Object> appMetadata) {
        Map<String, Object> json = defaultJson(email);
        if (userMetadata != null) {
            json.put("user_metadata", userMetadata);
        }
        if (appMetadata != null) {
            json.put("app_metadata", appMetadata);
        }
        return new UserProfile(json);
    }
}