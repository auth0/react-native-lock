//
//  A0ThemeReact.h
//  A0RNLock
//
//  Created by Julien Moutte on 27/01/2017.
//  Copyright © 2017 Auth0. All rights reserved.
//

#ifndef A0ThemeReact_h
#define A0ThemeReact_h

#import <UIKit/UIKit.h>
#import <Lock/Lock.h>

@interface A0ThemeReact : NSObject

@property (strong, readonly, nonatomic) A0Theme *theme;

- (void)themeLockColorForKey:(NSString *)key style:(NSDictionary *)style;

- (void)themeLockWithStyle:(NSDictionary*)style;

@end

#endif /* A0ThemeReact_h */
