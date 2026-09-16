package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Locale

@Composable
fun BetPickerDialog(
    currentBet: Double,
    userBalance: Double,
    onSelectBet: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedBet by remember { mutableStateOf(currentBet.coerceIn(1.0, 20000.0)) }
    var customBetInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    // Standard Grid Bet Options from 1 to 20,000 as requested
    val presetBets = listOf(
        1.0, 2.0, 5.0, 10.0,
        20.0, 50.0, 100.0, 200.0,
        500.0, 1000.0, 2000.0, 5000.0,
        10000.0, 20000.0
    )

    fun applyCustomBet() {
        val parsed = customBetInput.trim().toDoubleOrNull()
        if (parsed == null || parsed < 1.0 || parsed > 20000.0) {
            inputError = "১ থেকে ২০,০০০ এর মধ্যে সংখ্যা লিখুন"
        } else {
            inputError = null
            selectedBet = parsed
            keyboardController?.hide()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("bet_picker_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140828)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFF78350F)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "বেটিং পয়েন্ট নির্বাচন",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "সীমা: ১ - ২০,০০০ পয়েন্ট",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Current Selected Bet Display & Stepper
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x55000000))
                        .border(1.dp, Color(0xFFD97706), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decrease
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                                .clickable {
                                    val next = (selectedBet - 1.0).coerceAtLeast(1.0)
                                    selectedBet = next
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color.White
                            )
                        }

                        // Amount Center
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "নির্বাচিত বাজি",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                            val formatted = if (selectedBet % 1.0 == 0.0) {
                                String.format(Locale.US, "%,d", selectedBet.toLong())
                            } else {
                                String.format(Locale.US, "%,.1f", selectedBet)
                            }
                            Text(
                                text = "$formatted 🪙",
                                color = Color(0xFFFFD700),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Increase
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                                .clickable {
                                    val next = (selectedBet + 1.0).coerceAtMost(20000.0)
                                    selectedBet = next
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Bet Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customBetInput,
                        onValueChange = {
                            customBetInput = it.filter { ch -> ch.isDigit() }
                            inputError = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_bet_input"),
                        placeholder = {
                            Text("কাস্টম বেট (১ - ২০,০০০)", fontSize = 12.sp, color = Color(0xFF64748B))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { applyCustomBet() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0xFF475569),
                            cursorColor = Color(0xFFF59E0B)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = { applyCustomBet() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("set_custom_bet_button")
                    ) {
                        Text("সেট", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (inputError != null) {
                    Text(
                        text = inputError ?: "",
                        color = Color(0xFFF87171),
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grid of Betting Options (ছক)
                Text(
                    text = "দ্রুত বাজি নির্বাচন ছক (১ - ২০,০০০)",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(presetBets) { bet ->
                        val isSelected = (selectedBet == bet)
                        val formattedLabel = if (bet >= 1000) {
                            "${(bet / 1000).toInt()}k"
                        } else {
                            bet.toInt().toString()
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFB45309))
                                    ) else Brush.horizontalGradient(
                                        listOf(Color(0x33FFFFFF), Color(0x22FFFFFF))
                                    )
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.8.dp,
                                    color = if (isSelected) Color(0xFFFDE68A) else Color(0x3394A3B8),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedBet = bet
                                    customBetInput = ""
                                    inputError = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formattedLabel,
                                color = if (isSelected) Color(0xFF140828) else Color.White,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Shortcuts: Min (1), 2x, Max (20,000)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x333B82F6))
                            .border(0.8.dp, Color(0xFF60A5FA), RoundedCornerShape(8.dp))
                            .clickable { selectedBet = 1.0 }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("মিনিমাম ১", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x338B5CF6))
                            .border(0.8.dp, Color(0xFFA78BFA), RoundedCornerShape(8.dp))
                            .clickable { selectedBet = (selectedBet * 2.0).coerceAtMost(20000.0) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("২ গুণ (2x)", color = Color(0xFFC4B5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33EF4444))
                            .border(0.8.dp, Color(0xFFF87171), RoundedCornerShape(8.dp))
                            .clickable { selectedBet = 20000.0 }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ম্যাক্স ২০,০০০", color = Color(0xFFFCA5A5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm Bet Button
                Button(
                    onClick = {
                        onSelectBet(selectedBet)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("confirm_bet_selection_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "বাজি নিশ্চিত করুন (${String.format(Locale.US, "%,.0f", selectedBet)} 🪙)",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
