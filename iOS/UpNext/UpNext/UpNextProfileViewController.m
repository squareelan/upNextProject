//
//  UpNextSecondViewController.m
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 2. 27..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import <FacebookSDK/FacebookSDK.h>
#import "UpNextMainViewController.h"
#import "UpNextProfileViewController.h"

@interface UpNextProfileViewController ()

@property (strong, nonatomic) IBOutlet FBProfilePictureView *userImageView;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *emailLabel;

@end

@implementation UpNextProfileViewController
@synthesize userImageView;
@synthesize nameLabel, emailLabel;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    NSLog(@"Second View Loaded");
}

- (void) viewDidAppear:(BOOL)animated{
    [((UpNextMainViewController *) self.tabBarController.parentViewController).upNextActivityIndicator startAnimating];
    if ([FBSession.activeSession isOpen]) {
        [[FBRequest requestForMe] startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *user, NSError *error) {
             if (!error) {
                 nameLabel.text = user.name;
                 emailLabel.text = [user objectForKey:@"email"];
                 userImageView.profileID = [user objectForKey:@"id"];
                 userImageView.layer.cornerRadius =  userImageView.intrinsicContentSize.width/2;
                 userImageView.layer.masksToBounds = YES;
             }
         }];
        
        /*deprecated for now
        FBRequest *friendsRequest = [FBRequest requestForMyFriends];
        [friendsRequest startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary* result, NSError *error) {
             if (!error) {
                 NSArray* friends = [result objectForKey:@"data"];
                 friendsLabel.text = [NSString stringWithFormat:@"has %lu friends!",(unsigned long)friends.count];
             }
             dispatch_async(dispatch_get_main_queue(), ^{
                 [((UpNextMainViewController *) self.tabBarController.parentViewController).upNextActivityIndicator stopAnimating];
             });
         }];
         */

    }
    /*
    else{
        [[[UIAlertView alloc] initWithTitle:@"Login Required!" message:@"Please log in first." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil ] show];
    
        //[self.tabBarController setSelectedIndex:0];
        // [self.tabBarController setSelectedViewController:[self.tabBarController.viewControllers objectAtIndex:0]];
    }
     */
}

- (IBAction)loginBtn:(id)sender {

    if ([FBSession.activeSession isOpen]) {
        [FBSession.activeSession closeAndClearTokenInformation];
        [((UIButton *) sender) setTitle:@"Login" forState:UIControlStateNormal];
        nameLabel.text = nil;
        emailLabel.text = nil;
        userImageView.profileID = nil;
        
    }
    else {
        [self fbLogin];
        [((UIButton *) sender) setTitle:@"Logout" forState:UIControlStateNormal];
        [[FBRequest requestForMe] startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *user, NSError *error) {
             if (!error) {
                 nameLabel.text = user.name;
                 emailLabel.text = [user objectForKey:@"email"];
                 userImageView.profileID = [user objectForKey:@"id"];
                 userImageView.layer.cornerRadius =  userImageView.intrinsicContentSize.width/2;
                 userImageView.layer.masksToBounds = YES;
                 
                 
                 
             }
         }];
        
        /* deprecated for now
        FBRequest *friendsRequest = [FBRequest requestForMyFriends];
        [friendsRequest startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary* result, NSError *error) {
             if (!error) {
                 NSArray* friends = [result objectForKey:@"data"];
                 friendsLabel.text = [NSString stringWithFormat:@"has %lu friends!",(unsigned long)friends.count];
             }
             dispatch_async(dispatch_get_main_queue(), ^{
                 [((UpNextMainViewController *) self.tabBarController.parentViewController).upNextActivityIndicator stopAnimating];
             });
         }];
         */
    }
    
}

#pragma mark - Facebook Handlers
-(void)fbLogin
{
    if(![FBSession.activeSession isOpen])
    {
        [self fbResync];
        [FBSession openActiveSessionWithReadPermissions:@[@"basic_info",@"email",@"read_friendlists"]
                                           allowLoginUI:YES
                                      completionHandler:^(FBSession *session,
                                                          FBSessionState status,
                                                          NSError *error) {
                                          if ([session isOpen]) {
                                              NSLog(@"session open");
                                              [[FBRequest requestForMe] startWithCompletionHandler:
                                               ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *user, NSError *error) {
                                                   if (!error) {
                                                       nameLabel.text = user.name;
                                                       emailLabel.text = [user objectForKey:@"email"];
                                                       userImageView.profileID = [user objectForKey:@"id"];
                                                       userImageView.layer.cornerRadius =  userImageView.intrinsicContentSize.width/2;
                                                       userImageView.layer.masksToBounds = YES;
                                                       
                                                       
                                                       
                                                   }
                                               }];
                                              
                                              /*deprecated for now
                                              FBRequest *friendsRequest = [FBRequest requestForMyFriends];
                                              [friendsRequest startWithCompletionHandler:
                                               ^(FBRequestConnection *connection, NSDictionary* result, NSError *error) {
                                                   if (!error) {
                                                       NSArray* friends = [result objectForKey:@"data"];
                                                       friendsLabel.text = [NSString stringWithFormat:@"has %lu friends!",(unsigned long)friends.count];
                                                   }
                                                   dispatch_async(dispatch_get_main_queue(), ^{
                                                       [((UpNextMainViewController *) self.tabBarController.parentViewController).upNextActivityIndicator stopAnimating];
                                                   });
                                               }];

                                              */
                                              
                                          }
                                          else {
                                              NSLog(@"\nSeession NOT open. ERROR: %@\n\n",error.localizedDescription);
                                              // Session is closed
                                          }
                                          
                                          // [self.navigationController popViewControllerAnimated:YES];
                                          // Respond to session state changes,
                                          // ex: updating the view
                                      }];
    }
    else
    {
        UIAlertView *message = [[UIAlertView alloc] initWithTitle:nil
                                                          message:@"You are already logged in"
                                                         delegate:self
                                                cancelButtonTitle:@"OK"
                                                otherButtonTitles:nil,nil];
        [message show];
        [self.navigationController popViewControllerAnimated:YES];
        //[self performSegueWithIdentifier: @"fb_login" sender: self];
    }
    NSLog(@"session status: %d",[FBSession.activeSession isOpen]);
    NSString *accessToken = [[FBSession.activeSession accessTokenData] accessToken];
    NSLog(@"accesstoken: %@",accessToken);
    
}


-(void)fbResync
{
    ACAccountStore *accountStore;
    ACAccountType *accountTypeFB;
    if ((accountStore = [[ACAccountStore alloc] init]) && (accountTypeFB = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierFacebook] ) ){
        
        NSArray *fbAccounts = [accountStore accountsWithAccountType:accountTypeFB];
        id account;
        if (fbAccounts && [fbAccounts count] > 0 && (account = [fbAccounts objectAtIndex:0])){
            
            [accountStore renewCredentialsForAccount:account completion:^(ACAccountCredentialRenewResult renewResult, NSError *error) {
                //we don't actually need to inspect renewResult or error.
                if (error){
                    NSLog(@"\nEERRRROORR!!!!!!!!!!!!!!!!!: %@\n\n", error.description);
                    
                }
            }];
        }
    }
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
