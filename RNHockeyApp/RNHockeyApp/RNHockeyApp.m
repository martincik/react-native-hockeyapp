#import "RNHockeyApp.h"

#if __has_include(<React/RCTEventDispatcher.h>)

#import <React/RCTEventDispatcher.h>

#else

#import "RCTEventDispatcher.h"

#endif

static BOOL initialized = NO;
static BOOL autoSend = YES;
static AuthType authType = 0;
static NSString *token = nil;
static NSString *appSecret = nil;

#if HOCKEYSDK_FEATURE_CRASH_REPORTER
@interface RNHockeyApp() <BITHockeyManagerDelegate, BITCrashManagerDelegate>
@end
#else
@interface RNHockeyApp() <BITHockeyManagerDelegate>
@end
#endif

@implementation RNHockeyApp

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(configure:(NSString *) apiToken autoSend:(BOOL) autoSendCrashes authType:(NSInteger) apiAuthType appSecret:(NSString*) apiAppSecret ignoreDefaultHandler:(BOOL) ignoreDefaultCrashHandler)
{
    if (initialized == NO) {
        autoSend = autoSendCrashes;
        token = apiToken;
        authType = apiAuthType;
        appSecret = apiAppSecret;
        initialized = YES;
    } else {
        NSLog(@"Already initialized! \n");
    }
}

RCT_EXPORT_METHOD(start) {
    if (initialized == YES) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:token
                                                                   delegate:self];
            if (autoSend == YES) {
#if HOCKEYSDK_FEATURE_CRASH_REPORTER
                [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus:BITCrashManagerStatusAutoSend];
#else
                NSLog(@"CrashManager not included in HockeyApp SDK installation! \n");
#endif
            }

            [RNHockeyApp setupAuthenticator];
            [[BITHockeyManager sharedHockeyManager] startManager];

            [RNHockeyApp deleteMetadataFileIfExists];
        });
    }
}

RCT_EXPORT_METHOD(addMetadata:(NSData*) metadata)
{
    if (initialized == YES) {
        NSDictionary *newMetadata = [NSJSONSerialization JSONObjectWithData:metadata options:0 error:nil];

        if (!newMetadata) {
           NSLog(@"react-native-hockeyapp: the metadata is not valid JSON.");
           return;
        }

        NSMutableDictionary *allMetadata = [RNHockeyApp getExistingMetadata];
        [allMetadata addEntriesFromDictionary:newMetadata];
        NSData *json = [NSJSONSerialization dataWithJSONObject:allMetadata options:0 error:nil];

        if (json) {
            NSString *filePath = [RNHockeyApp getMetadataFilePath];
            [json writeToFile:filePath atomically:YES];
        }
    } else {
        NSLog(@"Not initialized! \n");
    }
}

RCT_EXPORT_METHOD(feedback)
{
#if HOCKEYSDK_FEATURE_FEEDBACK
    if (initialized == YES) {
        [[BITHockeyManager sharedHockeyManager].feedbackManager showFeedbackListView];
    } else {
        NSLog(@"Not initialized! \n");
    }
#else
    NSLog(@"Feedback not included in HockeyApp SDK installation! \n");
#endif
}

RCT_EXPORT_METHOD(checkForUpdate)
{
#if HOCKEYSDK_FEATURE_UPDATES
    if (initialized == YES) {
        [[BITHockeyManager sharedHockeyManager].updateManager checkForUpdate];
    } else {
        NSLog(@"Not initialized! \n");
    }
#else
    NSLog(@"Updates not included in HockeyApp SDK installation! \n");
#endif
}

RCT_EXPORT_METHOD(generateTestCrash)
{
#if HOCKEYSDK_FEATURE_CRASH_REPORTER
    if (initialized == YES) {
        [[BITHockeyManager sharedHockeyManager].crashManager generateTestCrash];
    }
#else
    NSLog(@"CrashManager not included in HockeyApp SDK installation! \n");
#endif
}

