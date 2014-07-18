//
//  UpNextRequest.m
//  UpNextian
//
//  Created by Juyong Kim, Yongjun Yoo on 13. 10. 17..
//  Copyright (c) 2013년 UpNext. All rights reserved.
//

#import "UpNextRequest.h"
#import <CoreLocation/CoreLocation.h>

@implementation UpNextRequest

/*  Always terminate the url with '/'  */
#define SERVER_URL @"http://www.uhpnext.com/"
#define CAMPUS_BIZ_QUARY @"api/campus_biz/"
#define REPORT_WAITTIME @"api/reportWaitTime/"
// #define NEARBY_BUSINESSES_QUARY @"api/find-business-geo/" // Deprecated

+(UpNextRequest*) nearbyBusinessesWithTerm:(NSString*) term geoCode:(CLLocation*) location
{
    NSString *geoCode = [NSString stringWithFormat:@"%f,%f",location.coordinate.latitude,location.coordinate.longitude];
    return [UpNextRequest getRequest: CAMPUS_BIZ_QUARY withParams:@{@"term":term,@"geocode":geoCode}];
}

+(UpNextRequest*) request:(NSString*)url ofMethod:(NSString*)method withBody: (NSMutableDictionary*)body withParams: (NSMutableDictionary*)params {
    
    UpNextRequest *request;
    NSError *error;
    
    NSMutableString *requestURL = [NSMutableString stringWithFormat:@"%@%@",SERVER_URL,url];
    
    if (params!=nil && ![params isEqualToDictionary:@{}]) {
        [requestURL appendString:@"?"];
        
        NSEnumerator *keyEnum = [params keyEnumerator];
        NSString *key = [keyEnum nextObject];
        while (key) {
            [requestURL appendFormat:@"%@=%@",key,[params objectForKey:key]];
            if (key=[keyEnum nextObject])
                [requestURL appendString:@"&"];
        }
    }
    request = [UpNextRequest requestWithURL:[NSURL URLWithString:[requestURL stringByReplacingOccurrencesOfString:@" " withString:@"+"]]];
    NSLog(@"making request: with %@", request);
    if (body!=nil) {
        if ([method  isEqual: @"GET"])
            [NSException exceptionWithName:@"UpNextRequest Exception" reason:@"'GET' method request does not support http body" userInfo:nil];
        
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:body options:NSJSONWritingPrettyPrinted error:&error];
        [request setHTTPBody:jsonData];
    }
    
    
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPMethod: method];
    [request setTimeoutInterval:10];
    
    // NSData *data = [ NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&requestError];
    // NSDictionary *theReply = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    
    /*
    // DEBUG AREA
    NSLog( @"\n\ntheReply:" );
    NSLog( [theReply debugDescription] );
    NSLog( @"\n\nurlResponse:" );
    NSLog( [urlResponse debugDescription] );
    NSLog( @"\n\nrequestError:" );
    NSLog( [requestError debugDescription] );
    */

    return request;
}

+(UpNextRequest*) getRequest:(NSString*)url withParams: (NSMutableDictionary*)params{
    return [self request:url ofMethod:@"GET" withBody:nil withParams:params];
}

+(UpNextRequest*) postRequest:(NSString*)url withBody: (NSMutableDictionary*)body{
    return [self request:url ofMethod:@"POST" withBody:body withParams:nil];
}


+(id) SynchronouslyProcessRequest:(UpNextRequest *)request
{
    
    NSError *requestError = nil;
    NSURLResponse *urlResponse = nil;
    
    NSData *data = [ NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&requestError];
    
    if (requestError == nil) {
        return [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    } else {
        NSLog(@"Synchrounous Request Error: %@", requestError);
        UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Request Error!"
                                                          message:[requestError localizedRecoverySuggestion]
                                                         delegate:nil
                                                cancelButtonTitle:@"OK"
                                                otherButtonTitles:nil];
        [message show];
        return @{};
    }
    
}

@end
