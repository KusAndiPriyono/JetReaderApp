package com.example.jetreaderapp.components.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetreaderapp.R

@Composable
fun ImageSignUp(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.image_signup),
        contentDescription = null,
        modifier = modifier.size(300.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ImageSignUpPreview() {
    ImageSignUp()
}