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

@interface A0LockReact()
@property (nonatomic, assign) BOOL shown;
@end

@implementation A0LockReact

+ (instancetype)sharedInstance {
    static A0LockReact *instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[A0LockReact alloc] init];
    });
    return instance;
}

- (void)configureLockWithClientId:(NSString *)clientId domain:(NSString *)domain version:(NSString *)version {
    _lock = [A0Lock newLockWithClientId:clientId domain:domain];
    NSString *lockVersion = [A0Telemetry libraryVersion];
    NSString *libraryVersion = version != nil ? version : @"0.0.0";
    NSDictionary *extra = @{
        @"lib_version": lockVersion,
    };
    A0Telemetry *telemetry = [A0Telemetry telemetryEnabled] ? [[A0Telemetry alloc] initWithName:@"lock.react-native.ios" version:libraryVersion extra:extra] : nil;
    self.lock.telemetry = telemetry;
}

- (void)hideWithCallback:(A0LockCallback)callback {
    if (self.shown) {
        if (!self.lock) {
            callback(@[@"Please configure Lock before using it"]);
            return;
        }
        UIViewController *controller = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
        [controller dismissViewControllerAnimated:YES completion:^{
            self.shown = NO;
            callback(@[]);
        }];
    } else {
        callback(@[]);
    }
}

- (void)showWithOptions:(NSDictionary *)options callback:(A0LockCallback)callback {
    static NSString *TouchID = @"touchid";
    static NSString *SMS = @"sms";
    static NSString *Email = @"email";

    if (!self.lock) {
        callback(@[@"Please configure Lock before using it", [NSNull null], [NSNull null]]);
        return;
    }

    self.lock.usePKCE = [options[@"pkce"] boolValue];
    NSArray *connections = options[@"connections"];
    NSArray *passwordless = [connections filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(NSString * _Nonnull name, NSDictionary<NSString *,id> * _Nullable bindings) {
        NSString *lowecaseName = name.lowercaseString;
        return [lowecaseName isEqualToString:TouchID] || [lowecaseName isEqualToString:SMS] || [lowecaseName isEqualToString:Email];
    }]];
    BOOL isTouchID = [connections containsObject:TouchID];
    BOOL isSMS = [connections containsObject:SMS];
    BOOL isEmail = [connections containsObject:Email];
    if (passwordless.count > 1) {
        callback(@[@"Must specify either 'touchid', 'email' or 'sms' connections", [NSNull null], [NSNull null]]);
        return;
    }

    UIViewController *controller = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
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
        self.shown =  NO;
        callback(@[@"Lock was dismissed by the user", [NSNull null], [NSNull null]]);
    };

    if (isTouchID) {
        A0TouchIDLockViewController *lock = [self.lock newTouchIDViewController];
        lock.closable = [options[@"closable"] boolValue];
        lock.disableSignUp = ![self booleanValueOf:options[@"allowSignUp"] defaultValue:YES];
        lock.cleanOnError = [options[@"cleanOnError"] boolValue];
        lock.cleanOnStart = [options[@"cleanOnStart"] boolValue];
        lock.authenticationParameters = [self authenticationParametersFromOptions:options];
        lock.onAuthenticationBlock = authenticationBlock;
        lock.onUserDismissBlock = dismissBlock;
        [self.lock presentTouchIDController:lock fromController:controller];
    } else if (isSMS) {
        A0SMSLockViewController *lock = [self.lock newSMSViewController];
        lock.closable = [options[@"closable"] boolValue];
        lock.useMagicLink = [options[@"magicLink"] boolValue];
        lock.onAuthenticationBlock = authenticationBlock;
        lock.onUserDismissBlock = dismissBlock;
        [self.lock presentSMSController:lock fromController:controller];
    } else if (isEmail) {
        A0EmailLockViewController *lock = [self.lock newEmailViewController];
        lock.closable = [options[@"closable"] boolValue];
        lock.useMagicLink = [options[@"magicLink"] boolValue];
        lock.authenticationParameters = [self authenticationParametersFromOptions:options];
        lock.onAuthenticationBlock = authenticationBlock;
        lock.onUserDismissBlock = dismissBlock;
        [self.lock presentEmailController:lock fromController:controller];
    } else {
        A0LockViewController *lock = [self.lock newLockViewController];
        lock.closable = [options[@"closable"] boolValue];
        lock.disableSignUp = [options[@"disableSignUp"] boolValue];
        lock.disableResetPassword = [options[@"disableResetPassword"] boolValue];
        lock.usesEmail = [self booleanValueOf:options[@"usesEmail"] defaultValue:YES];
        lock.useWebView = [self booleanValueOf:options[@"useWebView"] defaultValue:YES];
        lock.loginAfterSignUp = [self booleanValueOf:options[@"loginAfterSignUp"] defaultValue:YES];
        lock.defaultADUsernameFromEmailPrefix = [options[@"defaultADUsernameFromEmailPrefix"] boolValue];
        lock.connections = options[@"connections"];
        lock.defaultDatabaseConnectionName = options[@"defaultDatabaseConnectionName"];
        lock.authenticationParameters = [self authenticationParametersFromOptions:options];
        lock.onAuthenticationBlock = authenticationBlock;
        lock.onUserDismissBlock = dismissBlock;
        [self.lock presentLockController:lock fromController:controller];
    }
    self.shown = YES;
}

- (void)authenticateWithConnectionName:(NSString *)connectionName options:(NSDictionary *)options callback:(A0LockCallback)callback {
    A0IdentityProviderAuthenticator *authenticator = [self.lock identityProviderAuthenticator];
    void(^success)(A0UserProfile *, A0Token *) = ^(A0UserProfile *profile, A0Token *token) {
        if (profile && token) {
            NSDictionary *profileDict = [profile asDictionary];
            NSDictionary *tokenDict = [token asDictionary];
            callback(@[[NSNull null], profileDict, tokenDict]);
        } else {
            callback(@[@"Unexpected null value in profile or token"]);
        }
    };
    void(^failure)(NSError *) = ^(NSError *error) {
        callback(@[[error localizedDescription]]);
    };

    A0AuthParameters* parameters = [A0AuthParameters newWithDictionary:options];
    [authenticator authenticateWithConnectionName:connectionName parameters:parameters success:success failure:failure];
}

- (NSArray *)scopeParamterFromOptions:(NSDictionary *)options {
    NSArray *scopes = [options[@"scope"] componentsSeparatedByString:@" "];
    return scopes;
}

- (A0AuthParameters *)authenticationParametersFromOptions:(NSDictionary *)options {
    NSDictionary *jsonParameters = options[@"authParams"];
    if (jsonParameters.count == 0) {
        return nil;
    }
    NSMutableDictionary *params = [jsonParameters mutableCopy];
    params[@"scope"] = [self scopeParamterFromOptions:params];
    return [A0AuthParameters newWithDictionary:params];
}

- (BOOL)booleanValueOf:(id)value defaultValue:(BOOL)defaultValue {
    return value != nil ? [value boolValue] : defaultValue;
}
@end
