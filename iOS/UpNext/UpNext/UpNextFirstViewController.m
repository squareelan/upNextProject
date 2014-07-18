//
//  UpNextFirstViewController.m
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 2. 27..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import "UpNextFirstViewController.h"
#import "UpNextSendReportViewController.h"
#import "UpNextMainViewController.h"

#import "UpNextDataPocket.h"
#import "UpNextRequest.h"
#import "UpNextMainTableCell.h"

@interface UpNextFirstViewController ()
{
    IBOutlet UITableView *mainTable;
    IBOutlet UISearchBar *mainSearchBar;
    Boolean isHeartClicked;
    //IBOutlet UIButton *floatingBtn;
}
- (IBAction)reportBt:(id)sender;
@end

@implementation UpNextFirstViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    
  
    
    
    
    
    
    mainTable.delegate = self;
    mainTable.dataSource = self;
    mainSearchBar.delegate = self;
    //configure current Time
    isHeartClicked = false;
    [self reloadTable];
}


- (void) viewWillAppear:(BOOL)animated
{
    // 이거 안하면 탭 전환시 floating btn 이 사라짐.
    //[floatingBtn removeFromSuperview];
    //[self.view addSubview: floatingBtn];
    [self reloadTable];

}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



#pragma mark - Table View Data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection: (NSInteger)section{
    return [[[UpNextDataPocket sharedInstance] businesses] count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath: (NSIndexPath *)indexPath{
    
    static NSString *cellIdentifier = @"MainTableCell";
    UpNextMainTableCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil)
        cell = [[UpNextMainTableCell alloc]initWithStyle: UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
    
    
    NSDictionary *rowData = [[[UpNextDataPocket sharedInstance] businesses] objectAtIndex:indexPath.row];
    [cell updateWithDictionary:rowData];
    
    return cell;
}


#pragma mark - TableView delegate
// NOT Used at This Version
/*-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath: (NSIndexPath *)indexPath{
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    // self.modalPresentationStyle = UIModalPresentationCurrentContext;
    [self presentSendReportView:[[[UpNextDataPocket sharedInstance] businesses] objectAtIndex:indexPath.row]];
}
*/ 


#pragma mark - SearchBar delegate
- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar {
    [searchBar setShowsCancelButton:YES animated:YES];
    mainTable.allowsSelection = NO;
    mainTable.scrollEnabled = NO;
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {
    
    [searchBar setText:@""];
    [searchBar setShowsCancelButton:NO animated:YES];
    [searchBar resignFirstResponder];
    mainTable.allowsSelection = YES;
    mainTable.scrollEnabled = YES;
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    // [[_upNextData data] setObject:searchBar.text forKey:@"term"];
    [self reloadTable];
    
    [searchBar setText:@""];
    [searchBar setShowsCancelButton:NO animated:YES];
    [searchBar resignFirstResponder];
    mainTable.allowsSelection = YES;
    mainTable.scrollEnabled = YES;
}


#pragma mark - UpNextFirstViewController methods.


-(void)reloadTable
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = TRUE;
    [((UpNextMainViewController *) self.tabBarController.parentViewController).upNextActivityIndicator startAnimating];
    
    [NSURLConnection
     sendAsynchronousRequest:[UpNextRequest nearbyBusinessesWithTerm: [UpNextDataPocket sharedInstance].data[@"term"]
                                                             geoCode:[UpNextDataPocket sharedInstance].location]
     queue:[[NSOperationQueue alloc] init]
     completionHandler:^(NSURLResponse *response,
                         NSData *data,
                         NSError *error)
     {
         if (error != nil){
             UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Request Error!"
                                                               message:[error localizedDescription]
                                                              delegate:nil
                                                     cancelButtonTitle:@"OK"
                                                     otherButtonTitles:nil];
             [message show];
             NSLog(@"Error = %@", error);
         }
         else if ([data length] >0 && error == nil)
         {
             [[UpNextDataPocket sharedInstance] updatePocketWithData: data];             
             dispatch_async(dispatch_get_main_queue(), ^{
                 [UIApplication sharedApplication].networkActivityIndicatorVisible = FALSE;
                 [((UpNextMainViewController *) self.tabBarController.parentViewController).upNextActivityIndicator stopAnimating];
                 [mainTable reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationAutomatic];
             });
             
             NSLog(@"Table Asynchronously Reloaded!");
         }
         else if ([data length] == 0 && error == nil)
         {
             NSLog(@"Nothing was downloaded.");
         }
     }];
}

- (void) presentSendReportView: (NSDictionary*) data
{
    UpNextSendReportViewController *vc = [self.storyboard instantiateViewControllerWithIdentifier:@"SendReport"];
    [self addChildViewController: vc];
    [self.view addSubview: vc.view];
    
    CGRect mainrect = [[UIScreen mainScreen] bounds];
    CGRect newRect = CGRectMake(0, mainrect.size.height, mainrect.size.width, mainrect.size.height);
    vc.view.frame = newRect;
    [UIView animateWithDuration:0.3 animations:^{ vc.view.frame = mainrect; } completion:nil];
    NSString *namelocation =[NSString stringWithFormat:@"%@ %@", data[@"name"],data[@"location"]];
    [vc.nameLabel setText:namelocation];
    vc.bizID = data[@"_id"];
}

- (IBAction)reportBt:(id)sender {
    NSLog(@"Now reportController");
    NSIndexPath *indexPath = [self getIndexPathFromSender:sender];
    [self presentSendReportView:[[[UpNextDataPocket sharedInstance] businesses] objectAtIndex:indexPath.row]];
}
- (IBAction)heartBt:(id)sender {
    NSIndexPath *indexPath = [self getIndexPathFromSender:sender];
    NSDictionary *data = [[[UpNextDataPocket sharedInstance] businesses] objectAtIndex:indexPath.row];
    NSString *bizid = data[@"_id"];
    
    if (isHeartClicked) {
        NSLog(@"heart unclikced");
        isHeartClicked = false;
    }
    else{
        NSLog(@"heart unclikced");
        isHeartClicked =true;
    }
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://www.uhpnext.com/api/updateHearts?id=%@&plus=%s",bizid,isHeartClicked ? "true" : "false"]]];
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
    NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request
                                                    completionHandler:^(NSURL *localfile, NSURLResponse *response, NSError *error) {
                                                        if (!error) {
                                                            [self reloadTable];NSLog(@"heart updated-%@",bizid);
                                                            
                                                        }
                                                        else{
                                                            NSLog(@"Error = %@", error);
                                                            NSLog(error);
                                                        }
                                                    }];
    [task resume];

}


-(NSIndexPath *)getIndexPathFromSender:(id)sender{
    if (!sender) {
        return nil;
    }
    if ([sender isKindOfClass:[UpNextMainTableCell class]]){
        UpNextMainTableCell *cell = sender;
        return [mainTable indexPathForCell:cell];
    }
    return [self getIndexPathFromSender:((UIView *)[sender superview])];
}


@end
