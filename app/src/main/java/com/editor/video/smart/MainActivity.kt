package com.editor.video.smart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoEditorScreen()
                }
            }
        }
    }
}

@Composable
fun VideoEditorScreen() {
    var selectedTransitions by remember { mutableStateOf(TransitionEffect.values().toList()) }
    var estimatedMB by remember { mutableStateOf("12.5 MB") }
    var isRendering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Editor de Vídeo Automático",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Animações de Câmera: 100% Automático (Inteligente)")
                Text(
                    text = "O sistema selecionará dinamicamente entre as 20 animações com base na narração e na imagem.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selecione as Transições Permitidas (Sorteio Aleatório):")

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(TransitionEffect.values()) { transition ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedTransitions.contains(transition),
                        onCheckedChange = { checked ->
                            selectedTransitions = if (checked) {
                                selectedTransitions + transition
                            } else {
                                selectedTransitions - transition
                            }
                        }
                    )
                    Text(text = transition.displayName)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tamanho Estimado: $estimatedMB", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { isRendering = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRendering
        ) {
            Text(if (isRendering) "Renderizando Vídeo..." else "Renderizar e Baixar Vídeo")
        }
    }
}
