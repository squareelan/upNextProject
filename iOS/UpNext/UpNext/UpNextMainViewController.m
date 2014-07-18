//
//  UpNextMainViewController.m
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 2. 28..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import <FacebookSDK/FacebookSDK.h>
#import "UpNextMainViewController.h"

@interface UpNextMainViewController ()

@end

@implementation UpNextMainViewController
@synthesize upNextActivityIndicator;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    upNextActivityIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    upNextActivityIndicator.frame = CGRectMake(0.0, 0.0, 40.0, 40.0);
    upNextActivityIndicator.center = self.view.center;
    upNextActivityIndicator.layer.zPosition = CGFLOAT_MAX;
    [self.view addSubview:upNextActivityIndicator];
    [upNextActivityIndicator bringSubviewToFront:self.view];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}




@end
