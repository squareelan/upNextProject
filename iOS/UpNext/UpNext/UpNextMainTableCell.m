//
//  UpNextMainTableCell.m
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 3. 15..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import "UpNextMainTableCell.h"
#import "UpNextSendReportViewController.h"
#import "UpNextFirstViewController.h"
#import "UpNextDataPocket.h"

Boolean *heartClicked = NO;
NSString *bizid;
@implementation UpNextMainTableCell

@synthesize updatedLabel, nameLabel,userName;
@synthesize  update, heart,crowd,timeW,profilePic,flow,wrapper,likes,distanceLabel,timeDiff,heartImage;


- (IBAction)heartBt:(id)sender {
    if(!heartClicked){
        heartClicked = YES;
        heart.image = [UIImage imageNamed:@"button_heart_clicked_3"];
    }
    else{
        heartClicked = NO;
        heart.image = [UIImage imageNamed:@"button_heart_unclicked_3"];
    }
}
- (IBAction)hearBtcancel:(id)sender {
    if(heartClicked)
        heart.image = [UIImage imageNamed:@"button_heart_clicked_3"];
    else
        heart.image = [UIImage imageNamed:@"button_heart_unclicked_3"];

}
- (IBAction)heartBtDown:(id)sender {
    heart.image = [UIImage imageNamed:@"button_heart_onClick_3"];
}

- (IBAction)reportBtDown:(id)sender {
    update.image = [UIImage imageNamed:@"button_Update_onClicked_3"];
}
- (IBAction)reportBtCancel:(id)sender {
    update.image = [UIImage imageNamed:@"button_Update_unclicked_3"];
}
- (IBAction)reportBt:(id)sender {
    update.image = [UIImage imageNamed:@"button_Update_unclicked_3"];
}



