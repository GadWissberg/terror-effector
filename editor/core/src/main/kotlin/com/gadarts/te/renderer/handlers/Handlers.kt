package com.gadarts.te.renderer.handlers

import com.gadarts.te.renderer.handlers.actions.ActionsHandler
import com.gadarts.te.renderer.handlers.drawing.DrawingHandler

enum class Handlers(val handlerInstance: BaseHandler) {
    CAMERA(CameraHandler()),
    CURSOR(CursorHandler()),
    ACTIONS(ActionsHandler()),
    DRAWING(DrawingHandler()),

}
