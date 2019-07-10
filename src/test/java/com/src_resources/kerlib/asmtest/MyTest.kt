package com.src_resources.kerlib.asmtest

import jdk.internal.org.objectweb.asm.ClassWriter
import org.junit.Test
import org.objectweb.asm.Opcodes.*
import java.util.*

//import org.objectweb.asm.commons.Method

class MyTest
{
    @Test
    fun testHello()
    {
//        println("Hello")
        val className = "Example_" + UUID.randomUUID().toString().replace('-', '_')

        val cw = ClassWriter(0)
        cw.visit(V1_1, ACC_PUBLIC, className, null, "java/lang/Object", null)
        cw.visitSource(".dynamic", null)
        var mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
        mv.visitInsn(RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("This message is from ASM!!!")
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V")
        mv.visitInsn(RETURN)
        mv.visitMaxs(2, 2)
        mv.visitEnd()
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "invokeMethod", "()V", null, null)
        mv.visitMethodInsn(INVOKESTATIC, "com/src_resources/kerlib/asmtest/InvokeTest", "invokeMe", "()V")
        mv.visitInsn(RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
        val code = cw.toByteArray()
        val cl = MyClassLoader()
        val exampleClass = cl.defineClassByBytecode(className, code)
        //exampleClass.methods[0].invoke(null, emptyArray<Any>())
        JavaExecutor.callExampleMethod(exampleClass.methods[1])
        JavaExecutor.callInvokeMethod(exampleClass.methods[0])

//        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
//        cw.visit(V1_1, ACC_PUBLIC, className, null, "java/lang/Object", null)
//        // Constructor Method
//        val m = Method
    }
}
