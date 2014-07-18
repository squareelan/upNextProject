//
//  UpNextDataPocket.h
//  UpNextian
//
//  Created by Juyong Kim, Yongjun Yoo on 2013. 11. 5..
//  Copyright (c) 2013년 UpNext. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface UpNextDataPocket : NSObject <NSURLConnectionDelegate, CLLocationManagerDelegate>
{
    NSMutableData *_responseData;
    CLLocationManager *_locationManager;
}

+(UpNextDataPocket*)sharedInstance;

@property (strong, nonatomic) NSMutableDictionary *data;
@property (strong, nonatomic) CLLocation *location;

-(NSArray*) businesses;
-(void) updatePocketWithData:(NSData*)data;
@end
