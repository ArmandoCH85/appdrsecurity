package com.drsecuritygps.app.presentation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.drsecuritygps.app.AppBrand
import com.drsecuritygps.app.R

@Composable
actual fun BrandLogo(
    modifier: Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.ic_brand_logo),
        contentDescription = AppBrand.displayName,
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}
