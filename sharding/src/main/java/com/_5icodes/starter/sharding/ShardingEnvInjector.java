package com._5icodes.starter.sharding;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.ClassUtils;
import org.objectweb.asm.Label;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.objectweb.asm.Opcodes.*;

public class ShardingEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        String originClassName = "io.shardingjdbc.core.util.InlineExpressionParser";
        String asmClassName = originClassName.replaceAll("\\.", "/");
        ClassUtils.changeMethod(originClassName,
                "evaluate",
                "(Ljava/util/List;)Ljava/util/List;",
                methodVisitor -> {
                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    methodVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
                    methodVisitor.visitInsn(DUP);
                    methodVisitor.visitVarInsn(ALOAD, 1);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
                    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V", false);
                    methodVisitor.visitVarInsn(ASTORE, 2);
                    Label label1 = new Label();
                    methodVisitor.visitLabel(label1);
                    methodVisitor.visitTypeInsn(NEW, "groovy/lang/GroovyShell");
                    methodVisitor.visitInsn(DUP);
                    methodVisitor.visitMethodInsn(INVOKESPECIAL, "groovy/lang/GroovyShell", "<init>", "()V", false);
                    methodVisitor.visitVarInsn(ASTORE, 3);
                    Label label2 = new Label();
                    methodVisitor.visitLabel(label2);
                    methodVisitor.visitVarInsn(ALOAD, 1);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
                    methodVisitor.visitVarInsn(ASTORE, 4);
                    Label label3 = new Label();
                    methodVisitor.visitLabel(label3);
                    methodVisitor.visitFrame(F_APPEND, 3, new Object[]{"java/util/List", "groovy/lang/GroovyShell", "java/util/Iterator"}, 0, null);
                    methodVisitor.visitVarInsn(ALOAD, 4);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                    Label label4 = new Label();
                    methodVisitor.visitJumpInsn(IFEQ, label4);
                    methodVisitor.visitVarInsn(ALOAD, 4);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                    methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
                    methodVisitor.visitVarInsn(ASTORE, 5);
                    Label label5 = new Label();
                    methodVisitor.visitLabel(label5);
                    methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
                    methodVisitor.visitInsn(DUP);
                    methodVisitor.visitVarInsn(ALOAD, 5);
                    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
                    methodVisitor.visitVarInsn(ASTORE, 6);
                    Label label6 = new Label();
                    methodVisitor.visitLabel(label6);
                    methodVisitor.visitVarInsn(ALOAD, 5);
                    methodVisitor.visitLdcInsn("{");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
                    Label label7 = new Label();
                    methodVisitor.visitJumpInsn(IFNE, label7);
                    Label label8 = new Label();
                    methodVisitor.visitLabel(label8);
                    methodVisitor.visitVarInsn(ALOAD, 2);
                    methodVisitor.visitVarInsn(ALOAD, 5);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
                    methodVisitor.visitInsn(POP);
                    Label label9 = new Label();
                    methodVisitor.visitLabel(label9);
                    methodVisitor.visitJumpInsn(GOTO, label3);
                    methodVisitor.visitLabel(label7);
                    methodVisitor.visitFrame(F_APPEND, 2, new Object[]{"java/lang/String", "java/lang/StringBuilder"}, 0, null);
                    methodVisitor.visitVarInsn(ALOAD, 5);
                    methodVisitor.visitLdcInsn("\"");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
                    Label label10 = new Label();
                    methodVisitor.visitJumpInsn(IFNE, label10);
                    Label label11 = new Label();
                    methodVisitor.visitLabel(label11);
                    methodVisitor.visitVarInsn(ALOAD, 6);
                    methodVisitor.visitInsn(ICONST_0);
                    methodVisitor.visitLdcInsn("\"");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "insert", "(ILjava/lang/String;)Ljava/lang/StringBuilder;", false);
                    methodVisitor.visitInsn(POP);
                    methodVisitor.visitLabel(label10);
                    methodVisitor.visitFrame(F_SAME, 0, null, 0, null);
                    methodVisitor.visitVarInsn(ALOAD, 5);
                    methodVisitor.visitLdcInsn("\"");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "endsWith", "(Ljava/lang/String;)Z", false);
                    Label label12 = new Label();
                    methodVisitor.visitJumpInsn(IFNE, label12);
                    Label label13 = new Label();
                    methodVisitor.visitLabel(label13);
                    methodVisitor.visitVarInsn(ALOAD, 6);
                    methodVisitor.visitLdcInsn("\"");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                    methodVisitor.visitInsn(POP);
                    methodVisitor.visitLabel(label12);
                    methodVisitor.visitFrame(F_SAME, 0, null, 0, null);
                    methodVisitor.visitVarInsn(ALOAD, 2);
                    methodVisitor.visitVarInsn(ALOAD, 3);
                    methodVisitor.visitVarInsn(ALOAD, 6);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/GroovyShell", "evaluate", "(Ljava/lang/String;)Ljava/lang/Object;", false);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
                    methodVisitor.visitInsn(POP);
                    Label label14 = new Label();
                    methodVisitor.visitLabel(label14);
                    methodVisitor.visitJumpInsn(GOTO, label3);
                    methodVisitor.visitLabel(label4);
                    methodVisitor.visitFrame(F_CHOP, 3, null, 0, null);
                    methodVisitor.visitVarInsn(ALOAD, 2);
                    methodVisitor.visitInsn(ARETURN);
                    Label label15 = new Label();
                    methodVisitor.visitLabel(label15);
                    methodVisitor.visitLocalVariable("expression", "Ljava/lang/StringBuilder;", null, label6, label14, 6);
                    methodVisitor.visitLocalVariable("each", "Ljava/lang/String;", null, label5, label14, 5);
                    methodVisitor.visitLocalVariable("this", "L" + asmClassName + ";", null, label0, label15, 0);
                    methodVisitor.visitLocalVariable("inlineExpressions", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/String;>;", label0, label15, 1);
                    methodVisitor.visitLocalVariable("result", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/Object;>;", label1, label15, 2);
                    methodVisitor.visitLocalVariable("shell", "Lgroovy/lang/GroovyShell;", null, label2, label15, 3);
                    methodVisitor.visitEnd();
                });
        super.onAllProfiles(env, application);
    }
}