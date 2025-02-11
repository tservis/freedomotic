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
package it.freedomotic.marketplace;

import it.freedomotic.app.Freedomotic;

import java.util.ArrayList;
import java.util.Collection;

import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel Pulido
 */
public class MarketPlaceService {

    private static MarketPlaceService service;
    private Lookup marketPlaceLookup;
    private Collection<? extends IMarketPlace> marketPlaces;
    private Template<IMarketPlace> marketPlaceTemplate;
    private Result<IMarketPlace> marketPlaceResults;

    /**
     * Creates a new instance of DictionaryService
     */
    private MarketPlaceService() {
        try {
        marketPlaceLookup = Lookup.getDefault();
        marketPlaceTemplate = new Template<IMarketPlace>(IMarketPlace.class);
        marketPlaceResults = marketPlaceLookup.lookup(marketPlaceTemplate);
        marketPlaces = marketPlaceResults.allInstances();
        marketPlaceResults.addLookupListener(new LookupListener() {

            @Override
            public void resultChanged(LookupEvent e) {
                LOG.severe("Lookup has changed");
            }
        });
        }catch (Exception e) {
            LOG.warning(Freedomotic.getStackTraceInfo(e));
        }
    }

    public static synchronized MarketPlaceService getInstance() {
        if (service == null) {
            service = new MarketPlaceService();
        }

        return service;
    }

    public ArrayList<IPluginPackage> getPackageList() {
        ArrayList<IPluginPackage> packageList = null;

        try {
            packageList = new ArrayList<IPluginPackage>();

            for (IMarketPlace market : marketPlaces) {
                packageList.addAll(market.getAvailablePackages());
            }
        } catch (Exception e) {
            LOG.warning(Freedomotic.getStackTraceInfo(e));
        }

        return packageList;
    }

    public ArrayList<IPluginPackage> getPackageList(IPluginCategory category) {
        ArrayList<IPluginPackage> packageList = null;

        try {
            packageList = new ArrayList<IPluginPackage>();

            for (IMarketPlace market : marketPlaces) {
                packageList.addAll(market.getAvailablePackages(category));
            }
        } catch (Exception e) {
            LOG.warning(Freedomotic.getStackTraceInfo(e));
        }

        return packageList;
    }

    public ArrayList<IPluginCategory> getCategoryList() {
        ArrayList<IPluginCategory> categoryList = null;

        try {
            categoryList = new ArrayList<IPluginCategory>();

            for (IMarketPlace market : marketPlaces) {
                categoryList.addAll(market.getAvailableCategories());
            }
        } catch (Exception e) {
            LOG.warning(Freedomotic.getStackTraceInfo(e));
        }

        return categoryList;
    }
    private static final Logger LOG = Logger.getLogger(MarketPlaceService.class.getName());
}
