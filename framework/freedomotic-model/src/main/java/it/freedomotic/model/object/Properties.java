/**
 *
 * Copyright (c) 2009-2013 Freedomotic team
 * http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Freedomotic; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.freedomotic.model.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author gpt
 */
public class Properties implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	HashMap<String,String> propertyList;
            
    public Properties()
    {
        propertyList = new HashMap<String,String>();    
    }

    public Properties(HashMap<String, String> prop) {
        propertyList = prop;
    }

    public Set<String> stringPropertyNames() {
        return propertyList.keySet();
    }

    public String getProperty(String name) {
        return propertyList.get(name);
    }

    public void setProperty(String name, String value) {
        propertyList.put(name, value);
    }

    public Set<Entry<String, String>> entrySet() {
        return propertyList.entrySet();
    }

    public int size() {
        return propertyList.size();
    }
}
