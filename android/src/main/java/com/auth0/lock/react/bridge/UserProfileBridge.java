/*
 * UserProfileBridge.java
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


import android.support.annotation.Nullable;

import com.auth0.core.UserIdentity;
import com.auth0.core.UserProfile;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class UserProfileBridge implements LockReactBridge {

    private static final String EMAIL_KEY = "email";
    private static final String ID_KEY = "userId";
    private static final String NAME_KEY = "name";
    private static final String NICKNAME_KEY = "nickname";
    private static final String CREATED_AT_KEY = "createdAt";
    private static final String PICTURE_KEY = "picture";

    private UserProfile profile;

    private final SimpleDateFormat formatter;
    public UserProfileBridge(@Nullable UserProfile profile) {
        // use ISO 8601 international standard date/time format
        this.formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        this.formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.profile = profile;
    }

    public WritableMap toMap() {
        WritableMap profileMap = null;
        if (profile != null) {
            profileMap = Arguments.createMap();
            profileMap.putString(EMAIL_KEY, profile.getEmail());
            profileMap.putString(ID_KEY, profile.getId());
            profileMap.putString(NAME_KEY, profile.getName());
            profileMap.putString(NICKNAME_KEY, profile.getNickname());
            if (profile.getCreatedAt() != null) {
                profileMap.putString(CREATED_AT_KEY, formatter.format(profile.getCreatedAt()));
            }
            profileMap.putString(PICTURE_KEY, profile.getPictureURL());
            Map<String, Object> info = new HashMap<>(profile.getExtraInfo());
            put("userMetadata", info.remove("user_metadata"), profileMap);
            put("appMetadata", info.remove("app_metadata"), profileMap);
            for (Map.Entry<String, Object> entry: info.entrySet()) {
                put(entry.getKey(), entry.getValue(), profileMap);
            }
            final WritableArray identities = Arguments.createArray();
            for (UserIdentity identity: profile.getIdentities()) {
                add(identity, identities);
            }
            profileMap.putArray("identities", identities);
        }
        return profileMap;
    }

    private void add(UserIdentity identity, WritableArray into) {
        final WritableMap map = Arguments.createMap();
        map.putString("userId", identity.getId());
        map.putString("connection", identity.getConnection());
        map.putString("provider", identity.getProvider());
        map.putBoolean("social", identity.isSocial());
        put("profileData", identity.getProfileInfo(), map);
        into.pushMap(map);
    }

    private void put(String key, Map<String, Object> map, WritableMap into) {
        if (map == null || map.isEmpty()) {
            return;
        }

        final WritableMap writableMap = Arguments.createMap();
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            put(entry.getKey(), entry.getValue(), writableMap);
        }
        into.putMap(key, writableMap);
    }

    private void put(String key, List<?> list, WritableMap into) {
        if (list == null || list.isEmpty()) {
            return;
        }

        final WritableArray array = Arguments.createArray();
        for (Object item: list) {
            if (item instanceof String) {
                array.pushString((String) item);
            }
            if (item instanceof Integer) {
                array.pushInt((Integer) item);
            }
            if (item instanceof Boolean) {
                array.pushBoolean((Boolean) item);
            }
            if (item instanceof Double) {
                array.pushDouble((Double) item);
            }
            if (item instanceof Date) {
                array.pushString(formatter.format(item));
            }
        }
        into.putArray(key, array);
    }

    private void put(String key, Object value, WritableMap map) {
        if (value instanceof String) {
            map.putString(key, (String) value);
        }
        if (value instanceof Integer) {
            map.putInt(key, (Integer) value);
        }
        if (value instanceof Boolean) {
            map.putBoolean(key, (Boolean) value);
        }
        if (value instanceof Double) {
            map.putDouble(key, (Double) value);
        }
        if (value instanceof Date) {
            map.putString(key, formatter.format(value));
        }
        if (value instanceof Map) {
            //noinspection unchecked
            put(key, (Map) value, map);
        }
        if (value instanceof List) {
            put(key, (List)value, map);
        }
    }
}
