//
//  AirGMUClusterRenderer.h
//  kkfly
//
//  Created by Ming on 25/10/2019.
// 
//

#ifndef AirGMUClusterRenderer_h
#define AirGMUClusterRenderer_h
#if defined(HAVE_GOOGLE_MAPS) && defined(HAVE_GOOGLE_MAPS_UTILS)

#import <Foundation/Foundation.h>

#import "AirClusterItem.h"
#import "GMUClusterRenderer.h"
#import <Google-Maps-iOS-Utils/GMUMarkerClustering.h>
#import <React/RCTBridge.h>

NS_ASSUME_NONNULL_BEGIN

@class GMSMapView;
@class GMSMarker;

@protocol GMUCluster;
@protocol GMUClusterIconGenerator;
@protocol GMUClusterRenderer;

/**
 * Default cluster renderer which shows clusters as markers with specialized icons.
 * There is logic to decide whether to expand a cluster or not depending on the number of
 * items or the zoom level.
 * There is also some performance optimization where only clusters within the visisble
 * region are shown.
 */
@interface AirGMUClusterRenderer : NSObject<GMUClusterRenderer>

/**
 * Creates a new cluster renderer with a given map view and icon generator.
 *
 * @param mapView The map view to use.
 * @param iconGenerator The icon generator to use. Can be subclassed if required.
 */
- (instancetype)initWithMapView:(GMSMapView *)mapView
           clusterIconGenerator:(id<GMUClusterIconGenerator>)iconGenerator;

/**
 * Animates the clusters to achieve splitting (when zooming in) and merging
 * (when zooming out) effects:
 * - splitting large clusters into smaller ones when zooming in.
 * - merging small clusters into bigger ones when zooming out.
 *
 * NOTES: the position to animate to/from for each cluster is heuristically
 * calculated by finding the first overlapping cluster. This means that:
 * - when zooming in:
 *    if a cluster on a higher zoom level is made from multiple clusters on
 *    a lower zoom level the split will only animate the new cluster from
 *    one of them.
 * - when zooming out:
 *    if a cluster on a higher zoom level is split into multiple parts to join
 *    multiple clusters at a lower zoom level, the merge will only animate
 *    the old cluster into one of them.
 * Because of these limitations, the actual cluster sizes may not add up, for
 * example people may see 3 clusters of size 3, 4, 5 joining to make up a cluster
 * of only 8 for non-hierachical clusters. And vice versa, a cluster of 8 may
 * split into 3 clusters of size 3, 4, 5. For hierarchical clusters, the numbers
 * should add up however.
 *
 * Defaults to YES.
 */
@property(nonatomic) BOOL animatesClusters;

/**
 * Determines the minimum number of cluster items inside a cluster.
 * Clusters smaller than this threshold will be expanded.
 *
 * Defaults to 4.
 */
@property(nonatomic) NSUInteger minimumClusterSize;

/**
 * Sets the maximium zoom level of the map on which the clustering
 * should be applied. At zooms above this level, clusters will be expanded.
 * This is to prevent cases where items are so close to each other than they
 * are always grouped.
 *
 * Defaults to 20.
 */
@property(nonatomic) NSUInteger maximumClusterZoom;

/**
 * Sets the animation duration for marker splitting/merging effects.
 * Measured in seconds.
 *
 * Defaults to 0.5.
 */
@property(nonatomic) double animationDuration;

/**
 * Allows setting a zIndex value for the clusters.  This becomes useful
 * when using multiple cluster data sets on the map and require a predictable
 * way of displaying multiple sets with a predictable layering order.
 *
 * If no specific zIndex is not specified during the initialization, the
 * default zIndex is '1'.  Larger zIndex values are drawn over lower ones
 * similar to the zIndex value of GMSMarkers.
 */
@property(nonatomic) int zIndex;

/** Sets to further customize the renderer. */
@property(nonatomic, nullable, weak) id<GMUClusterRendererDelegate> delegate;
@property(nonatomic, weak) RCTBridge *bridge;

/**
 * If returns NO, cluster items will be expanded and rendered as normal markers.
 * Subclass can override this method to provide custom logic.
 */
- (BOOL)shouldRenderAsCluster:(id<GMUCluster>)cluster atZoom:(float)zoom;

@end

NS_ASSUME_NONNULL_END

#endif
#endif /* AirGMUClusterRenderer_h */
