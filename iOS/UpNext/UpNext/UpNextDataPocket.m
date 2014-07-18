//
//  UpNextDataPocket.m
//  UpNextian
//
//  Created by Juyong Kim, Yongjun Yoo on 2013. 11. 5..
//  Copyright (c) 2013년 UpNext. All rights reserved.
//

#import "UpNextDataPocket.h"
#import "UpNextRequest.h"

@implementation UpNextDataPocket

#define BUSINESS_KEY @"businesses"
#define KEYWORD_KEY @"term"

@synthesize data = _data;
@synthesize location = _location;

+(UpNextDataPocket*)sharedInstance
{
    static UpNextDataPocket *_sharedInstance = nil;
    static dispatch_once_t oncePredicate;
    dispatch_once(&oncePredicate, ^{
        _sharedInstance = [[UpNextDataPocket alloc] init];
        [_sharedInstance initialize];
    });
    
    return _sharedInstance;
}

-(void) initialize
{
    _data = [[NSMutableDictionary alloc] init];
    [_data setObject:@"food" forKey: KEYWORD_KEY];
    [_data setObject: @[] forKey:BUSINESS_KEY];
    
    _locationManager = [[CLLocationManager alloc] init];
    _locationManager.delegate = self;
    _locationManager.distanceFilter = kCLDistanceFilterNone;
    _locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    _location = [[CLLocation alloc] initWithLatitude: 30.283386 longitude:-97.738068];
    
     //Location is not needed yet.
     [_locationManager startMonitoringSignificantLocationChanges];
}

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation
{
    _location = newLocation;
}

- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error
{
    NSLog(@"Error: %@", [error description]);
    UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Could not get the current location."
                                                      message:@"Please enable the location service!"
                                                     delegate:nil
                                            cancelButtonTitle:@"OK"
                                            otherButtonTitles:nil];
    [message show];
}

-(NSArray*) businesses{
    return [_data objectForKey:BUSINESS_KEY];
}

-(void) updatePocketWithData:(NSData*)data
{
    NSError *error;
    NSDictionary *parsedData = [NSJSONSerialization JSONObjectWithData:data options:0 error: &error];
    
    if (error != nil || parsedData[@"Businesses"] == nil) {
        [_data setObject: @{@"businesses":@[]} forKey:BUSINESS_KEY];
        
        [[[UIAlertView alloc] initWithTitle:@"Response Error!"
                                   message:[error localizedDescription]
                                  delegate:nil
                         cancelButtonTitle:@"OK"
                         otherButtonTitles:nil] show];
        NSLog(@"RESPONSE ERROR! :%@",[error localizedDescription]);
    }
    else{
        [_data setObject: parsedData[@"Businesses"] forKey:BUSINESS_KEY];
    }
    //NSLog(data);
    
    
}

@end
