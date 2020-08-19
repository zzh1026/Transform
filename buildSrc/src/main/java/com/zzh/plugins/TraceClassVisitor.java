package com.zzh.plugins;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 车主邦
 * ---------------------------
 * <p>
 * Created by zhaozh on 2020/8/19.
 */
public class TraceClassVisitor extends ClassVisitor {
    public String className;
    private int useApi;

    private boolean isABSClass = false;     //是否抽象方法或者接口

    public TraceClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.useApi = api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;

        //抽象方法或者接口
        if ((access & Opcodes.ACC_ABSTRACT) > 0 || (access & Opcodes.ACC_INTERFACE) > 0) {
            this.isABSClass = true;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (methodVisitor != null && !MethodFilter.isConstructor(name)) {
            System.out.println("trace method ================== " + name);
            return new TraceMethodVisitor(useApi, methodVisitor, access, name, descriptor);
        }
        return methodVisitor;
    }
}
