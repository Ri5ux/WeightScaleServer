package com.asx.wss.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class RequestHandler
{
    private String request;

    public RequestHandler(String request)
    {
        this.request = request;
    }

    public String getRequest()
    {
        return request;
    }

    public abstract void handleRequest(PrintWriter out, OutputStream dataOut);

    public static interface IDataHandler
    {
        public Object getData();
    }

    public static class StandardRequestHandler extends RequestHandler
    {
        private IDataHandler dataHandler;

        public StandardRequestHandler(String request, IDataHandler iDataHandler)
        {
            super(request);
            this.dataHandler = iDataHandler;
        }

        @Override
        public void handleRequest(PrintWriter out, OutputStream dataOut)
        {
            Object data = this.dataHandler.getData();
            
            if (data == null)
            {
                data = "";
            }
            
            int dataLength = 0;
            byte[] bytes = null;

            try
            {
                if (data instanceof String)
                {
                    String text = (String) data;
                    dataLength = text.length();
                    bytes = text.getBytes();

                    WebServer.buildGenericHeader(out, dataOut, dataLength);
                }

                WebServer.sendData(out, dataOut, bytes, dataLength);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public IDataHandler getDataHandler()
        {
            return dataHandler;
        }
    }

    public static class CommandRequestHandler extends RequestHandler
    {
        private String command;

        public CommandRequestHandler(String request, String command)
        {
            super(request);
            this.command = command;
        }

        public void handleRequest(PrintWriter out, OutputStream dataOut)
        {
            try
            {
                Process p = Runtime.getRuntime().exec(command);
                String o = Util.getFormattedOutputFromProcess(p);

                WebServer.buildGenericHeader(out, dataOut, o.length());
                WebServer.sendData(out, dataOut, o.getBytes(), o.length());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public String getCommand()
        {
            return command;
        }
    }
}