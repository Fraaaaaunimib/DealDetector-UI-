package com.example.navi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Composable
fun DealCard(
    deal: Deal,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                val intent = Intent(context, DealDetailActivity::class.java).apply {
                    putExtra("deal", deal)
                }
                context.startActivity(intent)
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = deal.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = deal.subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = deal.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@SuppressLint("ParcelCreator")
@Parcelize
data class Deal(
    val title: String,
    val subtitle: String,
    val detail: String,
    val supermarket: String,
    val category: String,
    var priceKilo: Double,
    var priceDeal: Double,
    var priceNormal: Double,
    var dealPerc: Double,
    var dealType: String
) : Parcelable