package com.autovideo.editor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.autovideo.editor.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val selectedAnimations = mutableMapOf<Int, String>()
    private val selectedTransitions = mutableSetOf<String>()
    private var isTransitionSelectionMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHomeUI()
        setupEditorUI()
    }

    private fun setupHomeUI() {
        binding.btnCreateProject.setOnClickListener {
            binding.layoutHome.visibility = View.GONE
            binding.layoutEditor.visibility = View.VISIBLE
        }

        binding.btnDeleteProject.setOnClickListener {
            Toast.makeText(this, "Projeto excluído dos rascunhos", Toast.LENGTH_SHORT).show()
            binding.layoutProjectItem.visibility = View.GONE
            binding.tvEmptyProjects.visibility = View.VISIBLE
        }
    }

    private fun setupEditorUI() {
        binding.btnBack.setOnClickListener {
            binding.layoutEditor.visibility = View.GONE
            binding.layoutHome.visibility = View.VISIBLE
            Toast.makeText(this, "Projeto salvo automaticamente nos rascunhos", Toast.LENGTH_SHORT).show()
        }

        binding.btnToggleTransitionMode.setOnClickListener {
            isTransitionSelectionMode = !isTransitionSelectionMode
            if (isTransitionSelectionMode) {
                binding.btnToggleTransitionMode.setBackgroundColor(getColor(android.R.color.holo_green_dark))
                Toast.makeText(this, "Modo de Seleção de Transições Ativado", Toast.LENGTH_SHORT).show()
            } else {
                binding.btnToggleTransitionMode.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
            }
        }

        binding.btnRedArrowAlert.setOnClickListener {
            binding.timelineScrollView.smoothScrollTo(500, 0)
            binding.btnRedArrowAlert.visibility = View.GONE
        }

        binding.btnStartAutoEdit.setOnClickListener {
            if (selectedTransitions.isEmpty()) {
                // Adiciona uma transição padrão para garantir a liberação se o usuário não selecionou manualmente
                selectedTransitions.add("FadeSuave")
            }

            binding.modalProgress.visibility = View.VISIBLE
            binding.tvLogs.text = "Iniciando análise de áudio (Google Transcriber)...\nDetectando pausas de fala...\nAjustando duração das imagens na timeline...\nAplicando animações de câmera..."

            // Inicia o serviço em segundo plano
            val serviceIntent = Intent(this, VideoRenderService::class.java)
            startService(serviceIntent)
        }

        binding.btnCancelEdit.setOnClickListener {
            binding.modalProgress.visibility = View.GONE
            stopService(Intent(this, VideoRenderService::class.java))
            Toast.makeText(this, "Processamento cancelado", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownloadVideo.setOnClickListener {
            binding.modalExportOptions.visibility = View.VISIBLE
        }

        binding.btnStartExport.setOnClickListener {
            binding.modalExportOptions.visibility = View.GONE
            val selectedResolutionId = binding.rgResolution.checkedRadioButtonId
            val resolutionText = when (selectedResolutionId) {
                R.id.rb320p -> "320p"
                R.id.rb480p -> "480p"
                R.id.rb720p -> "720p"
                else -> "1080p"
            }
            Toast.makeText(this, "Exportando vídeo na resolução $resolutionText...", Toast.LENGTH_LONG).show()
        }
    }
}
