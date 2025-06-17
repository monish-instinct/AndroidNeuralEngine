package skynetbee.developers.DevEnvironment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.DesignElements.map
import java.text.NumberFormat
import java.util.Locale
import java.util.regex.Pattern
import androidx.compose.material3.TextFieldDefaults



var selectItem = mutableStateMapOf<String, String>()
var fill = mutableStateOf(String())
fun numberToWords(number: Long): String {
    if (number == 0L) return "Zero Rupees"


    val parts = listOf(
        1_00_00_00_00_00_000L to "Quintillion",
        1_00_00_00_00_000L to "Quadrillion",
        1_00_00_00_000L to "Trillion",
        1_00_00_00_0L to "Billion",
        1_00_00_000L to "Million",
        1_00_00_0L to "Crore",
        1_00_000L to "Lakh",
        1_000L to "Thousand",
        100L to "Hundred"
    )

    val units = listOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
    val tens = listOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

    fun convertNumToWords(num: Int): String {
        return when {
            num < 20 -> units[num]
            num < 100 -> tens[num / 10] + (if (num % 10 > 0) " "
                    + units[num % 10] else "")
            else -> units[num / 100] + " Hundred" + (if (num % 100 > 0) " and "
                    + convertNumToWords(num % 100) else "")
        }
    }

    var num = number
    val words = mutableListOf<String>()

    for ((value, name) in parts) {
        if (num >= value) {
            val chunk = (num / value).toInt()
            num %= value
            words.add("${convertNumToWords(chunk)} $name")
        }
    }

    if (num > 0) {
        words.add(convertNumToWords(num.toInt()))
    }

    return words.joinToString(" ")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    selectItem: String,
    data: List<Map<String, String>>,
    onValueSelected: (id: String, value: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // 🟩 Label = First item
    val labelEntry = data.firstOrNull()?.entries?.firstOrNull()
    val dropdownItems = data.drop(1)

    var selectedText by remember { mutableStateOf("") }
    var selectedValue by remember { mutableStateOf("") }

    // 🟦 Track textfield width

    val textFieldWidth = remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor()
                .onGloballyPositioned {
                    textFieldWidth.value = with(density) { it.size.width.toDp() }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = !expanded
                }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                label = { Text(labelEntry?.key ?: "Select",fontWeight = FontWeight.Bold, color = Color(0xFFBFBFBF))},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow"
                    )
                }
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(textFieldWidth.value) // ✅ Same width as text field
        ) {
            dropdownItems.forEach { map ->
                val entry = map.entries.firstOrNull()
                if (entry != null) {
                    DropdownMenuItem(
                        text = { Text(entry.key,fontWeight = FontWeight.Bold,color = Color.Black) },
                        onClick = {
                            selectedText = entry.key
                            selectedValue = entry.value
                            expanded = false
                            onValueSelected(selectItem, selectedValue)
                        }
                    )
                }
            }
        }
    }


    cl("[$selectItem] -> Selected: $selectedText ($selectedValue)", "megava")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    items: MutableList<Map<String, String>>,
    modifier: Modifier = Modifier,
    ndval: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val textFieldWidth = remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    ndval,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBFBFBF)
                )
            },
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold, color = Color(0xFFBFBFBF)),
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
                .onGloballyPositioned {
                    textFieldWidth.value = it.size.width.toFloat()
                },
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon"
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(density) { textFieldWidth.value.toDp() })
        ) {
            if (items.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text("No data", fontWeight = FontWeight.Bold, color = Color(0xFFBFBFBF))
                    },
                    onClick = { expanded = false }
                )
            } else {
                for (kvp in items) {
                    for ((display, value) in kvp) {
                        DropdownMenuItem(
                            text = {
                                Text(display, fontWeight = FontWeight.Bold)
                            },
                            onClick = {
                                selectedText = display
                                expanded = false
                                onItemSelected(value)
                            }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun fillFromDatabase(
    fill: String,
    query: String,
    ndval: String,
    errorcode: String = "NoData",
    modifier: Modifier = Modifier,
    onchange: (String) -> Unit
) {
    val names = remember { mutableStateListOf<Map<String, String>>() }
    var dataLoaded by remember { mutableStateOf(false) }

    // Mutable state to hold the selected value
    var selectedValue by remember { mutableStateOf("nd") }

    LaunchedEffect(Unit) {
        val developers: MutableList<Map<String, String>?> = mutableListOf()

        if(sql.reset(lineNumber = 291))
            Log.d("nmb", "fillFromDatabase: hi")
        while (true) {
            val result = sql.select(query) ?: break
            Log.d("Megaaanand", "Query: $result")
            developers.add(result)
        }

        Log.d("Megaaanand", "Query: $query \n Developers List: $developers")

        if (developers.isEmpty()) {
            names.add(mapOf(errorcode to "nd"))
        } else {
            developers.forEach { developer ->
                val values = developer?.values?.toList()
                val name = values?.getOrNull(0)
                val value = values?.getOrNull(1)

                if (name != null && value != null) {
                    names.add(mapOf(name to value))
                } else {
                    names.add(mapOf(errorcode to "nd"))
                }
            }
        }

        dataLoaded = true
        Log.d("Megaaanand", "Final Data for $query: $names")
    }

    if (dataLoaded) {
        CustomDropdown(
            items = names,
            modifier = modifier,
            ndval = ndval
        ) { selectedItem ->
            selectedValue = selectedItem // track the value
            fill
            onchange(selectedItem)
        }
    }
}



//@Composable
//fun fillFromDatabase(placeholer:String,display: String, value: String, tableName: String, whereCondition: String? = null, others: String? = null,modifier: Modifier) {
//    val names: MutableList<Map<String, String>> = mutableListOf()
//    sql.reset()
//    val developers = sql.read("${display},${value}", tableName, whereCondition, others)
//    if (developers != null) {
//        for (developer in developers) {
//            val name = developer[display]
//            val v = developer[value]
//            if (name != null && v != null) {
//                names.add((mapOf(name to v)))
//            } else {
//                names.add(mapOf("Error Code: 7826734, Please Report to Developers" to "error : 0000"))
//            }
//        }
//    } else {
//        names.add(mapOf("Gayathri Mam has not registered any developers yet" to "error:11111"))
//    }
//    CustomDropdown(
//        items = names,
//        modifier = modifier,
//        label = placeholer,
//    ) { selectitem ->
//        fill = selectitem
//    }
//}


fun formatTextWithNumber(input: String): String {
    val regex = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)") // Finds numbers (including decimals)
    val matcher = regex.matcher(input)

    val result = StringBuffer()
    while (matcher.find()) {
        val numberPart = matcher.group(1) // Extract the number part
        val formattedNumber = formatNumberIndianWithDecimal(numberPart)
        matcher.appendReplacement(result, formattedNumber) // Replace number with formatted version
    }
    matcher.appendTail(result)

    return result.toString()
}

fun formatNumberIndianWithDecimal(input: String): String {
    return try {
        if (input.contains(".")) {
            val parts = input.split(".")
            val wholePart = parts[0] // Before decimal
            val decimalPart = parts.getOrNull(1) ?: "" // After decimal (if exists)

            val formattedWhole = formatNumberIndian(wholePart)
            "$formattedWhole.$decimalPart" // Combine formatted whole + decimal part
        } else {
            formatNumberIndian(input)
        }
    } catch (e: Exception) {
        input
    }
}

fun formatNumberIndian(input: String): String {
    return try {
        val number = input.toLong()
        if (number >= 1000) {
            val formatter = NumberFormat.getInstance(Locale("en", "IN"))
            formatter.format(number)
        } else {
            input
        }
    } catch (e: NumberFormatException) {
        input
    }
}

@Composable
fun DisplayList(headline: String, items: List<String>) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Display the bold headline
        Text(
            text = headline,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        // Display each item with an arrow
        items.forEach { item ->
            Text(text = "     ➜ $item", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

fun ua(input:String):String{
    val a = input.toUpperCase(Locale.ROOT)
    return a
}

fun uc(name: String): String {
    return name.split(" ") // Split by spaces
        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercaseChar() } }
}