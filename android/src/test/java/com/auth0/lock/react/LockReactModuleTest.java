/*
 * LockReactModuleTest.java
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

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.LockActivity;
import com.auth0.lock.passwordless.LockPasswordlessActivity;
import com.auth0.lock.react.bridge.TokenBridge;
import com.auth0.lock.react.bridge.UserProfileBridge;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SimpleArray;
import com.facebook.react.bridge.SimpleMap;
import com.facebook.react.bridge.WritableMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@PrepareForTest({Arguments.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18, manifest = Config.NONE)
@Ignore("No longer using context to start activity. Mocks need fixing")
public class LockReactModuleTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private LockReactModule module;
    @Mock
    private ReactApplicationContext reactContext;
    @Mock
    private Callback callback;
    @Mock
    private LocalBroadcastManager broadcastManager;

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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        module = new LockReactModule(reactContext, broadcastManager);
    }

    @Test
    public void shouldStartDefaultLockActivity() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(null, callback);
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(reactContext).startActivity(captor.capture());
        final Intent intent = captor.getValue();
        final ComponentName component = intent.getComponent();
        assertEquals(component.getClassName(), LockActivity.class.getCanonicalName());
        assertThat(intent.getFlags(), is(equalTo(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Test
    public void shouldStartPasswordlessEmailActivityCode() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(showOptions(false, false, false, false, new String[]{"email", "twitter"}), callback);
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(reactContext).startActivity(captor.capture());
        final Intent intent = captor.getValue();
        assertThat(intent.getIntExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER, LockPasswordlessActivity.MODE_UNKNOWN),
                is(equalTo(LockPasswordlessActivity.MODE_EMAIL_CODE)));
        final ComponentName component = intent.getComponent();
        assertEquals(component.getClassName(), LockPasswordlessActivity.class.getCanonicalName());
        assertThat(intent.getFlags(), is(equalTo(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Test
    public void shouldStartPasswordlessEmailActivityMagicLink() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(showOptions(false, false, false, true, new String[]{"linkedin", "email", "twitter"}), callback);
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(reactContext).startActivity(captor.capture());
        final Intent intent = captor.getValue();
        assertThat(intent.getIntExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER, LockPasswordlessActivity.MODE_UNKNOWN),
                is(equalTo(LockPasswordlessActivity.MODE_EMAIL_MAGIC_LINK)));
        final ComponentName component = intent.getComponent();
        assertEquals(component.getClassName(), LockPasswordlessActivity.class.getCanonicalName());
        assertThat(intent.getFlags(), is(equalTo(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Test
    public void shouldStartPasswordlessSmsActivityCode() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(showOptions(false, false, false, false, new String[]{"sms", "facebook"}), callback);
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(reactContext).startActivity(captor.capture());
        final Intent intent = captor.getValue();
        assertThat(intent.getIntExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER, LockPasswordlessActivity.MODE_UNKNOWN),
                is(equalTo(LockPasswordlessActivity.MODE_SMS_CODE)));
        final ComponentName component = intent.getComponent();
        assertEquals(component.getClassName(), LockPasswordlessActivity.class.getCanonicalName());
        assertThat(intent.getFlags(), is(equalTo(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Test
    public void shouldStartPasswordlessSmsActivityMagicLink() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(showOptions(false, false, false, true, new String[]{"linkedin", "sms", "twitter"}), callback);
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(reactContext).startActivity(captor.capture());
        final Intent intent = captor.getValue();
        assertThat(intent.getIntExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER, LockPasswordlessActivity.MODE_UNKNOWN),
                is(equalTo(LockPasswordlessActivity.MODE_SMS_MAGIC_LINK)));
        final ComponentName component = intent.getComponent();
        assertEquals(component.getClassName(), LockPasswordlessActivity.class.getCanonicalName());
        assertThat(intent.getFlags(), is(equalTo(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Test
    public void shouldAuthenticateSuccessfully() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(null, callback);

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", "user-id-value");
        values.put("name", "name-value");
        values.put("extra_key", "extra-key-value");
        UserProfile profile = new UserProfile(values);

        Token token = new Token("id-token", "access-token", "token-type", "refresh-token");

        Intent result = new Intent(Lock.AUTHENTICATION_ACTION)
                .putExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER, profile)
                .putExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER, token);

        module.authenticationReceiver.onReceive(reactContext, result);

        UserProfileBridge profileBridge = new UserProfileBridge(profile);
        TokenBridge tokenBridge = new TokenBridge(token);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(callback).invoke(captor.capture());

        List<Object> list = captor.getAllValues();
        assertThat(list.get(0), is(nullValue()));
        assertThat((WritableMap) list.get(1), is(equalTo(profileBridge.toMap())));
        assertThat((WritableMap) list.get(2), is(equalTo(tokenBridge.toMap())));

        verifyNoMoreInteractions(callback);
    }

    @Test
    public void shouldCancelAuthentication() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(null, callback);

        Intent result = new Intent(Lock.CANCEL_ACTION);

        module.authenticationReceiver.onReceive(reactContext, result);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(callback).invoke(captor.capture());

        List<Object> list = captor.getAllValues();
        assertThat((String) list.get(0), is(equalTo(LockReactModule.MESSAGE_USER_CANCELLED)));
        assertThat(list.get(1), is(nullValue()));
        assertThat(list.get(2), is(nullValue()));

        verifyNoMoreInteractions(callback);
    }

    @Test
    public void shouldNotifySignUp() throws Exception {
        module.init(initOptions("CLIENT_ID", "samples.auth0.com", null));
        module.show(null, callback);

        Intent result = new Intent(Lock.AUTHENTICATION_ACTION);

        module.authenticationReceiver.onReceive(reactContext, result);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(callback).invoke(captor.capture());

        List<Object> list = captor.getAllValues();
        assertThat((String) list.get(0), is(equalTo(LockReactModule.MESSAGE_USER_SIGNED_UP)));
        assertThat(list.get(1), is(nullValue()));
        assertThat(list.get(2), is(nullValue()));

        verifyNoMoreInteractions(callback);
    }

    private ReadableMap initOptions(String clientId, String domain, String configDomain) {
        SimpleMap options = new SimpleMap();
        options.putString("clientId", clientId);
        options.putString("domain", domain);
        options.putString("configurationDomain", configDomain);
        return options;
    }

    private ReadableMap showOptions(boolean closable, boolean disableSignUp, boolean disableResetPassword, boolean useMagicLink, String[] connections) {
        SimpleMap options = new SimpleMap();
        options.putBoolean("closable", closable);
        options.putBoolean("disableSignup", disableSignUp);
        options.putBoolean("disableResetPassword", disableResetPassword);
        options.putBoolean("useMagicLink", useMagicLink);
        SimpleArray connectionsArray = new SimpleArray();
        for (String connection : connections) {
            connectionsArray.pushString(connection);
        }
        options.putArray("connections", connectionsArray);
        return options;
    }
}