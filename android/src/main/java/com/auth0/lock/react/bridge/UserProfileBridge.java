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

import com.auth0.core.UserProfile;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class UserProfileBridge implements LockReactBridge {

    private static final String EMAIL_KEY = "email";
    private static final String ID_KEY = "userId";
    private static final String NAME_KEY = "name";
    private static final String NICKNAME_KEY = "nickname";
    private static final String CREATED_AT_KEY = "createdAt";

    private UserProfile profile;

    public UserProfileBridge(@Nullable UserProfile profile) {
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
                // use ISO 8601 international standard date/time format
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                profileMap.putString(CREATED_AT_KEY, simpleDateFormat.format(profile.getCreatedAt()));
            }
        }
        return profileMap;
    }
}
