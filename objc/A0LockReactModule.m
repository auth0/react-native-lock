// A0LockReactModule.m
//
// Copyright (c) 2015 Auth0 (http://auth0.com)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

#import "A0LockReactModule.h"
#import <LockReactNative/A0LockReact.h>
#import <Lock/Lock.h>

#if __has_include(<Lock-Facebook/A0FacebookAuthenticator.h>)
#define FACEBOOK_ENABLED 1
#import <Lock-Facebook/A0FacebookAuthenticator.h>
#endif

#if __has_include(<Lock-Twitter/A0TwitterAuthenticator.h>)
#define TWITTER_ENABLED 1
#import <Lock-Twitter/A0TwitterAuthenticator.h>
#endif

#if __has_include(<Lock-GooglePlus/A0GooglePlusAuthenticator.h>)
#define GOOGLE_PLUS_ENABLED 1
#import <Lock-GooglePlus/A0GooglePlusAuthenticator.h>
#endif

@implementation A0LockReactModule

RCT_EXPORT_MODULE(Auth0LockModule);

RCT_REMAP_METHOD(init, configureLockWithValues:(NSDictionary *)values) {
    if (values.count == 2) {
        [[A0LockReact sharedInstance] configureLockWithClientId:values[@"clientId"] domain:values[@"domain"]];
    } else {
        [[A0LockReact sharedInstance] configureLockFromBundle];
    }
}

RCT_EXPORT_METHOD(nativeIntegrations:(NSDictionary *)integrations) {
    A0Lock *lock = [[A0LockReact sharedInstance] lock];
    if (!lock) {
        return;
    }
    __block NSMutableArray *authenticators = [@[] mutableCopy];
    [integrations enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, NSDictionary * _Nonnull values, BOOL * _Nonnull stop) {
#ifdef FACEBOOK_ENABLED
        if ([@"facebook" isEqualToString:key]) {
            NSArray *permissions = values[@"permissions"];
            if (permissions.count == 0) {
                permissions = nil;
            }
            [authenticators addObject:[A0FacebookAuthenticator newAuthenticatorWithPermissions:permissions]];
        }
#endif
#ifdef TWITTER_ENABLED
        if ([@"twitter" isEqualToString:key]) {
            NSString *apiKey = values[@"api_key"];
            NSString *apiSecret = values[@"api_secret"];
            [authenticators addObject:[A0TwitterAuthenticator newAuthenticatorWithKey:apiKey andSecret:apiSecret]];
        }
#endif
#ifdef GOOGLE_PLUS_ENABLED
        if ([@"google" isEqualToString:key]) {
            NSString *clientId = values[@"client_id"];
            NSArray *scopes = values[@"scopes"];
            [authenticators addObject:[A0GooglePlusAuthenticator newAuthenticatorWithClientId:clientId andScopes:scopes]];
        }
#endif
    }];
    [lock registerAuthenticators:authenticators];
}

RCT_EXPORT_METHOD(hide:(RCTResponseSenderBlock)callback) {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[A0LockReact sharedInstance] hideWithCallback:callback];
    });
}

RCT_EXPORT_METHOD(show:(NSDictionary *)options callback:(RCTResponseSenderBlock)callback) {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[A0LockReact sharedInstance] showWithOptions:options callback:callback];
    });
}

@end