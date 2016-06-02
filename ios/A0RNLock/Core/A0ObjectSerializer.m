// A0ObjectSerializer.m
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

#import "A0ObjectSerializer.h"
#import <objc/runtime.h>

@implementation A0ObjectSerializer

- (instancetype)init {
    [NSException raise:NSInternalInconsistencyException format:@"Cannot instantiate helper class"];
    return nil;
}

+ (NSDictionary *)dictionaryFrom:(NSObject<A0DictionarySerialization> *)object {
    unsigned int count = 0;
    objc_property_t *properties = class_copyPropertyList([object class], &count);

    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] initWithCapacity:count];

    NSArray *propertiesToSkip = @[
                                  NSStringFromSelector(@selector(debugDescription)),
                                  NSStringFromSelector(@selector(description)),
                                  NSStringFromSelector(@selector(hash)),
                                  NSStringFromSelector(@selector(superclass)),
                                  ];
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
        if ([propertiesToSkip containsObject:key]) {
            continue;
        }
        id value = [object valueForKey:key];
        id entryValue = [self entryValueFromObject:value];
        if (entryValue) {
            [dictionary setObject:entryValue forKey:key];
        }
    }

    free(properties);

    return dictionary;
}

+ (id)entryValueFromObject:(id)object {
    id value;

    if ([object isKindOfClass:NSURL.class]) {
        value = [self stringFromURL:object];
    }
    if ([object isKindOfClass:NSDate.class]) {
        value = [self timestampFromDate:object];
    }
    if ([object conformsToProtocol:@protocol(A0DictionarySerialization)]) {
        value = [object asDictionary];
    }
    if ([object isKindOfClass:NSArray.class]) {
        value = [self arrayFromArray:object];
    }
    if ([object isKindOfClass:NSDictionary.class]) {
        value = [self dictionaryFromDictionary:object];
    }
    if ([object isKindOfClass:NSString.class] ||
        [object isKindOfClass:NSNumber.class]) {
        value = object;
    }
    return value;
}

+ (NSString *)stringFromURL:(NSURL *)url {
    return url.absoluteString;
}

+ (NSNumber *)timestampFromDate:(NSDate *)date {
    return @(date.timeIntervalSince1970);
}

+ (NSArray *)arrayFromArray:(NSArray *)array {
    NSMutableArray *tranformed = [@[] mutableCopy];
    [array enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        id value = [self entryValueFromObject:obj];
        if (value) {
            [tranformed addObject:value];
        }
    }];
    return [NSArray arrayWithArray:tranformed];
}

+ (NSDictionary *)dictionaryFromDictionary:(NSDictionary *)dictionary {
    NSMutableDictionary *transformed = [@{} mutableCopy];
    [dictionary enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        id value;
        if ([key isKindOfClass:NSString.class]) {
            value = [self entryValueFromObject:obj];
        }
        if (value) {
            transformed[key] = value;
        }
    }];
    return [NSDictionary dictionaryWithDictionary:transformed];
}

@end
