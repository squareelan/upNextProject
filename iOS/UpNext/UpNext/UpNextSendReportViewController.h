//
//  UpNextSendReportViewController.h
//  UpNext
//
//  Created by Juyong Kim on 2014. 3. 14..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UpNextSendReportViewController : UIViewController<UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) IBOutlet UIButton *currentLineReport;
@property (strong, nonatomic) IBOutlet UIButton *currentWaitReport;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UIImageView *updateImage;
@property (strong, nonatomic) IBOutlet UIImageView *busyImage;
@property (strong, nonatomic) IBOutlet UIImageView *notbusyImage;
@property (strong, nonatomic) IBOutlet UIImageView *cancelImage;
@property (strong, nonatomic) IBOutlet UIImageView *submitImage;
@property(strong,nonatomic) NSString *bizID;
- (IBAction)submitReport:(id)sender;

@end
