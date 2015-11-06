Pod::Spec.new do |s|
  s.name             = "LockReactNative"
  s.version          = "0.0.1"
  s.summary          = "A wrapper of Lock for iOS to use with React Native."
  s.description      = <<-DESC
                        [![Auth0](https://i.cloudup.com/1vaSVATKTL.png)](http://auth0.com)
                        Auth0 is a SaaS that helps you with Authentication and Authorization. 
                        You can use Social Providers (Like Facebook, Google, Twitter, etc.), Enterprise Providers (Active Directory, LDap, Windows Azure AD, SAML, etc.) and a Username/Password store which can be saved either by us or by you. 
                        We have SDKs for the most common platforms (Ruby, Node, iOS, Angular, etc.) so that with a couple lines of code, you can get the Authentication for your app implemented. 
                        Let us worry about Authentication so that you can focus on the core of your business.
                       DESC
  s.homepage         = "https://github.com/auth0/Lock.ReactNative"
  s.license          = 'MIT'
  s.author           = { "Auth0" => "support@auth0.com", "Hernan Zalazar" => "hernan@auth0.com" }
  s.source           = { :git => "https://github.com/auth0/Lock.ReactNative.git", :tag => s.version.to_s }
  s.social_media_url = 'https://twitter.com/auth0'

  s.platform     = :ios, '7.0'
  s.requires_arc = true
  s.default_subspecs = 'Core', 'NativeModule'

  s.subspec 'Core' do |core|
    core.source_files = 'objc/core/*.{h,m}'
    core.public_header_files = "objc/core/A0LockReact.h"
    core.dependency 'Lock/UI', '~> 1.21'
    core.dependency 'Lock/TouchID'
    core.dependency 'Lock/SMS'
    core.dependency 'Lock/Email'
  end

  s.subspec 'NativeModule' do |native|
    native.source_files = 'objc/*.{h,m}'
    native.dependency 'React/Core'
    native.dependency 'LockReactNative/Core'
  end
end
