package com.gadarts.te.renderer.handlers

import com.gadarts.te.renderer.handlers.actions.ActionsHandler

enum class Handlers(val handlerInstance: BaseHandler) {
    CAMERA(CameraHandler()),
    CURSOR(CursorHandler()),
    DRAWING(DrawingHandler()),
    ACTIONS(ActionsHandler());

}