- (void) updateWithDictionary:(NSDictionary *)data
{
    //bizid = data[@"_id"];
    heartImage.image = [UIImage imageNamed:@"heart_transparent BG_3.png"];
    [self lastReportedTime:data];
    UpNextDataPocket *dp = [UpNextDataPocket sharedInstance];
    
    NSArray *geo = data[@"geocode"];
    int latdest = [geo[0] intValue];
    int longdest = [geo[1] intValue];
    
    CLLocation *loc2 = [[CLLocation alloc] initWithLatitude:latdest longitude:longdest];
    CLLocationDistance distance = [loc2 distanceFromLocation: dp.location]/1609.344;
    nameLabel.text = [NSString stringWithFormat:@"%@",data[@"name"]];
    updatedLabel.text = data[@"category"];
    if([data[@"reportedBy"]isEqual: @"-1"]){
        //NSLog(@"%@ has no data ", data[@"name"]);
        userName.hidden = YES;
    }
    else{userName.text = data[@"reportedBy"];userName.hidden = NO;}
    distanceLabel.text = [NSString stringWithFormat:@"%.2lf mi",distance/1609.34];
    NSLog(@"%@",data[@"hearts"]);
    
    likes.text =  [NSString stringWithFormat:@"%@",data[@"hearts"]];
    
    //trafficFlow image implementation
    if(![data[@"crowd"] isEqual: [NSNull null]]){
        int currentCrowd = [data[@"crowd"] intValue];
        switch (currentCrowd) {
            case 1:
                crowd.image = [UIImage imageNamed:@"0-5ppl_2.png"];
                break;
            case 2:
                crowd.image = [UIImage imageNamed:@"5-10ppl_3.png"];
                break;
            case 3:
                crowd.image = [UIImage imageNamed:@"10-20ppl_2.png"];
                break;
            case 4:
                crowd.image = [UIImage imageNamed:@"20-30ppl_2-02.png"];
                break;
            case 5:
                crowd.image = [UIImage imageNamed:@"30+ppl_2-02.png"];
                break;
            default:
                NSLog(@"Wrong crowd size from server");
                break;
        }
    }
    else{
        crowd.image = [UIImage imageNamed:@"0-5ppl_2.png"];
        //crowd.image = nil;
    }
    //WaitTime image implementation
    if(![data[@"wait"] isEqual: [NSNull null]]){
        int currentWait = [data[@"wait"] intValue];
        switch (currentWait) {
            case 1:
                timeW.image = [UIImage imageNamed:@"0-5 min_3.png"];
                break;
            case 2:
                timeW.image = [UIImage imageNamed:@"5-10 mins_3.png"];
                break;
            case 3:
                timeW.image = [UIImage imageNamed:@"10-20 mins_3.png"];
                break;
            case 4:
                timeW.image = [UIImage imageNamed:@"20-30 mins_3.png"];
                break;
            case 5:
                timeW.image = [UIImage imageNamed:@"30+ mins_3.png"];
            default:
                NSLog(@"Wrong wait Time from server");
                break;
        }
    }
    else{
        //crowd.image = [UIImage imageNamed:@"0-5ppl_2.png"];
        //timeW.image = nil;
        timeW.image = [UIImage imageNamed:@"0-5 min_3.png"];
    }
    
    
    heart.image = [UIImage imageNamed:@"button_heart_unclicked_3"];
    update.image = [UIImage imageNamed:@"button_Update_unclicked_1"];
    
    //busyness image
    if (![data[@"busyness"] isEqual: [NSNull null]]) {
        if ([data[@"busyness"] isEqualToString:@"-1"]) {
            flow.image = nil;
        }
        else if([data[@"busyness"] isEqualToString:@"0"]){
            flow.image = [UIImage imageNamed:@"busy_2.png"];
        }
        else{
            flow.image = [UIImage imageNamed:@"not busy_2.png"];
        }
    }
    
    if([data[@"reportedBy"]isEqualToString:@"Anonymous"]| [data[@"reportedBy"]isEqualToString:@"-1"]){
        profilePic.image = [UIImage imageNamed:@"anonymous_3"];
    }
    else profilePic.image = [UIImage imageNamed:@"profilepic.png"];
    profilePic.layer.cornerRadius =  profilePic.bounds.size.width/2;
    profilePic.layer.masksToBounds = YES;
    [heart.layer setBorderColor: [[UIColor grayColor] CGColor]];
    [heart.layer setBorderWidth: .3];
    [update.layer setBorderColor: [[UIColor grayColor] CGColor]];
    [update.layer setBorderWidth: .3];
    [flow.layer setBorderColor: [[UIColor grayColor] CGColor]];
    [flow.layer setBorderWidth: .3];
    [wrapper.layer setBorderColor:[[UIColor grayColor] CGColor]];
    [wrapper.layer setBorderWidth:.4];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)lastReportedTime:(NSDictionary*)data{
    NSDate *currentTime = [NSDate date];
    NSDateFormatter *dateformatter = [[NSDateFormatter alloc]init];
    dateformatter.dateFormat = @"HH:mm";
    [dateformatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"CST"]];
    NSArray *temp = [[dateformatter stringFromDate:currentTime] componentsSeparatedByString:@":"];
    int phonehour = [[temp objectAtIndex:0] intValue];
    int phoneminute = [[temp objectAtIndex:1] intValue];
    temp = [data[@"lastReported"] componentsSeparatedByString:@":"];
    int datahour = [[temp objectAtIndex:0]intValue];
    if(datahour!=-1){
        int dataminute =[[temp objectAtIndex:1]intValue];
        int hourDiff = phonehour - datahour;
        int minDiff = phoneminute - dataminute;
        if(minDiff<0){
            hourDiff--;
            minDiff+=60;
        }
        if (hourDiff<0) {
            NSLog(@"Wrong Time difference for %@",data[@"name"]);
        }
        if(hourDiff==0)
        {          timeDiff.text = [NSString stringWithFormat:@"%d mins ago",minDiff];
            NSLog(@"current time %d:%d, %d:%d",phonehour,phoneminute,datahour,dataminute);}
        else
        {            timeDiff.text = [NSString stringWithFormat:@"%d hours ago",hourDiff];
            NSLog(@"current time %d:%d, %d:%d",phonehour,phoneminute,datahour,dataminute);}
    }
    else{
        NSLog(@"no Time report for %@",data[@"name"]);
        timeDiff.text = nil;
    }
    
//debugging purpose
//    NSLog(@"current time %d:%d, %d:%d",phonehour,phoneminute,datahour,dataminute);
//   ////    NSLog(@"current time Diff is : %@",[dateformatter stringFromDate:currentTime]);
//    NSLog(@"current time Diff %@ is : %d:%d %d %d ",data[@"name"],hourDiff,minDiff,datahour,dataminute );
   
}


@end
