//
//  UpNextAppDelegate.m
//  UpNext
//
//  Created by Juyong Kim on 2014. 2. 27..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import "UpNextAppDelegate.h"
#import <FacebookSDK/FacebookSDK.h>

@implementation UpNextAppDelegate

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
    // FBSample logic
    // We need to handle URLs by passing them to FBSession in order for SSO authentication
    // to work.
    return [FBSession.activeSession handleOpenURL:url];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
//    // Override point for customization after application launch.
//    CGSize iOSDeviceScreenSize = [[UIScreen mainScreen] bounds].size;
//    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
//    UIViewController *initialViewController;
//    
//    if (iOSDeviceScreenSize.height == 480)
//    {
//        initialViewController = [mainStoryboard instantiateViewControllerWithIdentifier:@"iPhone4Storyboard"];
//    }
//    
//    if (iOSDeviceScreenSize.height == 568)
//    {   // iPhone 5 and iPod Touch 5th generation: 4 inch screen
//        initialViewController = [mainStoryboard instantiateInitialViewController];
//    }
//    // Instantiate a UIWindow object and initialize it with the screen size of the iOS device
//    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
//    // Set the initial view controller to be the root view controller of the window object
//    self.window.rootViewController  = initialViewController;
//    // Set the window object to be the key window and show it
//    [self.window makeKeyAndVisible];
    [FBProfilePictureView class];
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
