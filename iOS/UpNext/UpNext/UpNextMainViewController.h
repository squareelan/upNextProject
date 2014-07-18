//
//  UpNextMainViewController.h
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 2. 28..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UpNextMainViewController : UIViewController

@property (strong, atomic) UIActivityIndicatorView *upNextActivityIndicator;
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
- (IBAction)loginBtn:(id)sender;

@end
