package com.gadarts.te

import com.badlogic.gdx.utils.Disposable
import java.lang.reflect.Field
import kotlin.reflect.KClass

object GeneralUtils {
    fun <T : Any> disposeObject(instance: T, kClass: KClass<T>) {
        val fields: Array<Field> = kClass.java.declaredFields
        for (field in fields) {
            if (Disposable::class.java.isAssignableFrom(field.type)) {
                field.isAccessible = true
                val fieldValue = field.get(instance)
                if (fieldValue is Disposable) {
                    fieldValue.dispose()
                }
            }
        }
    }

}
