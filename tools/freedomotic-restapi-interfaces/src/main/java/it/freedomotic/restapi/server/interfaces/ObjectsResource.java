/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.freedomotic.restapi.server.interfaces;

import it.freedomotic.model.object.EnvObject;

import java.util.ArrayList;

import org.restlet.resource.Get;

/**
 *
 * @author gpt
 */
public interface ObjectsResource extends FreedomoticResource {

    @Get("object|gwt_object")
    public ArrayList<EnvObject> retrieveObjects();
}
