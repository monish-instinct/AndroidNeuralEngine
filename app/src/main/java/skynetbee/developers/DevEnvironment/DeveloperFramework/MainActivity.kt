package skynetbee.developers.DevEnvironment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalConfiguration
import androidx.constraintlayout.compose.ConstraintLayout
import com.razorpay.PaymentResultListener
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity

var myProf = ""
val mediaPlayer = MediaPlayer()

private lateinit var paymentHandler: PaymentHandler
class MainActivity : ComponentActivity(), PaymentResultListener {
    @SuppressLint("UnrememberedMutableState")
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the PaymentHandler with the transactionId
        val transactionId = "TXN12345"
        paymentHandler = PaymentHandler(activity = this, transactionId)


        if (!isTablet()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContent {
            DevEnvironmentTheme {

                if (isTablet()) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(

                                    colors = if (!isSystemInDarkTheme()) {
                                        listOf(Color(0xFF90CAF9), Color.White)
                                    } else {
                                        listOf(Color(0xFF040C38), Color.Black)
                                    }
                                )
                            )
                    ) {
                        myProf = "my_profile_Tablet"
                        Navigator()
                    }
                } else {
                    Box(modifier = Modifier
                        .fillMaxSize()
                    ) {
                        ConstraintLayout(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CallBackground()
                            myProf = "my_profile_Mobile"
                            Navigator()
                        }
                    }
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        paymentHandler.onPaymentSuccess(razorpayPaymentId)
    }

    override fun onPaymentError(code: Int, response: String?) {
        paymentHandler.onPaymentError(code, response)
    }

    private fun isTablet(): Boolean {
        val screenLayout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        return screenLayout >= Configuration.SCREENLAYOUT_SIZE_LARGE

    }
}

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.smallestScreenWidthDp >= 600
}

fun OTPNeverVerified() : Boolean{

    val data = DF.select("otp FROM last_communication_with_server ORDER BY localcounti DESC;")
    Log.d("check1234", "VerifyTheUser_Mobile: ${data}")
    if (data == null){
        return  true
    } else {
        return if (validateOTP(data["otp"].toString()).first){
            false
        } else {
            true
        }
    }
    return false
}

fun OTPNotVerifiedToday(): Boolean{
    val data = DF.select("otp FROM last_communication_with_server WHERE currentdoe = ${getCurrentDate()} ORDER BY localcounti DESC;")
    Log.d("otpneververified", "$data")
    if (data != null){
        if (validateOTP(data["otp"].toString()).first){
            return true
        } else {
            return false
        }
    }
    return false
}

fun reVerifyOTP(): String{
    val data = DF.select("otp FROM last_communication_with_server WHERE currentdoe = ${getCurrentDate()} ORDER BY localcounti DESC;")
    var access = ""
    if (data != null){
        var otp = validateOTP(data["otp"].toString()).second
        sendOtpToServer(otp, onOtpValidation = {}, onSecondCall = {}, onResponseReceived = { acc ->
            access = acc
        })
    }
    return access
}

fun LasteVisitedPage(): String?{

    val lwp = DF.select("pagename FROM last_page_worked_on ORDER BY localcounti DESC")

    cl(lwp?.get("pagename"),"Nithish123")
    if (lwp!=null) {
        return lwp["pagename"]
    }

    return null
}

fun LastWorkedOn(page: String) {

    val insert = """INSERT INTO last_page_worked_on (
    pagename, 
    area, 
    currentdoe, 
    currenttoe, 
    counti, 
    mcounti, 
    fromdat, 
    ftodat, 
    ftotim, 
    ftovername, 
    ftover, 
    ftopid, 
    todat, 
    totim, 
    tovername, 
    tover, 
    topid, 
    ipmac, 
    deviceanduserainfo, 
    basesite, 
    owncomcode, 
    testeridentity, 
    testcontrol, 
    adderpid, 
    addername, 
    adder, 
    doe, 
    toe
) VALUES (
    '${page}', 
    'skytest', 
    '${getCurrentDate()}', 
    '${getCurrentTime()}', 
    NULL, 
    NULL, 
    '', 
    '0000-00-00', 
    '00:00:00', 
    '', 
    '', 
    '', 
    '0000-00-00', 
    '00:00:00', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '', 
    '',
    '${getCurrentDate()}', 
    '${getCurrentTime()}'
);
"""

    DF.executeQuery(insert)
}

@Composable
fun CallBackground(){
    Column (modifier = Modifier
        .fillMaxSize()
        .blur(150.dp)){
        Background()
    }
}

@Composable
fun Background() {
    val baseGradientColors = listOf(
        Color(80f / 255, 120f / 255, 255f / 255),
        Color(255f / 255, 0f, 255f / 255)
    )

    val ispad = LocalConfiguration.current.screenWidthDp >= 600
    val isphone = !ispad

    val time by produceState(initialValue = 0.0) {
        while (true) {
            value = System.currentTimeMillis() / 500.0
            delay(2)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(28f / 255, 16f / 255, 62f / 255))
    ) {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val screenWidth: Float
        val screenHeight: Float

        with(density) {
            screenWidth = configuration.screenWidthDp.dp.toPx()
            screenHeight = configuration.screenHeightDp.dp.toPx()
        }

        for (index in baseGradientColors.indices) {
            Circle(
                color = baseGradientColors[index],
                modifier = Modifier
                    .size(if (isphone) 400.dp else 900.dp)
                    .graphicsLayer(
                        translationX = (sin((time + index * 20) / 10) * screenWidth * 0.5f).toFloat(),
                        translationY = (cos((time + index * 20) / 10) * screenHeight * 0.4f).toFloat(),
                        alpha = 0.6f
                    )
            )
        }

        // Black gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun Circle(color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color)
    )
}