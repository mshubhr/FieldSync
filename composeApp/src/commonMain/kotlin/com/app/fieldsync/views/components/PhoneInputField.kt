package com.app.fieldsync.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.fieldsync.model.Country

val countries = listOf(
    Country("United States", "+1", "🇺🇸", 10),
    Country("United Kingdom", "+44", "🇬🇧", 10),
    Country("Germany", "+49", "🇩🇪", 10),
    Country("Australia", "+61", "🇦🇺", 9),
    Country("Russia", "+7", "🇷🇺", 10),
    Country("India", "+91", "🇮🇳", 10)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInputField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            OutlinedTextField(
                value = "${selectedCountry.flag} ${selectedCountry.code}",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.width(123.dp).clickable(enabled = enabled) { expanded = true },
                label = { Text("Code") },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable(enabled = enabled) { expanded = true })
                },
                enabled = enabled
            )

            DropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text("${country.flag} ${country.name} (${country.code})") },
                        onClick = {
                            onCountrySelected(country)
                            expanded = false
                        })
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.length <= selectedCountry.phoneLength && it.all { char -> char.isDigit() }) {
                    onPhoneNumberChange(it)
                }
            },
            label = { Text("Phone Number") },
            placeholder = { Text("Enter ${selectedCountry.phoneLength} digits") },
            modifier = Modifier.weight(1f),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = enabled
        )
    }
}