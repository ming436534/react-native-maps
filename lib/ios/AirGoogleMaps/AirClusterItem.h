//
//  AirClusterItem.h
//
//  Created by Ming on 25/10/2019.
//

#if defined(HAVE_GOOGLE_MAPS) && defined(HAVE_GOOGLE_MAPS_UTILS)

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

#import <Google-Maps-iOS-Utils/GMUMarkerClustering.h>
#import <GoogleMaps/GoogleMaps.h>

@interface AirClusterItem : NSObject<GMUClusterItem>

@property(nonatomic, readonly) CLLocationCoordinate2D position;
@property(nonatomic, strong) NSString *name;
@property(nonatomic, strong) NSString *identifier;
@property(nonatomic, strong) NSString *iconUri;

- (instancetype)initWithPosition:(CLLocationCoordinate2D)position
                            name:(NSString *)name
                      identifier:(NSString *)identifier
                         iconUri:(NSString *)iconUri;

@end


#endif
