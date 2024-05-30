package com.example.storitechnicaltest.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation

@Composable
fun CircularImage(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    uri: Uri? = null,
    contentDescription: String?,
    size: Dp = 100.dp,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = imageUrl ?: uri,
            builder = {
                transformations(CircleCropTransformation())
            }
        )
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier.size(size)
        )
    }
}
