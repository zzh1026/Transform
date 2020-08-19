package com.zzh.plugins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class HelloPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println " =========================================== "
        println " HelloPlugin is start "
        println " =========================================== "

//        project.gradle.addListener(new TaskListener())
        if (project.plugins.hasPlugin(AppPlugin)) {
            def android = project.extensions.getByType(AppExtension)
            android.registerTransform(new MyTransform(project))
        }
    }
}

