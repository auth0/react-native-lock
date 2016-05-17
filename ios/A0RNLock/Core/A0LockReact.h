// A0LockReact.h
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

#import <Foundation/Foundation.h>
#import <Lock/Lock.h>

typedef void(^A0LockCallback)(NSArray *parameters);

@interface A0LockReact : NSObject

@property (strong, readonly, nonatomic) A0Lock *lock;

- (void)hideWithCallback:(A0LockCallback)callback;

- (void)showWithOptions:(NSDictionary *)options callback:(A0LockCallback)callback;

- (void)authenticateWithConnectionName:(NSString *)connectionName options:(NSDictionary *)options callback:(A0LockCallback)callback;

- (void)configureLockWithClientId:(NSString *)clientId domain:(NSString *)domain version:(NSString *)version;

+ (instancetype)sharedInstance;

@end
