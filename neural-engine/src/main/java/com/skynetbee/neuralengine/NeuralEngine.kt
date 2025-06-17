package com.skynetbee.neuralengine

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.skynetbee.neuralengine.DeveloperFramework.Payment
import com.skynetbee.neuralengine.DeveloperFramework.ViewModel as NeuralViewModel
import com.skynetbee.neuralengine.DeveloperFramework.TabletFramework.EngineStarter_Tablet
import com.skynetbee.neuralengine.DeveloperFramework.TabletFramework.LoginWithOTP_Tablet
import com.skynetbee.neuralengine.DeveloperFramework.TabletFramework.MyProfile_Tablet

/**
 * Main entry point for the Neural Engine library
 * Provides access to all framework components and utilities
 */
class NeuralEngine private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: NeuralEngine? = null
        
        /**
         * Get the singleton instance of NeuralEngine
         */
        fun getInstance(): NeuralEngine {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NeuralEngine().also { INSTANCE = it }
            }
        }
        
        /**
         * Initialize the Neural Engine with application context
         */
        fun initialize(context: Context) {
            // Initialize any required components
            getInstance()
        }
    }
    
    /**
     * Access to payment processing functionality
     */
    fun getPaymentEngine(): Payment {
        return Payment()
    }
    
    /**
     * Access to the main view model
     */
    fun getViewModel(): NeuralViewModel {
        return NeuralViewModel()
    }
    
    /**
     * Tablet Framework Components
     */
    object TabletFramework {
        
        /**
         * Engine Starter for tablet interface
         */
        @Composable
        fun EngineStarter() {
            EngineStarter_Tablet()
        }
        
        /**
         * Login with OTP component for tablet
         */
        @Composable
        fun LoginWithOTP() {
            LoginWithOTP_Tablet()
        }
        
        /**
         * Profile management component for tablet
         */
        @Composable
        fun MyProfile() {
            MyProfile_Tablet()
        }
    }
    
    /**
     * UI Theme access
     */
    object Theme {
        // Access to theme components will be added here
    }
}

