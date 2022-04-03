package com._5icodes.starter.common.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.*;
import org.springframework.lang.NonNull;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@UtilityClass
public class ClassUtils {
    private final Map<Method, String> configKeyCache = new ConcurrentHashMap<>();

    /**
     * @see feign.Feign#configKey(Class, Method)
     */
    public String configKey(Class<?> targetType, Method m) {
        return configKeyCache.computeIfAbsent(m, method -> {
            StringBuilder builder = new StringBuilder();
            builder.append(targetType.getSimpleName());
            builder.append('#').append(method.getName()).append('(');
            for (Type param : method.getGenericParameterTypes()) {
                param = TypeUtils.resolve(targetType, targetType, param);
                builder.append(TypeUtils.getRawType(param).getSimpleName()).append(',');
            }
            if (method.getParameterTypes().length > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.append(')').toString();
        });
    }

    @NonNull
    private final ClassLoader CLASS_LOADER = org.springframework.util.ClassUtils.getDefaultClassLoader();

    Method defineClassByBytesMethod;

    static {
        try {
            defineClassByBytesMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClassByBytesMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void defineClassByBytes(byte[] bytes) {
        defineClassByBytesMethod.invoke(CLASS_LOADER, bytes, 0, bytes.length);
    }

    @SneakyThrows
    public void changeMethod(String className, String methodName, String methodDesc, Consumer<MethodVisitor> consumer) {
        InputStream stream = CLASS_LOADER.getResourceAsStream(className.replaceAll("\\.", "/") + ".class");
        if (stream == null) {
            throw new ClassNotFoundException(className);
        }
        ClassReader classReader = new ClassReader(stream);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classAdapter = new ClassVisitor(Opcodes.ASM7, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                if (!name.equals(methodName) || !desc.equals(methodDesc)) {
                    return methodVisitor;
                }
                return new MethodVisitor(Opcodes.ASM7, methodVisitor) {
                    @Override
                    public void visitCode() {
                        consumer.accept(mv);
                    }
                };
            }
        };
        classReader.accept(classAdapter, ClassReader.EXPAND_FRAMES);
        byte[] bytes = classWriter.toByteArray();
        defineClassByBytes(bytes);
    }
}