RCT_EXPORT_METHOD(trackEvent:(NSString *)eventName)
{
#if HOCKEYSDK_FEATURE_METRICS
    if (initialized == YES) {
        if ([eventName length] > 0) {
            BITMetricsManager *metricsManager = [[BITHockeyManager sharedHockeyManager] metricsManager];
            [metricsManager trackEventWithName:eventName];
        } else {
            NSLog(@"react-native-hockeyapp: An event name must be provided.");
        }
    }
#else
    NSLog(@"Metrics not included in HockeyApp SDK installation! \n");
#endif
}

RCT_EXPORT_METHOD(trackEventWithOptionsAndMeasurements:(NSString *)eventName Options:(NSDictionary *)options Measurements:(NSDictionary *)measurements)
{
#if HOCKEYSDK_FEATURE_METRICS
  if (initialized == YES) {
    if ([eventName length] > 0) {
      BITMetricsManager *metricsManager = [[BITHockeyManager sharedHockeyManager] metricsManager];
      [metricsManager trackEventWithName:eventName properties:options measurements:measurements];
    } else {
      NSLog(@"react-native-hockeyapp: An event name must be provided.");
    }
  }
#else
    NSLog(@"Metrics not included in HockeyApp SDK installation! \n");
#endif
}

+ (void)setupAuthenticator
{
#if HOCKEYSDK_FEATURE_AUTHENTICATOR
    switch (authType) {
        case EmailSecret:
            NSLog(@"react-native-hockeyapp: Email + Secret Auth set");
            [[BITHockeyManager sharedHockeyManager].authenticator setAuthenticationSecret:appSecret];
            [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:BITAuthenticatorIdentificationTypeHockeyAppEmail];
            break;
        case EmailPassword:
            NSLog(@"react-native-hockeyapp: Email + Password Auth set");
            [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:BITAuthenticatorIdentificationTypeHockeyAppUser];
            break;
        case DeviceUUID:
            NSLog(@"react-native-hockeyapp: Device UUID Auth set");
            [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:BITAuthenticatorIdentificationTypeDevice];
            break;
        case WebAuth:
            NSLog(@"react-native-hockeyapp: Web Auth set");
            [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:BITAuthenticatorIdentificationTypeWebAuth];
            break;
        case Anonymous:
        default:
            NSLog(@"react-native-hockeyapp: Anonymous Auth set");
            [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:BITAuthenticatorIdentificationTypeAnonymous];
            break;
    }
    [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
#else
    NSLog(@"Authenticator not included in HockeyApp SDK installation! \n");
#endif
}

+ (void)deleteMetadataFileIfExists
{
    NSString *filePath = [RNHockeyApp getMetadataFilePath];

    [[NSFileManager defaultManager] removeItemAtPath:filePath error:nil];
}

+ (NSMutableDictionary *)getExistingMetadata
{
    NSString *filePath = [RNHockeyApp getMetadataFilePath];
    NSData *data = [NSData dataWithContentsOfFile:filePath];
    NSMutableDictionary *dictionary = nil;

    if (data) {
        dictionary = [[NSJSONSerialization JSONObjectWithData:data options:0 error:nil] mutableCopy];
    }

    if (!dictionary) {
        dictionary = [NSMutableDictionary new];
    }

    return dictionary;
}

+ (NSString *)getMetadataFilePath
{
    BOOL expandTilde = YES;
    NSString *directoryPath = [NSSearchPathForDirectoriesInDomains (NSLibraryDirectory, NSUserDomainMask, expandTilde) objectAtIndex:0];

    return [directoryPath stringByAppendingPathComponent:@"HockeyAppCrashMetadata.json"];
}

#if HOCKEYSDK_FEATURE_CRASH_REPORTER
- (NSString *)applicationLogForCrashManager:(BITCrashManager *)crashManager
{
    NSString *filePath = [RNHockeyApp getMetadataFilePath];

    return [NSString stringWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:nil];
}
#endif
@end
