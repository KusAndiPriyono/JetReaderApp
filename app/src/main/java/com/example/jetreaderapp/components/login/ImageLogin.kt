package com.example.jetreaderapp.components.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetreaderapp.R

@Composable
fun ImageLogin(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.image_login),
        contentDescription = null,
        modifier = modifier.size(300.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ImageLoginPreview() {
    ImageLogin()
}