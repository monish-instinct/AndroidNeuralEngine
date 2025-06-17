# Neural Engine Examples

This directory contains example implementations demonstrating how to use the Android Neural Engine library.

## Sample App

The `sample-app` directory contains a complete Android application that demonstrates:

- **Library Integration**: How to add the Neural Engine dependency
- **Initialization**: Proper setup of the Neural Engine
- **UI Components**: Usage of tablet framework components
- **Payment Integration**: Accessing payment functionality
- **View Model**: Working with the Neural Engine view model

### Running the Sample App

1. **Set up GitHub Packages authentication**:
   Create a `gradle.properties` file in your home directory or project root:
   ```properties
   gpr.user=your-github-username
   gpr.key=your-github-personal-access-token
   ```

2. **Open the sample app in Android Studio**

3. **Sync and build the project**

4. **Run on device or emulator**

### Key Features Demonstrated

#### 1. Library Initialization
```kotlin
// In your Application class or MainActivity
NeuralEngine.initialize(this)
```

#### 2. Accessing Neural Engine Instance
```kotlin
val neuralEngine = NeuralEngine.getInstance()
```

#### 3. Using Tablet Framework Components
```kotlin
@Composable
fun MyScreen() {
    // Engine starter component
    NeuralEngine.TabletFramework.EngineStarter()
    
    // Login with OTP
    NeuralEngine.TabletFramework.LoginWithOTP()
    
    // Profile management
    NeuralEngine.TabletFramework.MyProfile()
}
```

#### 4. Payment Processing
```kotlin
val paymentEngine = neuralEngine.getPaymentEngine()
// Use payment functionality
```

#### 5. View Model Access
```kotlin
val viewModel = neuralEngine.getViewModel()
// Access view model functionality
```

## Project Structure

```
examples/
├── sample-app/
│   ├── build.gradle.kts
│   └── MainActivity.kt
└── README.md
```

## Additional Examples

More examples will be added here covering:

- **RFID Integration**: Complete RFID workflow examples
- **Database Operations**: SQLCipher usage patterns
- **Media Processing**: Video and audio handling
- **Document Generation**: PDF and Excel creation
- **Custom Theming**: Extending the UI framework
- **Advanced Configuration**: ProGuard and optimization setups

## Support

If you need help with any of these examples or have questions about implementation:

- Check the main [README](../README.md)
- Open an issue on [GitHub](https://github.com/monish-instinct/AndroidNeuralEngine/issues)
- Contact: monish@skynetbee.com

