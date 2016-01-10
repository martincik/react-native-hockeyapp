#ifndef RN_HockeyApp_h
#define RN_HockeyApp_h

#import "RCTBridgeModule.h"
#import <HockeySDK/HockeySDK.h>

@interface RNHockeyApp : NSObject<RCTBridgeModule> {
  BOOL initialized;
  NSString *token;
  BOOL autoSend;
}

+ (BOOL)applicationDidFinishLaunching;

@end

#endif
