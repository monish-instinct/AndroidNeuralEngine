# Android Neural Engine

🚀 A comprehensive Android development framework that provides RFID integration, payment processing, multimedia support, and modern UI components with Jetpack Compose.

## Features

### 🔧 Core Components
- **RFID SDK Integration** - Complete Zebra RFID SDK support
- **Payment Processing** - Razorpay integration for secure payments
- **Jetpack Compose UI** - Modern Android UI components
- **Media Support** - ExoPlayer integration for video/audio
- **Database Security** - SQLCipher for encrypted SQLite databases
- **Networking** - OkHttp integration
- **Document Processing** - PDF and Office document support
- **Image Loading** - Coil for efficient image loading
- **Animations** - Lottie animation support

### 📱 UI Framework
- Tablet-optimized components
- Login with OTP
- Profile management
- Engine starter interface
- Consistent theming

## Installation

### 1. Add GitHub Packages Repository

In your project's `build.gradle.kts` (project level):

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/monish-instinct/AndroidNeuralEngine")
            credentials {
                username = "YOUR_GITHUB_USERNAME"
                password = "YOUR_GITHUB_TOKEN" // Personal Access Token with read:packages permission
            }
        }
    }
}
```

### 2. Add Dependency

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.skynetbee:neural-engine:1.0.0")
}
```

### 3. Sync Project
Sync your project with Gradle files.

## Quick Start

### Initialize the Neural Engine

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NeuralEngine.initialize(this)
    }
}
```

### Basic Usage

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val neuralEngine = NeuralEngine.getInstance()
        
        setContent {
            YourAppTheme {
                // Use Neural Engine components
                NeuralEngine.TabletFramework.EngineStarter()
            }
        }
    }
}
```

### Payment Processing

```kotlin
val paymentEngine = NeuralEngine.getInstance().getPaymentEngine()
// Use payment functionality
```

### Tablet Framework Components

```kotlin
@Composable
fun MyScreen() {
    Column {
        // Engine starter
        NeuralEngine.TabletFramework.EngineStarter()
        
        // Login with OTP
        NeuralEngine.TabletFramework.LoginWithOTP()
        
        // Profile management
        NeuralEngine.TabletFramework.MyProfile()
    }
}
```

## Permissions

The library automatically includes necessary permissions. Make sure your app has:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

## Advanced Configuration

### ProGuard/R8

If you're using ProGuard or R8, the library includes consumer ProGuard rules that will be automatically applied. No additional configuration needed.

### Custom Theming

The library provides theme components that can be customized:

```kotlin
// Access theme components
NeuralEngine.Theme
```

## Included Dependencies

This library includes and manages these dependencies for you:

- **AndroidX Core, Lifecycle, Compose**
- **Material Design 3**
- **Navigation Compose**
- **ExoPlayer (Media3)**
- **Coil for image loading**
- **Lottie for animations**
- **SQLCipher for database encryption**
- **OkHttp for networking**
- **iText for PDF processing**
- **Apache POI for Office documents**
- **Razorpay for payments**
- **Zebra RFID SDK**
- **RxJava3**
- **Gson for JSON**
- **Apache Commons Lang**

## Minimum Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 35
- **Compile SDK**: 35
- **Java Version**: 11
- **Kotlin**: 2.0.21+
- **Android Gradle Plugin**: 8.9.1+

## Versioning

This project uses [Semantic Versioning](https://semver.org/).

- **MAJOR** version for incompatible API changes
- **MINOR** version for backwards-compatible functionality additions
- **PATCH** version for backwards-compatible bug fixes

## GitHub Packages Authentication

To use this library, you need a GitHub Personal Access Token with `read:packages` permission:

1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Generate a new token with `read:packages` scope
3. Use this token in your `gradle.properties` or environment variables

## Examples

Check out the [examples directory](./examples) for complete implementation examples.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For questions, issues, or contributions:

- **Issues**: [GitHub Issues](https://github.com/monish-instinct/AndroidNeuralEngine/issues)
- **Email**: monish@skynetbee.com

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a list of changes in each version.

---

**Made with ❤️ by SkynetBee Developers**

