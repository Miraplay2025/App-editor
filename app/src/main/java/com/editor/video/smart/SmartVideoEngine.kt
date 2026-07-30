package com.editor.video.smart

import kotlin.random.Random

// Enumeração com 20 Animações de Câmera Automáticas
enum class CameraAnimation(val ffmpegFilter: String) {
    PAN_LEFT_TO_RIGHT("zoompan=z='min(zoom+0.0015,1.5)':x='if(gte(zoom,1.5),x,x+1)':y='y':d=125"),
    PAN_RIGHT_TO_LEFT("zoompan=z='min(zoom+0.0015,1.5)':x='if(gte(zoom,1.5),x,x-1)':y='y':d=125"),
    TILT_BOTTOM_TO_TOP("zoompan=z='min(zoom+0.0015,1.5)':x='x':y='if(gte(zoom,1.5),y,y-1)':d=125"),
    TILT_TOP_TO_BOTTOM("zoompan=z='min(zoom+0.0015,1.5)':x='x':y='if(gte(zoom,1.5),y,y+1)':d=125"),
    ZOOM_IN_CENTER("zoompan=z='min(zoom+0.002,1.3)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=125"),
    ZOOM_OUT_CENTER("zoompan=z='max(1.5-zoom*0.002,1.0)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=125"),
    FOCUS_TOP_LEFT("zoompan=z='min(zoom+0.002,1.4)':x='0':y='0':d=125"),
    FOCUS_TOP_RIGHT("zoompan=z='min(zoom+0.002,1.4)':x='iw-(iw/zoom)':y='0':d=125"),
    FOCUS_BOTTOM_LEFT("zoompan=z='min(zoom+0.002,1.4)':x='0':y='ih-(ih/zoom)':d=125"),
    FOCUS_BOTTOM_RIGHT("zoompan=z='min(zoom+0.002,1.4)':x='iw-(iw/zoom)':y='ih-(ih/zoom)':d=125"),
    DIAGONAL_TL_TO_BR("zoompan=z='1.2':x='x+1':y='y+1':d=125"),
    DIAGONAL_TR_TO_BL("zoompan=z='1.2':x='x-1':y='y+1':d=125"),
    DIAGONAL_BL_TO_TR("zoompan=z='1.2':x='x+1':y='y-1':d=125"),
    DIAGONAL_BR_TO_TL("zoompan=z='1.2':x='x-1':y='y-1':d=125"),
    SLOW_PUSH_IN("zoompan=z='min(zoom+0.0008,1.15)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=125"),
    SLOW_PULL_OUT("zoompan=z='max(1.15-0.0008*on,1.0)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=125"),
    TRACK_SUBJECT_LEFT("zoompan=z='1.25':x='x+1.5':y='ih/2-(ih/zoom/2)':d=125"),
    TRACK_SUBJECT_RIGHT("zoompan=z='1.25':x='x-1.5':y='ih/2-(ih/zoom/2)':d=125"),
    VERTICAL_SCAN_UP("zoompan=z='1.3':x='iw/2-(iw/zoom/2)':y='y-2':d=125"),
    VERTICAL_SCAN_DOWN("zoompan=z='1.3':x='iw/2-(iw/zoom/2)':y='y+2':d=125")
}

// Enumeração com 20 Transições para Sorteio Aleatório
enum class TransitionEffect(val displayName: String, val xfadeName: String) {
    FADE("Fade Cross", "fade"), WIPE_LEFT("Wipe Esquerda", "wipeleft"),
    WIPE_RIGHT("Wipe Direita", "wiperight"), WIPE_UP("Wipe Cima", "wipeup"),
    WIPE_DOWN("Wipe Baixo", "wipedown"), SLIDE_LEFT("Slide Esquerda", "slideleft"),
    SLIDE_RIGHT("Slide Direita", "slideright"), SLIDE_UP("Slide Cima", "slideup"),
    SLIDE_DOWN("Slide Baixo", "slidedown"), CIRCLE_CROP("Círculo", "circlecrop"),
    RECT_CROP("Retângulo", "rectcrop"), DISTORTION("Distorção", "distance"),
    FADE_BLACK("Fade Preto", "fadeblack"), FADE_WHITE("Fade Branco", "fadewhite"),
    RADIAL("Radial", "radial"), SMOOTH_LEFT("Suave Esquerda", "smoothleft"),
    SMOOTH_RIGHT("Suave Direita", "smoothright"), HORZ_OPEN("Abertura Horiz.", "horzopen"),
    VERT_OPEN("Abertura Vert.", "vertopen"), DISSOLVE("Dissolver", "dissolve")
}

class SmartVideoEngine {
    // Sorteia uma transição aleatória entre as permitidas pelo usuário
    fun getRandomTransition(userSelectedTransitions: List<TransitionEffect>): TransitionEffect {
        return if (userSelectedTransitions.isNotEmpty()) {
            userSelectedTransitions.random()
        } else {
            TransitionEffect.FADE
        }
    }
}
