package com.editor.video.smart

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

data class AudioKeywordSegment(
    val keyword: String,
    val startTimeMs: Long,
    val endTimeMs: Long
)

data class SmartCameraMotion(
    val motionType: String,
    val ffmpegZoompanFilter: String
)

class NarrationAwareCameraEngine {

    // Escolha Inteligente Automática entre todas as 20 Animações de Câmera
    fun autoSelectBestCameraMotion(
        bitmap: Bitmap,
        narrationKeywords: List<AudioKeywordSegment>,
        onComplete: (SmartCameraMotion) -> Unit
    ) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        val objectDetector = ObjectDetection.getClient(options)

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                var selectedMotion: SmartCameraMotion? = null

                // 1. Tenta sincronizar a palavra da narração com um objeto da imagem
                for (obj in detectedObjects) {
                    val labels = obj.labels.map { it.text.lowercase() }
                    val matchedKeyword = narrationKeywords.find { segment ->
                        labels.any { label -> label.contains(segment.keyword.lowercase()) }
                    }

                    if (matchedKeyword != null) {
                        selectedMotion = buildMotionFromBox(obj.boundingBox, bitmap.width, bitmap.height)
                        break
                    }
                }

                // 2. Se não houver palavra correspondente, foca no primeiro objeto detectado
                if (selectedMotion == null && detectedObjects.isNotEmpty()) {
                    selectedMotion = buildMotionFromBox(detectedObjects.first().boundingBox, bitmap.width, bitmap.height)
                }

                // 3. Fallback: Escolha automática entre as 20 animações disponíveis
                val finalMotion = selectedMotion ?: SmartCameraMotion(
                    motionType = "AUTO_SELECTED",
                    ffmpegZoompanFilter = CameraAnimation.values().random().ffmpegFilter
                )

                onComplete(finalMotion)
            }
            .addOnFailureListener {
                onComplete(
                    SmartCameraMotion(
                        motionType = "FALLBACK",
                        ffmpegZoompanFilter = CameraAnimation.PAN_LEFT_TO_RIGHT.ffmpegFilter
                    )
                )
            }
    }

    private fun buildMotionFromBox(box: Rect, imageWidth: Int, imageHeight: Int): SmartCameraMotion {
        val centerX = box.centerX()
        val centerY = box.centerY()

        val filter = when {
            centerX < imageWidth / 2 && centerY < imageHeight / 2 -> CameraAnimation.FOCUS_TOP_LEFT.ffmpegFilter
            centerX >= imageWidth / 2 && centerY < imageHeight / 2 -> CameraAnimation.FOCUS_TOP_RIGHT.ffmpegFilter
            centerX < imageWidth / 2 && centerY >= imageHeight / 2 -> CameraAnimation.FOCUS_BOTTOM_LEFT.ffmpegFilter
            else -> CameraAnimation.FOCUS_BOTTOM_RIGHT.ffmpegFilter
        }

        return SmartCameraMotion(motionType = "OBJECT_FOCUSED", ffmpegZoompanFilter = filter)
    }
}
