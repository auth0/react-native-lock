// A0LockReact.m
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

#import "A0LockReact.h"
#import <Lock/Lock.h>
#import "A0Token+ReactNative.h"
#import "A0UserProfile+ReactNative.h"

@implementation A0LockReact

+ (instancetype)sharedInstance {
    static A0LockReact *instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[A0LockReact alloc] init];
    });
    return instance;
}

- (void)configureLockFromBundle {
    _lock = [A0Lock new];
}

- (void)configureLockWithClientId:(NSString *)clientId domain:(NSString *)domain {
    _lock = [A0Lock newLockWithClientId:clientId domain:domain];
}

- (void)showWithOptions:(NSDictionary *)options callback:(A0LockCallback)callback {
    if (!self.lock) {
        callback(@[@"Please configure Lock before using it", [NSNull null], [NSNull null]]);
        return;
    }
    NSArray *connections = options[@"connections"];
    BOOL isTouchID = [connections containsObject:@"touchid"];
    BOOL isSMS = [connections containsObject:@"sms"];
    if (isTouchID && isSMS) {
        callback(@[@"Must specify either 'touchid' or 'sms' connections", [NSNull null], [NSNull null]]);
        return;
    }

    UIViewController *controller = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
    UIViewController *lock;
    A0AuthenticationBlock authenticationBlock = ^(A0UserProfile *profile, A0Token *token) {
        if (profile && token) {
            NSDictionary *profileDict = [profile asDictionary];
            NSDictionary *tokenDict = [token asDictionary];
            callback(@[[NSNull null], profileDict, tokenDict]);
        } else {
            callback(@[]);
        }
        [controller dismissViewControllerAnimated:YES completion:nil];
    };
    void(^dismissBlock)() = ^{
        callback(@[@"Lock was dismissed by the user", [NSNull null], [NSNull null]]);
    };
    if (isTouchID) {
        lock = [self buildTouchIDLockWithOptions:options authenticationBlock:authenticationBlock dismissBlock:dismissBlock];
    } else if (isSMS) {
        lock = [self buildSMSLockWithOptions:options authenticationBlock:authenticationBlock dismissBlock:dismissBlock];
    } else {
        lock = [self buildLockWithOptions:options authenticationBlock:authenticationBlock dismissBlock:dismissBlock];
    }
    [controller presentViewController:lock animated:YES completion:nil];
}

- (void)showSMSWithOptions:(NSDictionary *)options callback:(A0LockCallback)callback {
    UIViewController *controller = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
    A0AuthenticationBlock authenticationBlock = ^(A0UserProfile *profile, A0Token *token) {
        NSDictionary *profileDict = [profile asDictionary];
        NSDictionary *tokenDict = [token asDictionary];
        callback(@[[NSNull null], profileDict, tokenDict]);
        [controller dismissViewControllerAnimated:YES completion:nil];
    };
    void(^dismissBlock)() = ^{
        callback(@[@"Lock was dismissed by the user", [NSNull null], [NSNull null]]);
    };
    UIViewController *lock = [self buildSMSLockWithOptions:options authenticationBlock:authenticationBlock dismissBlock:dismissBlock];
    [controller presentViewController:lock animated:YES completion:nil];
}

- (void)showTouchIDWithOptions:(NSDictionary *)options callback:(A0LockCallback)callback {
    UIViewController *controller = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
    A0AuthenticationBlock authenticationBlock = ^(A0UserProfile *profile, A0Token *token) {
        NSDictionary *profileDict = [profile asDictionary];
        NSDictionary *tokenDict = [token asDictionary];
        callback(@[[NSNull null], profileDict, tokenDict]);
        [controller dismissViewControllerAnimated:YES completion:nil];
    };
    void(^dismissBlock)() = ^{
        callback(@[@"Lock was dismissed by the user", [NSNull null], [NSNull null]]);
    };
    UIViewController *lock = [self buildTouchIDLockWithOptions:options authenticationBlock:authenticationBlock dismissBlock:dismissBlock];
    [controller presentViewController:lock animated:YES completion:nil];
}

- (UIViewController *)buildTouchIDLockWithOptions:(NSDictionary *)options authenticationBlock:(A0AuthenticationBlock)authenticationBlock dismissBlock:(void(^)())dismissBlock {
    A0TouchIDLockViewController *lock = [self.lock newTouchIDViewController];
    lock.closable = [options[@"closable"] boolValue];
    lock.authenticationParameters = [self authenticationParametersFromOptions:options];
    lock.onAuthenticationBlock = authenticationBlock;
    lock.onUserDismissBlock = dismissBlock;
    return [[UINavigationController alloc] initWithRootViewController:lock];
}

- (UIViewController *)buildSMSLockWithOptions:(NSDictionary *)options authenticationBlock:(A0AuthenticationBlock)authenticationBlock dismissBlock:(void(^)())dismissBlock {
    A0SMSLockViewController *lock = [self.lock newSMSViewController];
    lock.closable = [options[@"closable"] boolValue];
    lock.onAuthenticationBlock = authenticationBlock;
    lock.onUserDismissBlock = dismissBlock;
    return [[UINavigationController alloc] initWithRootViewController:lock];
}

- (UIViewController *)buildLockWithOptions:(NSDictionary *)options authenticationBlock:(A0AuthenticationBlock)authenticationBlock dismissBlock:(void(^)())dismissBlock {
    A0LockViewController *lock = [self.lock newLockViewController];
    lock.closable = [options[@"closable"] boolValue];
    lock.usesEmail = [options[@"usesEmail"] boolValue];
    lock.useWebView = [options[@"useWebView"] boolValue];
    lock.loginAfterSignUp = [options[@"loginAfterSignUp"] boolValue];
    lock.defaultADUsernameFromEmailPrefix = [options[@"defaultADUsernameFromEmailPrefix"] boolValue];
    lock.connections = options[@"connections"];
    lock.defaultDatabaseConnectionName = options[@"defaultDatabaseConnectionName"];
    lock.authenticationParameters = [self authenticationParametersFromOptions:options];
    lock.onAuthenticationBlock = authenticationBlock;
    lock.onUserDismissBlock = dismissBlock;
    return lock;
}

- (A0AuthParameters *)authenticationParametersFromOptions:(NSDictionary *)options {
    NSDictionary *jsonParameters = options[@"authParams"];
    if (jsonParameters.count == 0) {
        return nil;
    }
    NSMutableDictionary *params = [jsonParameters mutableCopy];
    NSString *scope = params[@"scope"];
    NSArray *scopes = [scope componentsSeparatedByString:@" "];
    if (scopes.count > 0) {
        params[@"scope"] = scopes;
    }
    return [A0AuthParameters newWithDictionary:params];
}

@end
