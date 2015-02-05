/*
 * Copyright 2014 the original author or authors.
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
package grails.artefact.controller.support

import groovy.transform.CompileStatic
import org.grails.web.converters.Converter

/**
 * 
 * @author Jeff Brown
 * @author Graeme Rocher
 *
 * @since 3.0
 */
@CompileStatic
trait ResponseRenderer {
    private RenderHelper helper = new RenderHelper()
    
    void render(o) {
        helper.invokeRender this, o.inspect()
    }

    void render(CharSequence txt) {
        helper.invokeRender this, txt
    }

    void render(Map args) {
        helper.invokeRender this, args
    }

    void render(Closure c) {
        helper.invokeRender this, c
    }

    void render(Map args, Closure c) {
        helper.invokeRender this, args, c
    }

    void render(Map args, CharSequence body) {
        helper.invokeRender this, args, body
    }

    void render(Converter<?> converter) {
        helper.invokeRender this, converter
    }
}