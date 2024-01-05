package com.tdder.gradle

import java.time.Instant

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildNumberPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        project.getTasks().register("buildNumber") {
            project.getExtensions().getExtraProperties().set("builtTimestamp", Instant.now())

            doLast {
                // println "======== BuildNumberPlugin buildNumber doLast"
                final gitCommand = "git rev-parse --verify --short=8 HEAD"
                final p = gitCommand.execute(null, project.getRootDir())
                def w = new StringBuilder()
                p.waitForProcessOutput(w, System.err)
                final exitValue = p.exitValue()
                if (exitValue != 0) {
                    throw new Exception("execution failed: <${gitCommand}> ${exitValue}")
                }
                final buildNumber = w.toString().trim()
                assert buildNumber
                project.getExtensions().getExtraProperties().set('buildNumberValue', buildNumber)
            }
        }
    }

}
