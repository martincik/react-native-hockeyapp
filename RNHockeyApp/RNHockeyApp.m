#import "RNHockeyApp.h"
#import "RCTEventDispatcher.h"

@implementation RNHockeyApp

- (id)init
{
  self = [super init];
  initialized = NO;
  autoSend = YES;
  return self;
}

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

RCT_EXPORT_METHOD(configure:(NSString *) apiToken autoSend:(BOOL) autoSendCrashes)
{
  if (initialized == NO)
    autoSend = autoSendCrashes;
    token = apiToken;
    initialized = YES;
  }
}

RCT_EXPORT_METHOD(start:(NSString *) token)
{
  [self start:token autoSend:YES];
}

RCT_EXPORT_METHOD(feedback)
{
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager].feedbackManager showFeedbackListView];
  }
}

RCT_EXPORT_METHOD(checkForUpdate)
{
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager].updateManager checkForUpdate];
  }
}

RCT_EXPORT_METHOD(generateTestCrash)
{
  [[BITHockeyManager sharedHockeyManager].crashManager generateTestCrash];
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

+ (BOOL)applicationDidFinishLaunching {
  if (initialized == YES) {
    [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:token];
    if (autoSend == YES) {
      [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus:BITCrashManagerStatusAutoSend];
    }
    [[BITHockeyManager sharedHockeyManager] startManager];
    [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
  }

  return YES;
}

@end
