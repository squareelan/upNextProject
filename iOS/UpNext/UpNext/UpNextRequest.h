//
//  UpNextRequest.h
//  UpNextian
//
//  Created by Juyong Kim, Yongjun Yoo on 13. 10. 17..
//  Copyright (c) 2013년 UpNext. All rights reserved.
//

#import <Foundation/Foundation.h>

@class CLLocation;
@interface UpNextRequest : NSMutableURLRequest

+(UpNextRequest*) getRequest:(NSString*)api withParams: (NSDictionary*)params;
+(UpNextRequest*) postRequest:(NSString*)api withBody: (NSMutableDictionary*)body;
+(UpNextRequest*) nearbyBusinessesWithTerm:(NSString*) term geoCode:(CLLocation*) geoCode;

+(id) SynchronouslyProcessRequest:(UpNextRequest *)request;
@end

