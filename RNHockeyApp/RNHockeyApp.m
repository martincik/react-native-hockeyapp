#import "RNHockeyApp.h"
#import "RCTEventDispatcher.h"

static BOOL initialized = NO;
static BOOL autoSend = YES;
static NSString *token = nil;

@implementation RNHockeyApp

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(configure:(NSString *) apiToken autoSend:(BOOL) autoSendCrashes)
{
  if (initialized == NO) {
    autoSend = autoSendCrashes;
    token = apiToken;
    initialized = YES;
  } else {
    NSLog(@"Already initialized! \n");
  }
}

RCT_EXPORT_METHOD(start) {
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:token];
    if (autoSend == YES) {
      [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus:BITCrashManagerStatusAutoSend];
    }
    [[BITHockeyManager sharedHockeyManager] startManager];
    [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
  }
}


RCT_EXPORT_METHOD(feedback)
{
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager].feedbackManager showFeedbackListView];
  } else {
    NSLog(@"Not initialized! \n");
  }
}

RCT_EXPORT_METHOD(checkForUpdate)
{
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager].updateManager checkForUpdate];
  } else {
    NSLog(@"Not initialized! \n");
  }
}

RCT_EXPORT_METHOD(generateTestCrash)
{
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager].crashManager generateTestCrash];
  }
}

@end
