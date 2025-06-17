package com.skynetbee.neuralengine

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.backgroundCard(): Modifier {
    return this.then(
        Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .border(1.dp, color = Color(0x30B7B4B4), RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    color = Color(0x33000000),
                    size = size.copy(
                        width = size.width * 1.02f,
                        height = size.height * 1.02f
                    ),
                    topLeft = Offset(4f, 4f),
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
            }
    )
}