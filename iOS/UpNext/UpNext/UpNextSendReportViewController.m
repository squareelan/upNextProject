//
//  UpNextSendReportViewController.m
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 3. 14..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import "UpNextSendReportViewController.h"
#import "UpNextFirstViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import "UpNextRequest.h"

@interface UpNextSendReportViewController (){
    
    IBOutlet UITableView *lineReportMenuTable;
    
    IBOutlet UITableView *waitReportMenuTable;
}


@end


@implementation UpNextSendReportViewController

@synthesize bizID = _bizID;

@synthesize updateImage,busyImage,notbusyImage,cancelImage,submitImage,currentLineReport,currentWaitReport;
NSString *crowdSelected = @"0";
NSString *waitSelected = @"0";
NSString *userReported = @"Anonymous";
NSString *busyness = @"-1";
Boolean busyClikced = false;
Boolean notBusyClikced = false;

//NSString *lineSize, *waitSize;

- (IBAction)reportLineBt:(id)sender {
    [UIView transitionWithView:lineReportMenuTable
                      duration:.7
                       options:UIViewAnimationOptionTransitionCurlDown
                    animations:NULL
                    completion:NULL];
    lineReportMenuTable.hidden = NO;
    currentWaitReport.hidden = YES;
}
- (IBAction)reportWaitBt:(id)sender {
    [UIView transitionWithView:waitReportMenuTable
                      duration:.7
                       options:UIViewAnimationOptionTransitionCurlDown
                    animations:NULL
                    completion:NULL];
    waitReportMenuTable.hidden = NO;
    currentWaitReport.hidden = YES;

}
- (IBAction)busyBt:(id)sender {
    if (!busyClikced) {
        busyImage.image = [UIImage imageNamed:@"button_busy_clicked_3.png"];
        notbusyImage.image = [UIImage imageNamed:@"button_not busy_3.png"];
        busyClikced = true;
        notBusyClikced = false;
        busyness = @"0";
    }
//    else{
//        busyImage.image = [UIImage imageNamed:@"button_busy_3.png"];
//        busyClikced = false;
//    }
}
- (IBAction)busyBtDown:(id)sender {
    if(!busyClikced)
    busyImage.image = [UIImage imageNamed:@"button_busy_onClick_3.png"];
}

- (IBAction)notBusyBt:(id)sender {
    if (!notBusyClikced) {
        busyImage.image = [UIImage imageNamed:@"button_busy_3.png"];
        notbusyImage.image = [UIImage imageNamed:@"button_not busy_clicked_3.png"];
        notBusyClikced = true;
        busyClikced = false;
        busyness =@"1";
    }
//    else{
//        notbusyImage.image = [UIImage imageNamed:@"button_not busy_3.png"];
//        notBusyClikced = false;
//    }
}
- (IBAction)notBusyBtDown:(id)sender {
    if(!notBusyClikced)
    notbusyImage.image = [UIImage imageNamed:@"button_not busy_onClick_3.png"];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    lineReportMenuTable.dataSource = self;
    lineReportMenuTable.delegate = self;
    waitReportMenuTable.dataSource = self;
    waitReportMenuTable.delegate = self;
    if ([FBSession.activeSession isOpen]) {
        [[FBRequest requestForMe] startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *user, NSError *error) {
             if (!error) {
                 userReported = [user.name stringByReplacingOccurrencesOfString:@" " withString:@"+"];
                 NSLog(@"Loged in user : %@",userReported);
             }
         }];
    }
    busyImage.image = [UIImage imageNamed:@"button_busy_3.png"];
    notbusyImage.image= [UIImage imageNamed:@"button_not busy_3"];
    submitImage.image = [UIImage imageNamed:@"button_submit_clicked_3"];
    cancelImage.image = [UIImage imageNamed:@"button_cancel_clicked_3.png"];
    updateImage.image = [UIImage imageNamed:@"update_image.png"];
    lineReportMenuTable.hidden = YES;
    waitReportMenuTable.hidden = YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)cancelBtDown:(id)sender {
    cancelImage.image = [UIImage imageNamed:@"button_cancel_3.png"];
}
- (IBAction)cancelBtcancel:(id)sender {
    cancelImage.image = [UIImage imageNamed:@"button_cancel_clicked_3.png"];
}
- (IBAction)cancelReport:(id)sender {
    CGRect mainrect = [[UIScreen mainScreen] bounds];
    CGRect newRect = CGRectMake(0, mainrect.size.height, mainrect.size.width, mainrect.size.height);
    cancelImage.image = [UIImage imageNamed:@"button_cancel_clicked_3.png"];
    self.view.frame = mainrect;
    [UIView animateWithDuration:0.2 animations:^{ self.view.frame = newRect; } completion:^(BOOL finished){
        if (finished) {
            [self.view removeFromSuperview];
            [self removeFromParentViewController];
        }
    }];

}

- (IBAction)submitBtDown:(id)sender {
    submitImage.image = [UIImage imageNamed:@"button_submit_3"];
}
- (IBAction)submitBtCancel:(id)sender {
    submitImage.image = [UIImage imageNamed:@"button_submit_clicked_3"];
}
- (IBAction)submitReport:(id)sender {
    CGRect mainrect = [[UIScreen mainScreen] bounds];
    CGRect newRect = CGRectMake(0, mainrect.size.height, mainrect.size.width, mainrect.size.height);
    self.view.frame = mainrect;
    submitImage.image = [UIImage imageNamed:@"button_submit_clicked_3"];

    
    //submitting waitlist by making request to the server
    
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://www.uhpnext.com/api/reportWaitTime?id=%@&wait=%@&user=%@&crowd=%@&busyness=%@",_bizID,waitSelected,userReported,crowdSelected,busyness]]];
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
    NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request
                                                        completionHandler:^(NSURL *localfile, NSURLResponse *response, NSError *error) {
                                                            if (!error) {                                                                   NSLog(@"WaitList Reported for %@",_bizID);
                                                                [((UpNextFirstViewController *)[self.view.superview nextResponder]) reloadTable];
                                                            }
                                                            else{
                                                                NSLog(@"Error = %@", error);
                                                                NSLog(error);
                                                            }
                                                        }];
    [task resume];
    
    
