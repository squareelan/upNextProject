//
//  UpNextMainTableCell.h
//  UpNext
//
//  Created by Juyong Kim, Yongjun Yoo on 2014. 3. 15..
//  Copyright (c) 2014년 UpNext. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UpNextMainTableCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UILabel *updatedLabel;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UIImageView *update;
@property (strong, nonatomic) IBOutlet UIImageView *heart;
@property (strong, nonatomic) IBOutlet UIImageView *flow;
@property (strong, nonatomic) IBOutlet UIImageView *crowd;
@property (strong, nonatomic) IBOutlet UIImageView *timeW;
@property (strong, nonatomic) IBOutlet UIImageView *profilePic;
@property (strong, nonatomic) IBOutlet UIView *wrapper;
@property (strong, nonatomic) IBOutlet UILabel *userName;
@property (strong, nonatomic) IBOutlet UILabel *likes;
@property (strong, nonatomic) IBOutlet UILabel *distanceLabel;
@property (strong, nonatomic) IBOutlet UILabel *timeDiff;
@property (strong, nonatomic) IBOutlet UIImageView *heartImage;

-(void) updateWithDictionary: (NSDictionary*) data;
@end
