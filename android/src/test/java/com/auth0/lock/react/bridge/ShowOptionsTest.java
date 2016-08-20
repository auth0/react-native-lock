/*
 * ShowOptionsTest.java
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

import com.auth0.lock.react.BuildConfig;
import com.auth0.lock.react.LockReactModule;
import com.facebook.react.bridge.SimpleArray;
import com.facebook.react.bridge.SimpleMap;
import com.facebook.react.bridge.WritableMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class ShowOptionsTest {

    @Test
    public void testAllNull() throws Exception {
        ShowOptions showOptions = new ShowOptions(null);
        assertThat(showOptions.isClosable(), is(false));
        assertThat(showOptions.isDisableSignUp(), is(false));
        assertThat(showOptions.isDisableResetPassword(), is(false));
        assertThat(showOptions.useMagicLink(), is(false));
        assertThat(showOptions.getConnections(), is(nullValue()));
        assertThat(showOptions.getAuthParams(), is(nullValue()));
    }

    @Test
    public void testAllNative() throws Exception {
        WritableMap options = new SimpleMap();
        options.putBoolean("closable", true);
        options.putBoolean("disableSignUp", true);
        options.putBoolean("disableResetPassword", true);
        options.putBoolean("magicLink", true);

        SimpleArray connections = new SimpleArray();
        connections.pushString("facebook");
        connections.pushString("twitter");
        options.putArray("connections", connections);

        SimpleMap authParams = new SimpleMap();
        authParams.putString("string", "string-value");
        authParams.putInt("int", 345);
        authParams.putBoolean("boolean-true", true);
        authParams.putBoolean("boolean-false", false);
        options.putMap("authParams", authParams);

        ShowOptions showOptions = new ShowOptions(options);
        assertThat(showOptions.isClosable(), is(true));
        assertThat(showOptions.isDisableSignUp(), is(true));
        assertThat(showOptions.isDisableResetPassword(), is(true));
        assertThat(showOptions.useMagicLink(), is(true));
        assertThat(showOptions.getConnectionType(), is(equalTo(LockReactModule.CONNECTION_NATIVE)));
        assertThat(Arrays.asList(showOptions.getConnections()), containsInAnyOrder("twitter", "facebook"));

        Map<String, Object> authParams2 = showOptions.getAuthParams();
        String stringValue = (String) authParams2.get("string");
        assertThat(stringValue, is(equalTo("string-value")));

        int intValue = (int) authParams2.get("int");
        assertThat(intValue, is(equalTo(345)));

        boolean booleanFalse = (boolean) authParams2.get("boolean-false");
        assertThat(booleanFalse, is(false));

        boolean booleanTrue = (boolean) authParams2.get("boolean-true");
        assertThat(booleanTrue, is(true));
    }

    @Test
    public void testEmail() throws Exception {
        WritableMap options = new SimpleMap();

        SimpleArray connections = new SimpleArray();
        connections.pushString("email");
        connections.pushString("facebook");
        connections.pushString("twitter");
        options.putArray("connections", connections);

        ShowOptions showOptions = new ShowOptions(options);
        assertThat(showOptions.getConnectionType(), is(equalTo(LockReactModule.CONNECTION_EMAIL)));
        assertThat(Arrays.asList(showOptions.getConnections()), containsInAnyOrder("email", "twitter", "facebook"));
    }

    @Test
    public void testSms() throws Exception {
        WritableMap options = new SimpleMap();

        SimpleArray connections = new SimpleArray();
        connections.pushString("sms");
        connections.pushString("facebook");
        connections.pushString("twitter");
        options.putArray("connections", connections);

        ShowOptions showOptions = new ShowOptions(options);
        assertThat(showOptions.getConnectionType(), is(equalTo(LockReactModule.CONNECTION_SMS)));
        assertThat(Arrays.asList(showOptions.getConnections()), containsInAnyOrder("sms", "twitter", "facebook"));
    }
}
