package com.github.reneranzinger.audible.list.util.scrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

public class WebUtil
{
    public static byte[] getWebPage(String a_url, int a_retry) throws IOException
    {
        try
        {
            URL t_url = new URL(a_url);
            BufferedInputStream t_inputStream = new BufferedInputStream(t_url.openStream());
            // read 4 kb at once
            byte[] t_buffer = new byte[4 * 1024];
            ByteArrayOutputStream t_outputStream = new ByteArrayOutputStream();
            int t_numberBytesRead = 0;

            while ((t_numberBytesRead = t_inputStream.read(t_buffer)) != -1)
            {
                t_outputStream.write(t_buffer, 0, t_numberBytesRead);
            }
            t_inputStream.close();
            t_outputStream.close();
            return t_outputStream.toByteArray();
        }
        catch (ConnectException t_tExection)
        {
            System.out.println(t_tExection.getMessage());
            if (a_retry > 0)
            {
                return WebUtil.getWebPage(a_url, a_retry - 1);
            }
            else
            {
                throw t_tExection;
            }
        }
    }
}
