/*
 * InitOptionsTest.java
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
import com.facebook.react.bridge.SimpleMap;
import com.facebook.react.bridge.WritableMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class InitOptionsTest {

    @Test
    public void testAllNull() throws Exception {
        InitOptions initOptions = new InitOptions(null);
        assertThat(initOptions.getClientId(), is(nullValue()));
        assertThat(initOptions.getConfigurationDomain(), is(nullValue()));
        assertThat(initOptions.getDomain(), is(nullValue()));
    }

    @Test
    public void testAll() throws Exception {
        WritableMap options = new SimpleMap();
        options.putString("clientId", "client-id-value");
        options.putString("domain", "domain-value");
        options.putString("configurationDomain", "configuration-domain-value");

        InitOptions initOptions = new InitOptions(options);
        assertThat(initOptions.getClientId(), is(equalTo("client-id-value")));
        assertThat(initOptions.getConfigurationDomain(), is(equalTo("configuration-domain-value")));
        assertThat(initOptions.getDomain(), is(equalTo("domain-value")));
    }

    @Test
    public void testOnlyClientId() throws Exception {
        WritableMap options = new SimpleMap();
        options.putString("clientId", "client-id-value");

        InitOptions initOptions = new InitOptions(options);
        assertThat(initOptions.getClientId(), is(equalTo("client-id-value")));
        assertThat(initOptions.getConfigurationDomain(), is(nullValue()));
        assertThat(initOptions.getDomain(), is(nullValue()));
    }

    @Test
    public void testOnlyDomain() throws Exception {
        WritableMap options = new SimpleMap();
        options.putString("domain", "domain-value");

        InitOptions initOptions = new InitOptions(options);
        assertThat(initOptions.getClientId(), is(nullValue()));
        assertThat(initOptions.getConfigurationDomain(), is(nullValue()));
        assertThat(initOptions.getDomain(), is(equalTo("domain-value")));
    }

    @Test
    public void testOnlyConfigurationDomain() throws Exception {
        WritableMap options = new SimpleMap();
        options.putString("configurationDomain", "configuration-domain-value");

        InitOptions initOptions = new InitOptions(options);
        assertThat(initOptions.getClientId(), is(nullValue()));
        assertThat(initOptions.getConfigurationDomain(), is(equalTo("configuration-domain-value")));
        assertThat(initOptions.getDomain(), is(nullValue()));
    }
}
