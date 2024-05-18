package com.example.robot_touch_pad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.robot_touch_pad.ui.theme.RobotTouchpadTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobotTouchpadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DraggableTextLowLevel(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
private fun DraggableTextLowLevel(modifier: Modifier) {
    var lastXSpeed by remember { mutableStateOf(0f) }// not used yet
    var lastYSpeed by remember { mutableStateOf(0f) }//not used yet
    var isHeld by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var lastOffsetX by remember { mutableStateOf(0f) }
    var lastOffsetY by remember { mutableStateOf(0f) }
    var positionHeldStart by remember { mutableStateOf(0L) }
    var lastPosition by remember { mutableStateOf(Offset.Zero) }
    LaunchedEffect(isHeld,lastPosition) {
        if (isHeld) {
            positionHeldStart = System.currentTimeMillis()
            while (isHeld && lastPosition == Offset(offsetX, offsetY)){
                delay(100) // check every 100ms
                if (System.currentTimeMillis() - positionHeldStart >= 1000) {
                    // The position has been held for more than 1.5 seconds
                    break
                }
            }
            if (lastPosition != Offset(offsetX, offsetY)) {
                // The position was changed before 1 seconds elapsed
                positionHeldStart = 0L
            }
            //delay(3000)
            if (System.currentTimeMillis() - positionHeldStart >= 1000) {
                // Lock the speed after 1 seconds of holding
                lastXSpeed = offsetX * 100 / 400
                lastYSpeed = -offsetY * 100 / 400
                lastOffsetY = offsetY
                lastOffsetX = offsetX
                println("SPEED IS HELD HERE_-_-_->>>:::lastXSpeed====$lastXSpeed%,lastYSpeed======$lastYSpeed%")
                println("Last_Offset::::X====$lastOffsetX,Y======$lastOffsetY")
            }
        } else {
            positionHeldStart = 0L
        }
    }
    Column ( modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ){
        Box(
            modifier = modifier
                .size(400.dp)
                .background(color = Color.Gray)
                .fillMaxSize()
        ) {
            Box(
                Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .clip(shape = CircleShape)
                    .background(Color.Blue)
                    .size(50.dp)
                    .align(Alignment.Center)

                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragCancel = {
/// you can implement som thing here when it needed : this is called if another gesture has consumed pointer input, canceling this gesture.
                            },
                            onDragEnd = {
                                if (isHeld){
                                    if (System.currentTimeMillis() - positionHeldStart < 1000) {
                                        // Reset speed only if the hold was less than 1 seconds
                                        lastXSpeed = 0f
                                        lastYSpeed = 0f
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }
                                isHeld = false
                                println("Reset_Offset::::X====$offsetX,Y======$offsetY")

                            }
                        ) { change, dragAmount ->
                            change.consume()

                            val newPosition = Offset(
                                (offsetX + dragAmount.x).coerceIn(-400f, 400f),
                                (offsetY + dragAmount.y).coerceIn(-400f, 400f)
                            )

                            if (newPosition != lastPosition) {
                                // Update the position and reset the hold start time if the position changed
                                positionHeldStart = System.currentTimeMillis()
                            }
                            offsetX = newPosition.x
                            offsetY = newPosition.y
                            lastPosition = newPosition
                            isHeld = true
                            println("Move_Offset::::X====$offsetX,Y======$offsetY")
                            println("Amount::::X====${dragAmount.x},Y======${dragAmount.y}")
                            // use offsetX value as horizontal speed
                            // use -offsetY value as vertical speed
                            // Calculate speed
                            val xSpeed = offsetX * 100 / 400
                            val ySpeed = -offsetY * 100 / 400

                            println("xSpeed= $xSpeed%, ySpeed = $ySpeed% ")
                        }
                    }
            ){

            }

        }
    }
}


