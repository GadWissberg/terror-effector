package com.gadarts.te.renderer.handlers

import com.gadarts.te.renderer.handlers.actions.handler.ActionsHandlerImpl
import com.gadarts.te.renderer.handlers.cursor.CursorHandlerImpl
import com.gadarts.te.renderer.handlers.drawing.DrawingHandlerImpl

enum class Handlers(val handlerInstance: BaseHandler) {
    CAMERA(CameraHandler()),
    CURSOR(CursorHandlerImpl()),
    ACTIONS(ActionsHandlerImpl()),
    DRAWING(DrawingHandlerImpl()),

}
