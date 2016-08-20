/*
 * ShowOptions.java
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
import android.util.Log;

import com.auth0.lock.react.LockReactModule;
import com.auth0.lock.react.bridge.utils.OptionsHelper;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowOptions {

    private static final String TAG = ShowOptions.class.getName();

    private static final String CLOSABLE_KEY = "closable";
    private static final String DISABLE_SIGNUP = "disableSignUp";
    private static final String DISABLE_RESET_PASSWORD = "disableResetPassword";
    private static final String AUTH_PARAMS_KEY = "authParams";
    private static final String CONNECTIONS_KEY = "connections";
    private static final String USE_MAGIC_LINK_KEY = "magicLink";

    private boolean closable = false;
    private boolean disableSignUp = false;
    private boolean disableResetPassword = false;
    private boolean useMagicLink = false;
    private Map<String, Object> authParams;
    private String[] connections;
    private String connectionType = LockReactModule.CONNECTION_NATIVE;

    public ShowOptions(@Nullable ReadableMap options) {
        if (options == null) {
            return;
        }

        if (options.hasKey(CLOSABLE_KEY)) {
            closable = options.getBoolean(CLOSABLE_KEY);
            Log.d(TAG, CLOSABLE_KEY + closable);
        }

        if (options.hasKey(DISABLE_SIGNUP)) {
             disableSignUp = options.getBoolean(DISABLE_SIGNUP);
             Log.d(TAG, DISABLE_SIGNUP + disableSignUp);
        }

        if (options.hasKey(DISABLE_RESET_PASSWORD)) {
             disableResetPassword = options.getBoolean(DISABLE_RESET_PASSWORD);
             Log.d(TAG, DISABLE_RESET_PASSWORD + disableResetPassword);
        }

        if (options.hasKey(USE_MAGIC_LINK_KEY)) {
            useMagicLink = options.getBoolean(USE_MAGIC_LINK_KEY);
            Log.d(TAG, USE_MAGIC_LINK_KEY + useMagicLink);
        }

        if (options.hasKey(AUTH_PARAMS_KEY)) {
            ReadableMap reactMap = options.getMap(AUTH_PARAMS_KEY);
            authParams = OptionsHelper.convertReadableMapToMap(reactMap);
            Log.d(TAG, AUTH_PARAMS_KEY + authParams);
        }

        if (options.hasKey(CONNECTIONS_KEY)) {
            ReadableArray connections = options.getArray(CONNECTIONS_KEY);
            List<String> list = new ArrayList<>(connections.size());
            for (int i = 0; i < connections.size(); i++) {
                String connectionName = connections.getString(i);
                switch (connectionName) {
                    case LockReactModule.CONNECTION_EMAIL:
                        connectionType = LockReactModule.CONNECTION_EMAIL;
                        break;
                    case LockReactModule.CONNECTION_SMS:
                        connectionType = LockReactModule.CONNECTION_SMS;
                        break;
                }
                list.add(connectionName);
            }
            this.connections = new String[list.size()];
            this.connections = list.toArray(this.connections);
            Log.d(TAG, CONNECTIONS_KEY + list);
        }
    }

    public boolean isClosable() {
        return closable;
    }

    public boolean isDisableSignUp() {
         return disableSignUp;
    }

    public boolean isDisableResetPassword() {
         return disableResetPassword;
    }

    public boolean useMagicLink() {
        return useMagicLink;
    }

    public Map<String, Object> getAuthParams() {
        return authParams;
    }

    public String[] getConnections() {
        return connections;
    }

    public String getConnectionType() {
        return connectionType;
    }
}
