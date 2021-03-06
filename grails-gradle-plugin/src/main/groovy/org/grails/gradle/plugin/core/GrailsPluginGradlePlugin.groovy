package org.grails.gradle.plugin.core

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.language.jvm.tasks.ProcessResources

/*
 * Copyright 2014 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A Gradle plugin for Grails plugins
 *
 * @author Graeme Rocher
 * @since 3.0
 *
 */
class GrailsPluginGradlePlugin extends GrailsGradlePlugin {

    @Override
    void apply(Project project) {
        super.apply(project)

        configureProjectNameAndVersionASTMetadata(project)

        configurePluginResources(project)

        configurePluginJarTask(project)

        configureSourcesJarTask(project)

    }

    @Override
    protected List<File> resolveGrailsSourceDirs(Project project) {
        def grailsSourceDirs = super.resolveGrailsResourceDirs(project)
        if (project.file("src/main/ast").isDirectory()) {
            grailsSourceDirs << project.file("src/main/ast")
        }
        grailsSourceDirs
    }

    protected void configureSourcesJarTask(Project project) {
        def sourcesJar = project.tasks.create("sourcesJar", Jar).configure {
            classifier = 'sources'
            from sourceSets.main.allSource
        }
    }

    protected void configurePluginJarTask(Project project) {
        project.jar {
            exclude "logback.groovy"
        }
    }

    protected void configurePluginResources(Project project) {
        def copyCommands = project.task(type: Copy, "copyCommands") {
            from "${project.projectDir}/src/main/scripts"
            into "${processResources.destinationDir}/META-INF/commands"
        }

        def copyTemplates = project.task(type: Copy, "copyTemplates") {
            from "${project.projectDir}/src/main/templates"
            into "${processResources.destinationDir}/META-INF/templates"
        }

        ProcessResources processResources = (ProcessResources) project.tasks.getByName('processResources')
        processResources.dependsOn(copyCommands, copyTemplates)
        processResources {
            rename "application.yml", "plugin.yml"
            exclude "spring/resources.groovy"
        }
    }

    protected void configureProjectNameAndVersionASTMetadata(Project project) {
        def configScriptTask = project.tasks.create('configScript')

        def configFile = project.file("$project.buildDir/config.groovy")
        configScriptTask.outputs.file(configFile)

        def projectName = project.name
        def projectVersion = project.version
        configScriptTask.inputs.property('name', projectName)
        configScriptTask.inputs.property('version', projectVersion)
        configScriptTask.doLast {
            configFile.parentFile.mkdirs()
            configFile.text = """
withConfig(configuration) {
    inline(phase: 'CONVERSION') { source, context, classNode ->
        classNode.putNodeMetaData('projectVersion', '$projectVersion')
        classNode.putNodeMetaData('projectName', '$projectName')
    }
}
"""
        }
        project.tasks.getByName('compileGroovy').dependsOn(configScriptTask)
        project.compileGroovy {
            groovyOptions.configurationScript = configFile
        }
    }
}
