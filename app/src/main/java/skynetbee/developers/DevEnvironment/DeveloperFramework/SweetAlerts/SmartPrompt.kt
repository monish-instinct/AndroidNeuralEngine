package skynetbee.developers.DevEnvironment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex


object SmartPromptManager {
    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)
    var userInput by mutableStateOf("") // ✅ Stores user-typed input

    var ConfirmPrompt: (String) -> Unit = {}
    var CancelPrompt: (String) -> Unit = {}

    fun SmartPrompt(msg: String, onconfirmPrompt: (String) -> Unit, oncancelPrompt: (String) -> Unit) {
        message = msg
        isVisible = true
        userInput = "" // ✅ Reset input when showing prompt

        ConfirmPrompt = onconfirmPrompt
        CancelPrompt = oncancelPrompt

    }
    fun hide() {
        isVisible = false
    }
}

@Composable
fun smartPrompt() {
    if (SmartPromptManager.isVisible) {
        var showInputError by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1000f)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFD18203),
                                Color(0xFFFACB69),
                                Color(0xFFD18203)
                            )
                        )
                    )
                    .padding(16.dp)
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = SmartPromptManager.message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // TextField bound to SmartPromptManager's userInput
                    OutlinedTextField(
                        value = SmartPromptManager.userInput,
                        onValueChange = {
                            SmartPromptManager.userInput = it
                            showInputError = false
                        },
                        placeholder = { Text("Type here...", color = Color.DarkGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showInputError) Color.Red else Color.Gray,
                            unfocusedBorderColor = if (showInputError) Color.Red else Color.Gray
                        ),
                        isError = showInputError
                    )

                    if (showInputError) {
                        Text(
                            text = "Input cannot be empty!",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (SmartPromptManager.userInput.isNotBlank()) {
                                    SmartPromptManager.ConfirmPrompt(SmartPromptManager.userInput)
                                    SmartPromptManager.hide()
                                } else {
                                    showInputError = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "OK", color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                SmartPromptManager.CancelPrompt(SmartPromptManager.userInput)
                                SmartPromptManager.hide()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Cancel", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

fun Cl (msg:String) {
    Log.d("print", msg)
}