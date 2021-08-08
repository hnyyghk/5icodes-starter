package com._5icodes.starter.webmvc.log;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LogHttpServletResponse extends HttpServletResponseWrapper {
    private volatile ByteArrayOutputStream byteArrayOutputStream;
    private volatile ServletOutputStream servletOutputStream;

    public LogHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return getResponse().getWriter();
    }

    public String reportResponse() {
        int size = byteArrayOutputStream == null ? 0 : byteArrayOutputStream.size();
        try {
            if (size > 0) {
                return byteArrayOutputStream.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            return String.format("%d bytes Binary data", size);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletOutputStream == null) {
            ServletOutputStream outputStream = super.getOutputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
            servletOutputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return outputStream.isReady();
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                    outputStream.setWriteListener(writeListener);
                }

                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                    byteArrayOutputStream.write(b);
                }
            };
        }
        return servletOutputStream;
    }
}