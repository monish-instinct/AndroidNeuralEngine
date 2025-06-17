package skynetbee.developers.DevEnvironment

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.zebra.rfid.api3.*
import kotlinx.coroutines.*

class RfidViewModel(application: Application) : AndroidViewModel(application) {

    private var readers: Readers? = null
    private var rfidReader: RFIDReader? = null
    val tagList = mutableStateListOf<String>()
    val logMessages = mutableStateListOf<String>()
    val serialNum = mutableStateOf("")
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private fun log(message: String) {
        logMessages.add(message)
        Log.d("RFID", message)
    }

    private fun setupReaderEvents() {
        rfidReader?.let { reader ->
            try {
                // Ensure reader is initialized and connected before setting up events
                if (reader.isConnected) {
                    // Enable the handheld trigger event and tag read event
                    reader.Events.setHandheldEvent(true)
                    reader.Events.setTagReadEvent(true)
                    reader.Events.setAttachTagDataWithReadEvent(true)

                    // Add event listener for RFID read events and status events
                    reader.Events.addEventsListener(object : RfidEventsListener {
                        override fun eventReadNotify(readEvents: RfidReadEvents?) {
                            Log.d("Divakar", "eventStatusNotify: HI1")

                            val tags = readEvents?.readEventData?.tagData
                            tags?.let { tag ->
                                if (!tagList.contains(tag.tagID)) {
                                    tagList.add(tag.tagID)
                                    Log.d("RFID", "📖 Tag Detected: ${tag.tagID}")

                                }
                            }
                        }

                        override fun eventStatusNotify(statusEvents: RfidStatusEvents?) {
                            val statusData = statusEvents?.StatusEventData
                            Log.d("Divakar", "eventStatusNotify: StatusEventType: ${statusData?.statusEventType}")

                            when (statusData?.statusEventType) {
                                STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT -> {
                                    val triggerEventData = statusData.HandheldTriggerEventData

                                    Log.d("Divakar", "HandheldTriggerEventData: ${triggerEventData.handheldEvent}")

                                    when (triggerEventData.handheldEvent) {
                                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                                            Log.d("Divakar", "Trigger Pressed")
                                            Log.d("RFID", "🔫 Trigger Pressed")
                                            // Start inventory reading when the trigger is pressed
                                            startInventory()
                                        }
                                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                                            Log.d("Divakar", "Trigger Released")
                                            Log.d("RFID", "🔫 Trigger Released")
                                            // Stop inventory reading when the trigger is released
                                            stopInventory()
                                        }
                                        else -> {
                                            Log.d("Divakar", "Unrecognized Trigger Event: $triggerEventData")
                                            Log.d("RFID", "❌ Unrecognized Trigger Event: $triggerEventData")
                                        }
                                    }
                                }
                                else -> {
                                    Log.d("Divakar", "Unrecognized Status Event Type: ${statusData?.statusEventType}")
                                    Log.d("RFID", "ℹ️ Unhandled Status Event Type: ${statusData?.statusEventType}")
                                }
                            }
                        }
                    })
                    Log.d("RFID", "✅ Event listeners configured for ${reader.hostName}")
                } else {
                    Log.e("RFID", "❌ Reader is not connected!")
                }
            } catch (e: Exception) {
                Log.e("RFID", "❌ Failed to configure events: ${e.message}", e)
            }
        } ?: Log.e("RFID", "❌ Cannot setup events: rfidReader is null")
    }

    fun connectToReader(hostName: String) {
        serialNum.value = hostName.trim()
        log("🔍 Connecting to: ${serialNum.value}")

        if (BluetoothAdapter.getDefaultAdapter()?.isEnabled != true) {
            log("❌ Bluetooth not available or disabled.")
            return
        }

        if (readers == null) {
            readers = Readers(getApplication(), ENUM_TRANSPORT.BLUETOOTH)
            log("ℹ️ Readers initialized.")
        }

        viewModelScope.launch {
            val devices = discoverDevices() ?: run {
                log("❌ No RFID readers found.")
                return@launch
            }

            val selected = devices.find {
                it.rfidReader?.hostName?.endsWith(serialNum.value) == true
            }

            if (selected == null) {
                log("❌ No reader ends with ${serialNum.value}")
                return@launch
            }

            rfidReader = selected.rfidReader

           // Let reader settle
            if (rfidReader?.isConnected == false) {
                try {
                    rfidReader?.connect()
                    log("✅ Connected to ${rfidReader?.hostName}")
                } catch (e: Exception) {
                    Log.e("RFID", "❌ Connect failed: ${e.message}", e)
                    rfidReader = null
                    return@launch
                }
            } else {
                log("ℹ️ Reader already connected.")
            }

            setupReaderEvents()
        }
    }

    private fun discoverDevices(): List<ReaderDevice>? {

        val devices = readers?.GetAvailableRFIDReaderList()
        if (!devices.isNullOrEmpty()) return devices
        return null

    }

    fun startInventory() {
        rfidReader?.let {
            try {
                it.Actions.Inventory.perform()
                log("▶️ Inventory started on ${it.hostName}")
            } catch (e: Exception) {
                Log.e("RFID", "❌ Start inventory failed: ${e.message}", e)
            }
        } ?: log("❌ Cannot start: Reader is null")
    }

    fun stopInventory() {
        rfidReader?.let {
            try {
                it.Actions.Inventory.stop()
                log("⏹️ Inventory stopped")
            } catch (e: Exception) {
                Log.e("RFID", "❌ Stop inventory failed: ${e.message}", e)
            }
        } ?: log("❌ Cannot stop: Reader is null")
    }

    override fun onCleared() {
        super.onCleared()
        try {
            rfidReader?.disconnect()
            readers?.Dispose()
            log("🧹 ViewModel cleared and resources disposed.")
        } catch (e: Exception) {
            Log.e("RFID", "❌ Error during cleanup: ${e.message}", e)
        } finally {
            viewModelScope.cancel()
            rfidReader = null
            readers = null
        }
    }
}



