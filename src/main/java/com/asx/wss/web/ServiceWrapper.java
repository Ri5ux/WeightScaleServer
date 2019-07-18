package com.asx.wss.web;

import java.util.ArrayList;

import com.asx.wss.web.Util.ComPortEntry;

public class ServiceWrapper
{
    private static SerialWeightScale scale;
    private static boolean           appRunning = false;

    public static void main(String[] args)
    {
        System.out.println("Service starting...");
        WebServer.startWebServer();
        appRunning = true;

        while (appRunning)
        {
            if (scale == null)
            {
                System.out.println("Scale not detected, scanning COM ports...");
                ArrayList<ComPortEntry> comPorts = Util.getListOfComPorts();

                String scalePort = "";

                for (ComPortEntry port : comPorts)
                {
                    if (port.getFriendlyName().equalsIgnoreCase("\\Device\\Silabser0"))
                    {
                        scalePort = port.getPort();
                        System.out.println(port.toString());
                    }
                }

                if (!scalePort.isEmpty())
                {
                    scale = new SerialWeightScale();
                    scale.setPortId(scalePort);
                    scale.setCanConnect(true);
                }
            }

            if (scale != null)
            {
                scale.update();
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static SerialWeightScale getWeightScale()
    {
        return scale;
    }

    public static void terminate()
    {
        appRunning = false;
    }

    public static boolean isAppRunning()
    {
        return appRunning;
    }

    public static void disconnectScale()
    {
        scale = null;
    }
}
