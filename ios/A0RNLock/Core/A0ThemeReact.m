//
//  A0ThemeReact.m
//  A0RNLock
//
//  Created by Julien Moutte on 27/01/2017.
//  Copyright © 2017 Auth0. All rights reserved.
//

#import "A0ThemeReact.h"

@implementation A0ThemeReact

- (NSString *)getStyleKeyFromThemeKey:(NSString *)key {
    NSString *tmp = [key substringFromIndex:7];
    NSString *styleKey = [[[tmp substringToIndex:1] lowercaseString] stringByAppendingString:[tmp substringFromIndex:1]];
    return styleKey;
}

- (void)themeLockColorForKey:(NSString *)key style:(NSDictionary *)style {
    NSString *styleKey = [self getStyleKeyFromThemeKey:key];
    NSString *value = style[styleKey];
    if (value) {
        unsigned rgbValue = 0;
        NSScanner *scanner = [NSScanner scannerWithString:value];
        [scanner setScanLocation:1]; // bypass '#' character
        [scanner scanHexInt:&rgbValue];
        UIColor *color = [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
        [_theme registerColor:color forKey:key];
    }
}

- (void)themeLockImageForKey:(NSString *)key style:(NSDictionary *)style {
    NSString *styleKey = [self getStyleKeyFromThemeKey:key];
    NSString *value = style[styleKey];
    if (value) {
        [_theme registerImageWithName:value bundle:NULL forKey:key];
    }
}

- (void)themeLockWithStyle:(NSDictionary*)style {
    _theme = [[A0Theme alloc] init];
    
    // Primary Button
    [self themeLockColorForKey:A0ThemePrimaryButtonNormalColor style:style];
    [self themeLockColorForKey:A0ThemePrimaryButtonHighlightedColor style:style];
    [self themeLockColorForKey:A0ThemePrimaryButtonTextColor style:style];
    // Secondary Button
    [self themeLockColorForKey:A0ThemeSecondaryButtonBackgroundColor style:style];
    [self themeLockColorForKey:A0ThemeSecondaryButtonTextColor style:style];
    // Text field
    [self themeLockColorForKey:A0ThemeTextFieldTextColor style:style];
    [self themeLockColorForKey:A0ThemeTextFieldPlaceholderTextColor style:style];
    [self themeLockColorForKey:A0ThemeTextFieldIconColor style:style];
    // Title
    [self themeLockColorForKey:A0ThemeTitleTextColor style:style];
    // Icon
    [self themeLockColorForKey:A0ThemeIconBackgroundColor style:style];
    // Background
    [self themeLockColorForKey:A0ThemeScreenBackgroundColor style:style];
    // Description
    [self themeLockColorForKey:A0ThemeDescriptionTextColor style:style];
    [self themeLockColorForKey:A0ThemeSeparatorTextColor style:style];
    // Credentials Box
    [self themeLockColorForKey:A0ThemeCredentialBoxBorderColor style:style];
    [self themeLockColorForKey:A0ThemeCredentialBoxSeparatorColor style:style];
    [self themeLockColorForKey:A0ThemeCredentialBoxBackgroundColor style:style];
    // Close
    [self themeLockColorForKey:A0ThemeCloseButtonTintColor style:style];
    
    // Primary Button
    [self themeLockImageForKey:A0ThemePrimaryButtonNormalImageName style:style];
    [self themeLockImageForKey:A0ThemePrimaryButtonHighlightedImageName style:style];
    // Secondary Button
    [self themeLockImageForKey:A0ThemeSecondaryButtonNormalImageName style:style];
    [self themeLockImageForKey:A0ThemeSecondaryButtonHighlightedImageName style:style];
    // Icon
    [self themeLockImageForKey:A0ThemeIconImageName style:style];
    // Screen
    [self themeLockImageForKey:A0ThemeScreenBackgroundImageName style:style];
    
    [[A0Theme sharedInstance] registerTheme:_theme];
}

@end
