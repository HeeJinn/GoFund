package com.example.gofund

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.SeaweedScriptFamily

@Composable
fun LogoText(){
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(270.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Go\nFund\nApp",
            fontFamily = IntroFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 80.sp,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 60.sp
        )
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(160.dp),
            contentAlignment = Alignment.TopEnd
        ){
            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                text = "Today",
                fontFamily = SeaweedScriptFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 75.sp,
                color = MaterialTheme.colorScheme.tertiary,
                lineHeight = 50.sp,


                )
        }

    }

}


@Composable
fun LoginScreen(){

}

@Preview(showBackground = true)
@Composable
fun PreviewCompose(){
    Surface(
        modifier = Modifier
            .width(437.dp)
            .height(971.dp),
        color = Color(LightModeLightBlue.value)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            LogoText()
        }
    }
}