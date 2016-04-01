/*
 * InitOptions.java
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

import com.facebook.react.bridge.ReadableMap;

public class InitOptions {

    private static final String CLIENT_ID_KEY = "clientId";
    private static final String DOMAIN_KEY = "domain";
    private static final String CONFIGURATION_DOMAIN_KEY = "configurationDomain";
    private static final String LIBRARY_VERSION_KEY = "libraryVersion";

    private String clientId;
    private String domain;
    private String configurationDomain;
    private String libraryVersion;

    public InitOptions(@Nullable ReadableMap options) {
        if (options == null) {
            return;
        }

        if (options.hasKey(CLIENT_ID_KEY)) {
            this.clientId = options.getString(CLIENT_ID_KEY);
        }

        if (options.hasKey(DOMAIN_KEY)) {
            this.domain = options.getString(DOMAIN_KEY);
        }

        if (options.hasKey(CONFIGURATION_DOMAIN_KEY)) {
            this.configurationDomain = options.getString(CONFIGURATION_DOMAIN_KEY);
        }

        this.libraryVersion = options.hasKey(LIBRARY_VERSION_KEY) ? options.getString(LIBRARY_VERSION_KEY) : "0.0.0";
    }

    public String getClientId() {
        return clientId;
    }

    public String getDomain() {
        return domain;
    }

    public String getConfigurationDomain() {
        return configurationDomain;
    }

    public String getLibraryVersion() {
        return libraryVersion;
    }
}