//    request = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://www.uhpnext.com/api/recordHistory?id=%@&first=%@&last=%@&biz=%@&date=%@",_bizID,waitSelected,userReported,crowdSelected]]];
//    NSLog([NSString stringWithFormat:@"http://www.uhpnext.com/api/reportWaitTime?id=%@&waitTime=%@&user=%@&trafficFlow=%@",_bizID,waitSelected,userReported,crowdSelected]);
//    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];
//    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
//    NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request
//                                                    completionHandler:^(NSURL *localfile, NSURLResponse *response, NSError *error) {
//                                                        if (!error) {                                                                   NSLog(@"WaitList Reported for %@",_bizID);
//                                                            [((UpNextFirstViewController *)[self.view.superview nextResponder]) reloadTable];
//                                                        }
//                                                        else{
//                                                            NSLog(@"Error = %@", error);
//                                                            NSLog(error);
//                                                        }
//                                                    }];
//    [task resume];
//
//    
    
    [UIView animateWithDuration:0.2 animations:^{ self.view.frame = newRect; } completion:^(BOOL finished){
        if (finished) {
            [self.view removeFromSuperview];
            [self removeFromParentViewController];
        }
    }];
}


#pragma mark - Table View Data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection: (NSInteger)section{
    return 5;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath: (NSIndexPath *)indexPath{
    
    if(tableView == lineReportMenuTable){
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"lineReportCell" forIndexPath:indexPath];

        if (cell == nil)
            cell = [[UITableViewCell alloc]initWithStyle: UITableViewCellStyleDefault reuseIdentifier:@"lineReportCell"];
        cell.textLabel.text = [self getCrowdSizeFromIndexPath:indexPath];
        return cell;
    }
    else if(tableView == waitReportMenuTable){
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"waitReportCell" forIndexPath:indexPath];
        if (cell == nil)
            cell = [[UITableViewCell alloc]initWithStyle: UITableViewCellStyleDefault reuseIdentifier:@"waitReportCell"];
        cell.textLabel.text = [self getWaitSizeFromIndexPath:indexPath];
        return cell;
    }
    else{
        NSLog(@"Unknow tableView error");
        return nil;
    }
}

-(NSString *)getWaitSizeFromIndexPath:(NSIndexPath *)indexPath{
    NSString *waitSize;
    switch (indexPath.row) {
        case 0:
            waitSize =@"0-5 mins";
            break;
        case 1:
            waitSize = @"5-10 mins";
            break;
        case 2:
            waitSize = @"10-20 mins";
            break;
        case 3:
            waitSize = @"20-30 mins";
            break;
        case 4:
            waitSize = @"More than 30 mins";
            break;
        default:
            break;
    }
    return waitSize;
}
-(NSString *)getCrowdSizeFromIndexPath:(NSIndexPath *)indexPath{
    NSString *crowdSize;
    switch (indexPath.row) {
        case 0:
            crowdSize = @"0-5 peoeple";
            break;
        case 1:
            crowdSize = @"5-10 peoeple";
            break;
        case 2:
            crowdSize = @"10-20 peoeple";
            break;
        case 3:
            crowdSize = @"20-30 peoeple";
            break;
        case 4:
            crowdSize = @"More than 30 people";
            break;
        default:
            break;
    }
    return crowdSize;
}

#pragma mark - TableView delegate
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath: (NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if(tableView ==  waitReportMenuTable){
        switch (indexPath.row) {
            case 0:
                waitSelected = @"1";
                break;
            case 1:
                waitSelected = @"2";
                break;
            case 2:
                waitSelected = @"3";
                break;
            case 3:
                waitSelected = @"4";
                break;
            case 4:
                waitSelected = @"5";
                break;
            default:
                break;
        }

        [UIView transitionWithView:tableView duration:.6 options:UIViewAnimationOptionTransitionCurlUp animations:NULL completion:NULL];
        waitReportMenuTable.hidden=YES;
        [currentWaitReport setTitle:[self getWaitSizeFromIndexPath:indexPath]
 forState:UIControlStateNormal];
        currentWaitReport.hidden = NO;
    }
    else if(tableView == lineReportMenuTable){
        switch (indexPath.row) {
            case 0:
                crowdSelected = @"1";
                break;
            case 1:
                crowdSelected = @"2";
                break;
            case 2:
                crowdSelected = @"3";
                break;
            case 3:
                crowdSelected = @"4";
                break;
            case 4:
                crowdSelected = @"5";
                break;
            default:
                break;
        }
        [UIView transitionWithView:tableView duration:1 options:UIViewAnimationOptionTransitionCrossDissolve animations:NULL completion:NULL];
        lineReportMenuTable.hidden=YES;
        [currentLineReport setTitle:[self getCrowdSizeFromIndexPath:indexPath]
                           forState:UIControlStateNormal];
        currentWaitReport.hidden = NO;
    }
}

@end
