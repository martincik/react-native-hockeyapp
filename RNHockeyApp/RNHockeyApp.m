#import "RNHockeyApp.h"
#import "RCTEventDispatcher.h"

static BOOL initialized = NO;
static BOOL autoSend = YES;
static AuthType authType = 0;
static NSString *token = nil;
static NSString *appSecret = nil;

@implementation RNHockeyApp

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(configure:(NSString *) apiToken autoSend:(BOOL) autoSendCrashes authType:(NSInteger) apiAuthType appSecret:(NSString*) apiAppSecret)
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
            [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:token];
            if (autoSend == YES) {
                [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus:BITCrashManagerStatusAutoSend];
            }
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
            [[BITHockeyManager sharedHockeyManager] startManager];
            [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
        });
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
