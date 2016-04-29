require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name              = 'RNHockeyApp'
  s.version           = package['version']

  s.summary           = 'React Native module wrapper that lets you use the HockeyApp SDK in your React Native apps.'
  s.description       = <<-DESC
                        A React Native module wrapper around HockeyApp which is a service that lets you
                        distribute beta apps, collect crash reports and communicate with your app's users.

                        It improves the testing process dramatically and can be used for both beta
                        and App Store builds.
                        DESC

  s.homepage          = 'http://hockeyapp.net/'
  s.source = { :http => "https://github.com/slowpath/react-native-hockeyapp" }
  s.license           = 'MIT'

  s.platform          = :ios, '6.0'
  s.requires_arc      = true

  s.source_files = 'RNHockeyApp/*.{h,m}'

  s.default_subspec   = 'HockeySDK'
  s.subspec 'HockeySDK' do |ss|
    ss.resource_bundle = { 'HockeySDKResources' => ['RNHockeyApp/HockeySDK.embeddedframework/HockeySDK.framework/Resources/HockeySDKResources.bundle/*.png', 'RNHockeyApp/HockeySDK.embeddedframework/HockeySDK.framework/Resources/HockeySDKResources.bundle/*.lproj'] }
    ss.frameworks = 'AssetsLibrary', 'CoreGraphics', 'CoreText', 'CoreTelephony', 'Foundation', 'MobileCoreServices', 'Photos', 'QuartzCore', 'QuickLook', 'Security', 'SystemConfiguration', 'UIKit'
    ss.libraries = 'c++', 'z'
    ss.vendored_frameworks = 'RNHockeyApp/HockeySDK.embeddedframework/HockeySDK.framework'
  end

  s.dependency 'React'
end