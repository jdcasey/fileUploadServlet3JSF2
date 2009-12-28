/*
 * net/balusc/jsf/renderer/html/FileRenderer.java
 *
 * Copyright (C) 2009 BalusC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.balusc.jsf.renderer.html;

import java.io.File;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.FacesRenderer;

import net.balusc.http.multipart.MultipartRequest;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.TextRenderer;

/**
 * Faces renderer for <code>input type="file"</code> field.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2009/12/uploading-files-with-jsf-20-and-servlet.html
 */
@FacesRenderer(componentFamily = "javax.faces.Input", rendererType = "javax.faces.File")
public class FileRenderer extends TextRenderer {

    // Constants ----------------------------------------------------------------------------------

    private static final Attribute[] INPUT_ATTRIBUTES =
        AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXT);

    // Actions ------------------------------------------------------------------------------------

    @Override
    protected void getEndTextToRender
        (FacesContext context, UIComponent component, String currentValue)
            throws IOException
    {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("input", component);
        writeIdAttributeIfNecessary(context, writer, component);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("name", (component.getClientId(context)), "clientId");

        // Render styleClass, if any.
        String styleClass = (String) component.getAttributes().get("styleClass");
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        // Render standard HTMLattributes expect of styleClass.
        RenderKitUtils.renderPassThruAttributes(
            context, writer, component, INPUT_ATTRIBUTES, getNonOnChangeBehaviors(component));
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);
        RenderKitUtils.renderOnchange(context, component, false);

        writer.endElement("input");
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        rendererParamsNotNull(context, component);
        if (!shouldDecode(component)) {
            return;
        }
        String clientId = decodeBehaviors(context, component);
        if (clientId == null) {
            clientId = component.getClientId(context);
        }
        File file = ((MultipartRequest) context.getExternalContext().getRequest()).getFile(clientId);
        if (file != null) {
            ((UIInput) component).setSubmittedValue(file);
        }
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        // No need to convert File to String as the bean property ought be File as well.
        return submittedValue;
    }

}