package com.asx.wss.web;

import java.io.File;
import java.util.ArrayList;

import com.asx.wss.web.Util.ComPortEntry;

public class ServiceWrapper
{
    private static final File        CONFIG_FILE   = new File("settings.json");

    private static SerialWeightScale scale;
    private static boolean           appRunning    = false;
    private static long              reconnectTime = 0;
    private static Config            config;

    public static void main(String[] args)
    {
        System.out.println("Service starting...");
        config = new Config(CONFIG_FILE);
        config.load();
        WebServer.startWebServer();
        appRunning = true;

        while (appRunning)
        {
            if (scale == null)
            {
                long time = System.currentTimeMillis();

                if (reconnectTime > 0 && time - reconnectTime > 5000 || reconnectTime == 0)
                {
                    reconnectTime = time;
                    System.out.println("Scale not detected, scanning COM ports...");
                    ArrayList<ComPortEntry> comPorts = Util.getListOfComPorts();

                    String scalePort = "";

                    for (ComPortEntry port : comPorts)
                    {
                        System.out.println(port.toString());

                        if (port.getFriendlyName().equalsIgnoreCase("\\Device\\BthModem0"))
                        {
                            scalePort = port.getPort();
                        }
                    }

                    if (!scalePort.isEmpty())
                    {
                        scale = new SerialWeightScale();
                        scale.setPortId(scalePort);
                        scale.setCanConnect(true);
                    }
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
