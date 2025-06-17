package skynetbee.developers.DevEnvironment


/**
 * Created by Megavarshini KS & Gowtham Bharath N
 * Kotlin Wing - PieChart Component
 *
 * 📄 Description:
 * This Kotlin file defines a composable PieChart that renders a pie chart visualization
 * with associated labels.
 *
 * ✅ Functionality:
 * - Draws a pie chart using Android Canvas within a Compose AndroidView.
 * - Displays the chart at the top with the legend (color and name) shown below.
 * - Accepts dynamic data for segment labels and their percentages.
 * - Supports custom sizing of the chart via the size parameter.
 * - Limits the visible legend items to 4 with scrolling support for additional items.
 * - Adapts colors for light and dark themes.
 *
 * 🛠️ Notes:
 * - Uses bitmap and Android Canvas API to draw pie slices.
 * - The legend is implemented with a LazyColumn to allow scrolling.
 * - Title is displayed prominently above the pie chart.
 * - Size parameter controls the chart’s diameter (in dp).
 */


import android.graphics.Bitmap
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun PieChart(
    title: String,
    shareNames: List<String>,
    percentage: List<Float>,
    size: Dp = 200.dp
) {

    val sizePx = size.value.toInt() * 3
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val predefinedColors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Color(0xFFFFA500),
        Color(0xFFADD8E6),
        Color(0xFF00008B),
        Color(0xFF8A2BE2)
    )

    val colors = predefinedColors.take(shareNames.size)
    val total = percentage.sum()
    var startAngle = -90f

    for (i in percentage.indices) {
        val sweepAngle = (percentage[i] / total) * 360f
        val paint = android.graphics.Paint().apply {
            color = colors[i].toArgb()
            style = android.graphics.Paint.Style.FILL
        }
        canvas.drawArc(
            0f, 0f,
            sizePx.toFloat(), sizePx.toFloat(),
            startAngle, sweepAngle,
            true, paint
        )
        startAngle += sweepAngle
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Transparent, shape = RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    setImageBitmap(bitmap)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            },
            modifier = Modifier
                .size(size)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 180.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(colors) { index, color ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color = color, shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = shareNames[index],
                        fontSize = 14.sp,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            }
        }
    }
}
