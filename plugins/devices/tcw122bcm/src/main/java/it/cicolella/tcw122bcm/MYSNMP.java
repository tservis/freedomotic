/**
 *
 * Copyright (c) 2009-2013 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.cicolella.tcw122bcm;

import it.freedomotic.app.Freedomotic;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class MYSNMP {

    public String SNMP_GET(String ipAddress, int port, String strOID, String community) {
        String strResponse = "";
        ResponseEvent response;
        Snmp snmp;
        try {
            OctetString community1 = new OctetString(community);
            String host = ipAddress + "/" + port;
            Address tHost = new UdpAddress(host);
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(community1);
            comtarget.setVersion(SnmpConstants.version1);
            comtarget.setAddress(tHost);
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(strOID)));
            pdu.setType(PDU.GET);
            snmp = new Snmp(transport);
            response = snmp.get(pdu, comtarget);
            if (response != null) {
                if (response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                    PDU pduresponse = response.getResponse();
                    strResponse = pduresponse.getVariableBindings().firstElement().toString();
                    if (strResponse.contains("=")) {
                        String strNewResponse[] = null;
                        int len = strResponse.indexOf("=");
                        strNewResponse = strResponse.split("=");
                        //System.out.println("The SNMP response to the OID requested is : " + strNewResponse[1]); //FOR DEBUG
                        strResponse = strNewResponse[1].trim();
                    }
                }
            } else {
                Freedomotic.logger.severe("TimeOut occured");
            }
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    public void SNMP_SET(String ipAddress, int port, String strOID, String value, String community) {
        ipAddress = ipAddress + "/" + port;
        Address tHost = GenericAddress.parse(ipAddress);
        Snmp snmp;
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setAddress(tHost);
            target.setRetries(2);
            target.setTimeout(5000);
            target.setVersion(SnmpConstants.version1); //Set the correct SNMP version here
            PDU pdu = new PDU();
            //Depending on the MIB attribute type, appropriate casting can be done here
            pdu.add(new VariableBinding(new OID(strOID), new Integer32(Integer.valueOf(value))));
            pdu.setType(PDU.SET);
            ResponseListener listener = new ResponseListener() {

                public void onResponse(ResponseEvent event) {
                    PDU strResponse;
                    String result;
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    strResponse = event.getResponse();
                    if (strResponse != null) {
                        result = strResponse.getErrorStatusText();
                        System.out.println("Set Status is: " + result);
                    }
                }
            };
            snmp.send(pdu, target, null, listener);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

