//
//  AirClusterItem.m
//
//  Created by Ming on 25/10/2019.
//

#ifdef HAVE_GOOGLE_MAPS

#import <Foundation/Foundation.h>
#import "AirClusterItem.h"

@implementation AirClusterItem
- (instancetype)initWithPosition:(CLLocationCoordinate2D)position
                            name:(NSString *)name
                      identifier:(NSString *)identifier
                         iconUri:(NSString *)iconUri {
  self = [super init];

  if (self) {
    _position = position;
    self.iconUri = iconUri;
    self.name = name;
    self.identifier = identifier;
  }

  return self;
}
@end
#endif
