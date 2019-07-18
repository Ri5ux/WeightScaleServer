package com.asx.wss.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Util
{
    public static String arrayToJson(String[] data)
    {
        return new Gson().toJson(data);
    }

    public static String toJson(Object o)
    {
        return new Gson().toJson(o);
    }

    public static String getFormattedOutputFromProcess(Process p) throws IOException
    {
        InputStreamReader is = new InputStreamReader(p.getInputStream());
        BufferedReader reader = new BufferedReader(is);
        TypePerfOutputMapping mapping = new TypePerfOutputMapping(reader);
        String json = mapping.toJson();

        if (WebServer.isVerbose())
        {
            System.out.println(json);
        }

        reader.close();

        return json;
    }

    public static class ComPortEntry
    {
        private String friendlyName;
        private String port;

        public ComPortEntry(String friendlyName, String port)
        {
            this.friendlyName = friendlyName;
            this.port = port;
        }

        public String getFriendlyName()
        {
            return friendlyName;
        }

        public String getPort()
        {
            return port;
        }
        
        @Override
        public String toString()
        {
            return String.format("[%s] %s", getPort(), getFriendlyName());
        }
    }

    public static ArrayList<ComPortEntry> getListOfComPorts()
    {
        String hive = "HKLM";
        String key = "HARDWARE\\DEVICEMAP\\SERIALCOMM";
        ArrayList<ComPortEntry> comPorts = new ArrayList<ComPortEntry>();
        Process p;

        try
        {
            p = Runtime.getRuntime().exec("reg query " + hive + "\\" + key);
            String result = readProcessOutput(p);
            String[] lines = result.split("\n");

            for (String s : lines)
            {
                if (!s.isEmpty() && !s.contains(key))
                {
                    String[] values = s.split("    ");
                    comPorts.add(new ComPortEntry(values[1], values[3]));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return comPorts;
    }

    public static String readProcessOutput(Process p) throws Exception
    {
        p.waitFor();
        InputStream stream = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String buffer = "";

        while (reader.ready())
        {
            String line = reader.readLine();
            buffer = buffer + line + "\n";
        }

        return buffer;
    }
}
