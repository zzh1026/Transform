package com.zzh.plugins;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 车主邦
 * ---------------------------
 * <p>
 * Created by zhaozh on 2020/8/19.
 */
public class TraceMethodVisitor extends AdviceAdapter {
    private String name;


    public TraceMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
        this.name = name;
    }


    @Override
    protected void onMethodEnter() {
        System.out.println("================ 进入方法，可以织入代码 =======================");
        if (mv != null) {
            mv.visitLdcInsn("Alog");
            mv.visitLdcInsn("\u8fdb\u5165\u4e86\u65b9\u6cd5");
            mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        }
        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        System.out.println("================ 即将退出，可以织入代码 =======================");
        if (mv != null) {
            mv.visitLdcInsn("Alog");
            mv.visitLdcInsn("\u9000\u51fa\u4e86\u65b9\u6cd5");
            mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(POP);
        }
        super.onMethodExit(opcode);
    }


    @Override
    public void visitCode() {
        System.out.println("================ 正在进入代码内容 =======================");
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        System.out.println("================ 写入代码 =======================");
        super.visitInsn(opcode);
    }

    @Override
    public void visitEnd() {
        System.out.println("================ 方法完毕，完全退出 =======================");
        super.visitEnd();
    }
}
