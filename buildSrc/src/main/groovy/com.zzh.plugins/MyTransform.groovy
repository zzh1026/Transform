package com.zzh.plugins

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * 车主邦
 * ---------------------------
 * <p>
 * Created by zhaozh on 2020/8/14.
 */
class MyTransform extends Transform {
    private Project project;

    MyTransform(Project project) {
        println "==========================register MyTransform=============================="
        this.project = project
    }

    //设置我们自定义的Transform对应的Task名称
    @Override
    String getName() {
        return "zzhTransform"
    }

    //指定输入的类型，通过这里设定，可以指定我们要处理的文件类型
    //这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    //指定Transfrom的作用范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    //是否支持增量更新
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        super.transform(transformInvocation)
        println 'transform-begin ====================================='

        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                traceSrcFiles(directoryInput, outputProvider)
            }

            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                traceJarFiles(jarInput, outputProvider)
            }
        }
    }

    static void traceSrcFiles(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        println 'transform--start directoryInput trace =====================================' + directoryInput.file.name
        if (directoryInput.file.isDirectory()) {    //文件夹
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                if (PluginConfig.isNeedTraceClass(name)) {
                    println 'transform--start need trace =====================================' + name
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new TraceClassVisitor(Opcodes.ASM5, classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }
            }
        }


        //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
        // 获取output目录
        def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                directoryInput.scopes, Format.DIRECTORY)

        //这里执行字节码的注入，不操作字节码的话也要将输入路径拷贝到输出路径
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    static void traceJarFiles(JarInput jarInput, TransformOutputProvider outputProvider) {
        println 'transform--start jar trace =====================================' + jarInput.file.name

        //jar文件一般是第三方依赖库jar文件
        // 重命名输出文件（同目录copyFile会冲突）
        def jarName = jarInput.name
        def md5Name = org.apache.commons.codec.digest.DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith('.jar')) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        def dest = outputProvider.getContentLocation(jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)


        //这里执行字节码注入,不操作字节码也要将输入路径拷贝到输出路径
        FileUtils.copyFile(jarInput.file, dest)

    }
}