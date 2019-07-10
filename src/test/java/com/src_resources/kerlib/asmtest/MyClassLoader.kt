package com.src_resources.kerlib.asmtest

import org.objectweb.asm.Opcodes

internal class MyClassLoader : ClassLoader() {
    fun defineClassByBytecode(className: String, byteArr: ByteArray): Class<*> {
        return defineClass(className, byteArr, 0, byteArr.size)
    }
}
