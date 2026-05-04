package com.example.almanotesmobile.ui.composables

import androidx.compose.runtime.Composable
import com.example.almanotesmobile.R
import com.google.android.material.navigation.NavigationBarView

@Composable
fun NavigationBar() {

    NavigationBarView.OnItemSelectedListener { item ->
        when(item.itemId) {
            R.id.item_1 -> {
                // Respond to navigation item 1 click
                true
            }
            R.id.item_2 -> {
                // Respond to navigation item 2 click
                true
            }
            R.id.item_3 ->{
                true
            }
            R.id.item_4->{
                true
            }
            R.id.item_5 ->{
                true
            }
            else -> false
        }
    }

}