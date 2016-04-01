/*
 * LockReactPackage.java
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


import com.auth0.core.Strategies;
import com.auth0.identity.IdentityProvider;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the package that provides a wrapper around Lock.Android for use in an Android React
 * Native application. In order to use this you'll also need the {@code react-native-lock-android}
 * dependency installed. You can add it your application's dependencies by running
 * {@code npm install --save react-native-lock-android}
 * Then you must add the LockReactPackage to the {@code ReactInstanceManager} in the {@code onCreate}
 * method of your {@code MainActivity}, as seen in the following snippet:
 *
 * <pre>{@code
 *  public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
 *      ...
 *      @Override
 *      protected void onCreate(Bundle savedInstanceState) {
 *          super.onCreate(savedInstanceState);
 *          ...
 *          LockReactPackage lockReactPackage = new LockReactPackage();
 *
 *          // If you would like to add native integrations, add them like this (optional)
 *          lockReactPackage.addIdentityProvider(Strategies.Facebook, new FacebookIdentityProvider(this));
 *          lockReactPackage.addIdentityProvider(Strategies.GooglePlus, new GooglePlusIdentityProvider(this));
 *
 *          mReactInstanceManager = ReactInstanceManager.builder()
 *                  ...
 *                  .addPackage(lockReactPackage)
 *                  ...
 *                  .build();
 *          ...
 *      }
 *      ...
 *  }
 * }</pre>
 *
 * You must also declare the Lock activities in your {@code AndroidManifest.xml} file.
 *
 * <pre>{@code
 *  <!--Auth0 Lock-->
 *  <activity
 *      android:name="com.auth0.lock.LockActivity"
 *      android:theme="@style/Lock.Theme"
 *      android:screenOrientation="portrait"
 *      android:launchMode="singleTask">
 *  </activity>
 *  <!--Auth0 Lock End-->
 *  <!--Auth0 Lock Embedded WebView-->
 *  <activity
 *      android:name="com.auth0.identity.web.WebViewActivity"
 *      android:theme="@style/Lock.Theme">
 *  </activity>
 *  <!--Auth0 Lock Embedded WebView End-->
 *  <!--Auth0 Lock Passwordless-->
 *  <activity
 *      android:name="com.auth0.lock.passwordless.LockPasswordlessActivity"
 *      android:theme="@style/Lock.Theme"
 *      android:screenOrientation="portrait"
 *      android:launchMode="singleTask">
 *  </activity>
 *  <activity
 *      android:name="com.auth0.lock.passwordless.CountryCodeActivity"
 *      android:theme="@style/Lock.Theme">
 *  </activity>
 *  <!--Auth0 Lock Passwordless End-->
 * }</pre>
 *
 * In case you added native integration with Facebook or GooglePlus you'll need to configure them
 * properly. See https://github.com/auth0/react-native-lock-android
 */
@SuppressWarnings("unused")
public class LockReactPackage implements ReactPackage {

    Map<Strategies, IdentityProvider> providers;

    /**
     * Sets a native handler for a specific Identity Provider (IdP), e.g.: Facebook
     *
     * @param strategy Auth0 strategy to handle. (For all valid values check {@link com.auth0.core.Strategies}
     * @param identityProvider IdP handler
     */
    @SuppressWarnings("unused")
    public void addIdentityProvider(Strategies strategy, IdentityProvider identityProvider) {
        if (providers == null) {
            providers = new HashMap<>();
        }
        providers.put(strategy, identityProvider);
    }

    /**
     * This method is called by React Native to register native modules
     * Here is where we add our module {@link com.auth0.lock.react.LockReactModule}.
     *
     * @param reactContext react application context that can be used to create modules
     * @return list of native modules to register with the newly created catalyst instance
     */
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();

        modules.add(new LockReactModule(reactContext, providers));

        return modules;
    }

    /**
     * NOT USED. This is a required override
     * @see ReactPackage
     */
    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return new ArrayList<>();
    }

    /**
     * NOT USED. This is a required override
     * @see ReactPackage
     */
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return new ArrayList<>();
    }
}
