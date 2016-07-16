/*
 * LockReactModule.java
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

package com.auth0.lock.react;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Strategies;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.IdentityProvider;
import com.auth0.lock.Lock;
import com.auth0.lock.LockActivity;
import com.auth0.lock.LockContext;
import com.auth0.lock.passwordless.LockPasswordlessActivity;
import com.auth0.lock.react.bridge.InitOptions;
import com.auth0.lock.react.bridge.ShowOptions;
import com.auth0.lock.react.bridge.TokenBridge;
import com.auth0.lock.react.bridge.UserProfileBridge;
import com.auth0.lock.receiver.AuthenticationReceiver;
import com.auth0.util.Telemetry;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

public class LockReactModule extends ReactContextBaseJavaModule {

    private final static String TAG = LockReactModule.class.getName();

    private final static String REACT_MODULE_NAME = "Auth0LockModule";

    static final String MESSAGE_USER_SIGNED_UP = "User signed up";
    static final String MESSAGE_USER_CANCELLED = "User cancelled";

    public static final String CONNECTION_NATIVE = "default";
    public static final String CONNECTION_EMAIL = "email";
    public static final String CONNECTION_SMS = "sms";

    private final LocalBroadcastManager broadcastManager;

    Lock.Builder lockBuilder;
    Map<Strategies, IdentityProvider> providers;

    private Callback authCallback;

    AuthenticationReceiver authenticationReceiver = new AuthenticationReceiver() {
        @Override
        public void onAuthentication(@NonNull UserProfile profile, @NonNull Token token) {
            Log.d(TAG, "User " + profile.getName() + " with token " + token.getIdToken());
            authCallbackSuccess(profile, token);
        }

        @Override
        protected void onSignUp() {
            Log.i(TAG, MESSAGE_USER_SIGNED_UP);
            authCallbackError(MESSAGE_USER_SIGNED_UP);
        }

        @Override
        protected void onCancel() {
            Log.i(TAG, MESSAGE_USER_CANCELLED);
            authCallbackError(MESSAGE_USER_CANCELLED);
        }
    };

    public LockReactModule(ReactApplicationContext reactContext, Map<Strategies, IdentityProvider> providers) {
        this(reactContext, LocalBroadcastManager.getInstance(reactContext.getApplicationContext()), providers);
    }

    @SuppressWarnings("unused")
    LockReactModule(ReactApplicationContext reactApplicationContext, LocalBroadcastManager localBroadcastManager) {
        this(reactApplicationContext, localBroadcastManager, null);
    }

    LockReactModule(ReactApplicationContext reactApplicationContext, LocalBroadcastManager localBroadcastManager, Map<Strategies, IdentityProvider> providers) {
        super(reactApplicationContext);
        this.broadcastManager = localBroadcastManager;
        this.providers = providers;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return REACT_MODULE_NAME;
    }

    /**
     * This method is exported to JS but no used directly by the user. See auth0-lock.js
     * Called from JS to configure the {@link com.auth0.lock.Lock.Builder} that will be used to
     * initialize the {@link com.auth0.lock.Lock} instance.
     *
     * @param options the map with the values passed when invoked from JS. Some of the supported
     *                fields are "clientId", "domain" and "configurationDomain"
     *                @see com.auth0.lock.react.bridge.InitOptions
     */
    @ReactMethod
    public void init(ReadableMap options) {
        InitOptions initOptions = new InitOptions(options);

        Telemetry telemetry = new Telemetry("lock.react-native.android", initOptions.getLibraryVersion(), com.auth0.lock.BuildConfig.VERSION_NAME, null);
        lockBuilder = new Lock.Builder()
                .useWebView(true)
                .telemetry(telemetry)
                .clientId(initOptions.getClientId())
                .domainUrl(initOptions.getDomain())
                .configurationUrl(initOptions.getConfigurationDomain());

        if (providers != null) {
            for (Strategies strategy : providers.keySet()) {
                lockBuilder.withIdentityProvider(strategy, providers.get(strategy));
            }
        }
    }

    /**
     * This method is exported to JS
     * Called from JS to show the Lock.Android login/signup activities. It will display the
     * corresponding activity based on the requested connections and other options.
     *
     * @param options the map with the values passed when invoked from JS. Some of the supported
     *                fields are "closable", "connections", "useMagicLink" and "authParams"
     *                @see com.auth0.lock.react.bridge.ShowOptions
     * @param callback the JS callback that will be invoked with the results. It should be a function
     *                 of the form callback(error, profile, token)
     */

    @ReactMethod
    public void signIn(@Nullable ReadableMap options, String username, String password, Callback callback) {
        authCallback = callback;
        authenticationReceiver.registerIn(this.broadcastManager);

        ShowOptions showOptions = new ShowOptions(options);

        lockBuilder
                .closable(showOptions.isClosable())
                .authenticationParameters(showOptions.getAuthParams());

        if (showOptions.getConnections() != null) {
            lockBuilder.useConnections(showOptions.getConnections());
        }

        LockContext.configureLock(lockBuilder);

        Activity activity = getCurrentActivity();
        Lock lock = LockContext.getLock(activity);

        AuthenticationAPIClient apiClient = lock.getAuthenticationAPIClient();

        apiClient.login(username, password).start(new AuthenticationCallback() {

            @Override
            public void onSuccess(UserProfile userProfile, Token token) {
                // Store credentials
                // Navigate to your main activity
                authCallbackSuccess(userProfile, token);
            }
            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Error");
            }
        });
    }

    @ReactMethod
    public void show(@Nullable ReadableMap options, Callback callback) {
        authCallback = callback;
        authenticationReceiver.registerIn(this.broadcastManager);

        ShowOptions showOptions = new ShowOptions(options);

        lockBuilder
                .closable(showOptions.isClosable())
                .disableSignUp(showOptions.isDisableSignUp())
                .disableChangePassword(showOptions.isDisableResetPassword())
                .authenticationParameters(showOptions.getAuthParams());

        if (showOptions.getConnections() != null) {
            lockBuilder.useConnections(showOptions.getConnections());
        }

        LockContext.configureLock(lockBuilder);

        Activity activity = getCurrentActivity();

        if (activity == null) {
            invokeAuthCallback(getReactApplicationContext().getString(R.string.com_auth0_android_react_native_lock_no_activity), null, null);
            return;
        }

        Intent intent;
        switch (showOptions.getConnectionType()) {
            case CONNECTION_SMS:
                intent = new Intent(activity, LockPasswordlessActivity.class);
                intent.putExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER,
                        showOptions.useMagicLink()
                                ? LockPasswordlessActivity.MODE_SMS_MAGIC_LINK
                                : LockPasswordlessActivity.MODE_SMS_CODE);
                break;
            case CONNECTION_EMAIL:
                intent = new Intent(activity, LockPasswordlessActivity.class);
                intent.putExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER,
                        showOptions.useMagicLink()
                                ? LockPasswordlessActivity.MODE_EMAIL_MAGIC_LINK
                                : LockPasswordlessActivity.MODE_EMAIL_CODE);
                break;
            case CONNECTION_NATIVE:
            default:
                intent = new Intent(activity, LockActivity.class);
        }

        activity.startActivity(intent);
    }

    private boolean invokeAuthCallback(String err, UserProfile profile, Token token) {
        if (authCallback == null) {
            Log.e(TAG, "Invalid/old callback called! err: " + err + " profile: " + profile + " token: " + token);
            return false;
        }

        authenticationReceiver.unregisterFrom(this.broadcastManager);

        UserProfileBridge userProfileBridge = new UserProfileBridge(profile);
        TokenBridge tokenBridge = new TokenBridge(token);

        authCallback.invoke(err, userProfileBridge.toMap(), tokenBridge.toMap());
        authCallback = null;

        return true;
    }

    private boolean authCallbackSuccess(UserProfile profile, Token token) {
        return invokeAuthCallback(null, profile, token);
    }

    private boolean authCallbackError(String errorMessage) {
        return invokeAuthCallback(errorMessage, null, null);
    }
}
