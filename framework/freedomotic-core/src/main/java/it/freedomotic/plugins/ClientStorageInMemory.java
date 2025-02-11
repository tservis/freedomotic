/*Copyright 2009 Enrico Nicoletti
 eMail: enrico.nicoletti84@gmail.com

 This file is part of Freedomotic.

 Freedomotic is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 any later version.

 Freedomotic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with EventEngine; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package it.freedomotic.plugins;

import it.freedomotic.api.Client;
import it.freedomotic.api.Plugin;
import it.freedomotic.app.Freedomotic;
import it.freedomotic.bus.BusService;
import it.freedomotic.events.PluginHasChanged;
import it.freedomotic.events.PluginHasChanged.PluginActions;
import it.freedomotic.exceptions.DaoLayerException;
import it.freedomotic.util.Info;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import it.freedomotic.model.ds.Config;

/**
 * A storage of loaded plugins and connected clients
 */
public final class ClientStorageInMemory implements ClientStorage {

    private final List<Client> clients = new ArrayList<Client>();

    public ClientStorageInMemory() {
        //just injected
    }

    @Override
    public void add(Client c) {
        if (!clients.contains(c)) {
            if (isCompatible(c)) {
                //force injection as this class is not built by guice
                Freedomotic.INJECTOR.injectMembers(c);
                clients.add(c);
            } else {
                Client client =
                        createPluginPlaceholder(c.getName(),
                        "Plugin",
                        "Not compatible with this framework version v" + Info.getVersion());
                clients.add(client);
                LOG.log(Level.WARNING, "Plugin {0} is not compatible with this framework version v{1}", 
                        new Object[]{c.getName(), Info.getVersion()});
            }

            PluginHasChanged event =
                    new PluginHasChanged(ClientStorageInMemory.class,
                    c.getName(), PluginActions.ENQUEUE);
            final BusService busService = Freedomotic.INJECTOR.getInstance(BusService.class);
            busService.send(event);
            LOG.log(Level.CONFIG,
                    "{0} added to plugins list.",
                    c.getName());
        }
    }

    @Override
    public void remove(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);

            PluginHasChanged event =
                    new PluginHasChanged(ClientStorageInMemory.class,
                    c.getName(), PluginActions.DEQUEUE);
            final BusService busService = Freedomotic.INJECTOR.getInstance(BusService.class);
            busService.send(event);
        }
    }

    @Override
    public List<Client> getClients() {
        Collections.sort(clients,
                new ClientNameComparator());

        return Collections.unmodifiableList(clients);
    }

    @Override
    public Client get(String name) {
        for (Client client : clients) {
            if (client.getName().equalsIgnoreCase(name)) {
                return client;
            }
        }

        return null;
    }

    @Override
    public List<Client> getClients(String filterType) {
        List<Client> tmp = new ArrayList<Client>();
        for (Client client : clients) {
            if (client.getType().equalsIgnoreCase(filterType)) {
                tmp.add(client);
            }
        }

        Collections.sort(tmp,
                new ClientNameComparator());

        return Collections.unmodifiableList(tmp);
    }

    @Override
    public Client getClientByProtocol(String protocol) {
        for (Client client : clients) {
            if (client.getConfiguration().getStringProperty("protocol.name", "").equals(protocol)) {
                return client;
            }
        }

        return null;
    }

    @Override
    public boolean isLoaded(Client input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }

        return clients.contains(input);
    }

    /*
     * Checks if a plugin is already installed, if is an obsolete or newer
     * version
     */
    @Override
    public int compareVersions(String name, String version) {
        Client client = get(name);

        if ((client != null) && client instanceof Plugin) {
            //already installed
            //now check for version
            Plugin plugin = (Plugin) client;

            if (plugin.getVersion() == null) {
                return -1;
            }

            return getOldestVersion(plugin.getVersion(),
                    version);
        } else {
            //not installed
            return -1;
        }
    }

    protected boolean isCompatible(Client client) {
        //seach for a file called PACKAGE
        Properties config = client.getConfiguration().getProperties();
        int requiredMajor = getVersionProperty(config, "framework.required.major");
        int requiredMinor = getVersionProperty(config, "framework.required.minor");
        int requiredBuild = getVersionProperty(config, "framework.required.build");

        //checking framework version compatibility
        //required version must be older (or equal) then current version

        if (requiredMajor == Info.getMajor()) {
            if ((getOldestVersion(requiredMajor + "." + requiredMinor + "." + requiredBuild,
                    Info.getVersion()) <= 0)) {
                return true;
            }
        }

        return false;
    }

    private int getVersionProperty(Properties properties, String key) {
        //if property is not specified returns Integer.MAX_VALUE so it never match
        //if is a string returns 0 to match any value with "x"
        try {
            int value;

            if (properties.getProperty(key).equalsIgnoreCase("x")) {
                value = 0;
            } else {
                value =
                        Integer.parseInt(properties.getProperty(
                        key,
                        new Integer(Integer.MAX_VALUE).toString()));
            }

            return value;
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Calculates the oldest version between two version string
     * MAJOR.MINOR.BUILD (eg: 5.3.1)
     *
     * @param str1 first version string 5.3.0
     * @param str2 second version string 5.3.1
     * @return -1 if str1 is older then str2, 0 if str1 equals str2, 1 if str2
     * is older then str1
     */
    private int getOldestVersion(String str1, String str2) {
        //System.out.println("VERSION: " + str1 + " - " + str2);
        String MAX = Integer.toString(Integer.MAX_VALUE).toString();
        str1 = str1.replaceAll("x", MAX);
        str2 = str2.replaceAll("x", MAX);

        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;

        while ((i < vals1.length) && (i < vals2.length) && vals1[i].equals(vals2[i])) {
            i++;
        }

        if ((i < vals1.length) && (i < vals2.length)) {
            int diff = new Integer(vals1[i]).compareTo(new Integer(vals2[i]));

            return (diff < 0) ? (-1) : ((diff == 0) ? 0 : 1);
        }

        int result = (vals1.length < vals2.length) ? (-1) : ((vals1.length == vals2.length) ? 0 : 1);

        return result;
    }

    /**
     * Creates a placeholder plugin and adds it to the list of loaded plugins.
     * This plugin is just a mock object to inform the user that an object with
     * complete features is expected here. It can be used for example to list a
     * fake plugin that informs the user the real plugin cannot be loaded.
     *
     * @param simpleName
     * @param type
     * @param description
     * @return
     */
    @Override
    public Plugin createPluginPlaceholder(final String simpleName, final String type, final String description) {
        final Plugin placeholder =
                new Plugin(simpleName) {
            @Override
            public String getDescription() {
                if (description == null) {
                    return "Plugin Unavailable. Error on loading";
                } else {
                    return description;
                }
            }

            @Override
            public String getName() {
                return "Cannot start " + simpleName;
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public void start() {
            }

            @Override
            public void stop() {
            }

            @Override
            public boolean isRunning() {
                return false;
            }

            @Override
            public void showGui() {
            }

            @Override
            public void hideGui() {
            }
        };

        placeholder.setDescription(description);
        placeholder.configuration = new Config();

        return placeholder;
    }

    @Override
    public Client createObjectPlaceholder(final File template)
            throws DaoLayerException {
        return new ObjectPluginPlaceholder(template);
    }

    class ClientNameComparator
            implements Comparator<Client> {

        @Override
        public int compare(Client m1, Client m2) {
            //possibly check for nulls to avoid NullPointerException
            return m1.getName().compareTo(m2.getName());
        }
    }
    private static final Logger LOG = Logger.getLogger(ClientStorageInMemory.class.getName());
}
