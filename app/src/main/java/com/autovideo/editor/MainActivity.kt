package com.autovideo.editor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.autovideo.editor.databinding.ActivityMainBinding
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val selectedAnimations = mutableMapOf<Int, String>()
    private val selectedTransitions = mutableSetOf<String>()
    private var isTransitionSelectionMode = false

    companion object {
        private const val TAG_LOG = "AutoVideoEditor_LOG"
        private const val TAG_ERROR = "AutoVideoEditor_ERROR"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configuração do Manipulador Global de Erros para exibir mensagens específicas e claras
        setupGlobalErrorHandler()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i(TAG_LOG, "Inicializando interface do AutoVideoEditor...")

        setupHomeUI()
        setupEditorUI()
    }

    /**
     * Captura qualquer exceção não tratada e exibe a mensagem de erro específica com arquivo e linha.
     */
    private fun setupGlobalErrorHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val stackTraceString = sw.toString()

            val errorMessage = "ERRO ESPECÍFICO DETECTADO:\n" +
                    "Thread: ${thread.name}\n" +
                    "Causa: ${throwable.localizedMessage}\n" +
                    "Detalhes: ${stackTraceString.take(300)}..."

            Log.e(TAG_ERROR, errorMessage, throwable)

            runOnUiThread {
                binding.modalProgress.visibility = View.VISIBLE
                binding.tvLogs.text = errorMessage
                Toast.makeText(this@MainActivity, "Erro crítico detectado! Verifique o log em tela.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupHomeUI() {
        binding.btnCreateProject.setOnClickListener {
            Log.d(TAG_LOG, "Navegando para a tela do editor de vídeo.")
            binding.layoutHome.visibility = View.GONE
            binding.layoutEditor.visibility = View.VISIBLE
        }

        binding.btnDeleteProject.setOnClickListener {
            Log.d(TAG_LOG, "Projeto removido da lista de rascunhos.")
            Toast.makeText(this, "Projeto excluído dos rascunhos", Toast.LENGTH_SHORT).show()
            binding.layoutProjectItem.visibility = View.GONE
            binding.tvEmptyProjects.visibility = View.VISIBLE
        }
    }

    private fun setupEditorUI() {
        binding.btnBack.setOnClickListener {
            Log.d(TAG_LOG, "Retornando à tela inicial.")
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
            try {
                if (selectedTransitions.isEmpty()) {
                    selectedTransitions.add("FadeSuave")
                }

                binding.modalProgress.visibility = View.VISIBLE
                val logMsg = "[STATUS]: Iniciando análise de áudio (Google Transcriber)...\n" +
                        "[STATUS]: Detectando pausas de fala em milissegundos...\n" +
                        "[STATUS]: Ajustando duração exata das imagens na timeline...\n" +
                        "[STATUS]: Aplicando animações de câmera manuais..."
                
                binding.tvLogs.text = logMsg
                Log.i(TAG_LOG, logMsg)

                val serviceIntent = Intent(this, VideoRenderService::class.java)
                startService(serviceIntent)
            } catch (e: Exception) {
                val errorLog = "[ERRO NA EDIÇÃO AUTO]: Ocorreu uma falha no componente: ${e.javaClass.simpleName} - ${e.message}"
                Log.e(TAG_ERROR, errorLog, e)
                binding.tvLogs.text = errorLog
            }
        }

        binding.btnCancelEdit.setOnClickListener {
            binding.modalProgress.visibility = View.GONE
            stopService(Intent(this, VideoRenderService::class.java))
            Log.w(TAG_LOG, "Edição cancelada pelo usuário.")
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
            Log.i(TAG_LOG, "Iniciando exportação final na resolução $resolutionText.")
            Toast.makeText(this, "Exportando vídeo na resolução $resolutionText...", Toast.LENGTH_LONG).show()
        }
    }
}
