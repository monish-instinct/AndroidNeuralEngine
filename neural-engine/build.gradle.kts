plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "com.skynetbee.neuralengine"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // --- Core AndroidX ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.appcompat)

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material.icons.extended)

    // --- Media / Exoplayer ---
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer)

    // --- Image Loading ---
    implementation(libs.coil.compose)

    // --- Lottie ---
    implementation(libs.lottie.compose)

    // --- SQLCipher & SQLite ---
    implementation(libs.android.database.sqlcipher)
    implementation(libs.androidx.sqlite)
    implementation(libs.androidx.sqlite.ktx)

    // --- Networking ---
    implementation(libs.okhttp)

    // --- PDF / Office ---
    implementation(libs.itext7.core)
    implementation(libs.poi.ooxml)

    // --- Payments ---
    implementation(libs.checkout)

    // --- Zebra RFID SDKs ---
    implementation(files("libs/API3_ASCII-release-2.0.3.162.aar"))
    implementation(files("libs/API3_CMN-release-2.0.3.162.aar"))
    implementation(files("libs/API3_INTERFACE-release-2.0.3.162.aar"))
    implementation(files("libs/API3_LLRP-release-2.0.3.162.aar"))
    implementation(files("libs/API3_NGE-protocolrelease-2.0.3.162.aar"))
    implementation(files("libs/API3_NGE-Transportrelease-2.0.3.162.aar"))
    implementation(files("libs/API3_NGEUSB-Transportrelease-2.0.3.162.aar"))
    implementation(files("libs/API3_READER-release-2.0.3.162.aar"))
    implementation(files("libs/API3_TRANSPORT-release-2.0.3.162.aar"))
    implementation(files("libs/API3_ZIOTC-release-2.0.3.162.aar"))
    implementation(files("libs/API3_ZIOTCTRANSPORT-release-2.0.3.162.aar"))
    implementation(files("libs/rfidhostlib.aar"))
    implementation(files("libs/rfidseriallib.aar"))

    // --- Utility Libraries ---
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.reactivex.rxjava3:rxjava:3.1.6")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.material3.v121)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.skynetbee"
            artifactId = "neural-engine"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
            
            pom {
                name.set("Android Neural Engine")
                description.set("A comprehensive Android development framework with RFID, payment processing, and multimedia capabilities")
                url.set("https://github.com/monish-instinct/AndroidNeuralEngine")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("monish-instinct")
                        name.set("Monish")
                        email.set("monish@skynetbee.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/monish-instinct/AndroidNeuralEngine.git")
                    developerConnection.set("scm:git:ssh://github.com/monish-instinct/AndroidNeuralEngine.git")
                    url.set("https://github.com/monish-instinct/AndroidNeuralEngine")
                }
            }
        }
    }
    
    repositories {
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

