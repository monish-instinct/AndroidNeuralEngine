# Setup Instructions for Android Neural Engine

## Step 1: GitHub Repository Setup

1. **Create the GitHub repository** (if not already done):
   - Go to https://github.com/monish-instinct/AndroidNeuralEngine
   - Make sure it's a public repository
   - Don't initialize with README (we already have one)

2. **Configure GitHub Personal Access Token**:
   - Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Generate new token with these scopes:
     - `read:packages`
     - `write:packages`
     - `repo`
   - Copy the token (you'll need it later)

## Step 2: Push Code to GitHub

Run these commands in the terminal from the project directory:

```bash
# Navigate to the project directory
cd /Users/instinct/Downloads/DevEnvironment/DevEnvironment

# Push to GitHub
git push -u origin main
```

## Step 3: Enable GitHub Actions

1. Go to your repository on GitHub
2. Click on the "Actions" tab
3. GitHub will detect the workflow file and ask to enable it
4. Click "I understand my workflows, go ahead and enable them"

## Step 4: Test the Publishing Workflow

### Option A: Manual Trigger
1. Go to Actions → "Publish AAR to GitHub Packages"
2. Click "Run workflow"
3. Enter version (e.g., "1.0.0")
4. Click "Run workflow"

### Option B: Create a Release Tag
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

## Step 5: Verify Package Publication

1. Go to your repository on GitHub
2. Click on the "Packages" tab (or check the right sidebar)
3. You should see "neural-engine" package listed

## Step 6: Using the Library in a New Project

### 1. Set up authentication
In your new project's `gradle.properties` (or global gradle.properties):

```properties
gpr.user=monish-instinct
gpr.key=YOUR_GITHUB_TOKEN
```

### 2. Add repository to your project's `build.gradle.kts` (project level):

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/monish-instinct/AndroidNeuralEngine")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}
```

### 3. Add dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.skynetbee:neural-engine:1.0.0")
}
```

### 4. Use the library:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Neural Engine
        NeuralEngine.initialize(this)
        
        setContent {
            MaterialTheme {
                // Use Neural Engine components
                NeuralEngine.TabletFramework.EngineStarter()
            }
        }
    }
}
```

## Troubleshooting

### Common Issues:

1. **Authentication Error**: Make sure your GitHub token has `read:packages` and `write:packages` permissions

2. **Build Fails**: Check that all dependencies are properly resolved in the GitHub Actions environment

3. **Package Not Found**: Ensure the package was published successfully and you're using the correct coordinates

### Verification Commands:

```bash
# Check if the library builds locally
./gradlew :neural-engine:assembleRelease

# Check publishing configuration
./gradlew :neural-engine:publishToMavenLocal
```

## What You Get

✅ **Automated AAR Publishing** - Every push to main or tag creates a new package
✅ **GitHub Packages Integration** - No need to manage your own Maven repository
✅ **All Dependencies Included** - RFID SDKs, payment processing, Compose UI, etc.
✅ **Versioned Releases** - Semantic versioning with Git tags
✅ **Easy Integration** - Simple `implementation()` line in new projects
✅ **Complete Documentation** - README, examples, and usage guides

## Next Steps

1. Push the code to GitHub
2. Test the publishing workflow
3. Create your first new project using the library
4. Star the repository and share with your team!

---

**Need Help?**
- GitHub Issues: https://github.com/monish-instinct/AndroidNeuralEngine/issues
- Email: monish@skynetbee.com

