/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.graphql.servlet.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

// TODO: tbatie - 6/12/17 - Remove this once batched requests are properly implemented
public class DelegateResponse implements HttpServletResponse {
    private HttpServletResponse servletResponse;
    private PrintWriter writer;
    private ByteArrayOutputStream stream;

    public DelegateResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
        stream = new ByteArrayOutputStream();
        writer = new PrintWriter(stream);
    }

    public String getDelegatedResponse() {
        writer.close();
        return new String(stream.toByteArray());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public void addCookie(Cookie cookie) {
        servletResponse.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return servletResponse.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return servletResponse.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return servletResponse.encodeRedirectURL(url);
    }

    @Override
    public String encodeUrl(String url) {
        return servletResponse.encodeUrl(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return servletResponse.encodeRedirectUrl(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        servletResponse.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        servletResponse.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        servletResponse.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        servletResponse.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        servletResponse.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        servletResponse.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        servletResponse.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        servletResponse.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        servletResponse.addIntHeader(name, value);
    }

    @Override
    public void setStatus(int sc) {
        servletResponse.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        servletResponse.setStatus(sc, sm);
    }

    @Override
    public int getStatus() {
        return servletResponse.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return servletResponse.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return servletResponse.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return servletResponse.getHeaderNames();
    }

    @Override
    public String getCharacterEncoding() {
        return servletResponse.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return servletResponse.getContentType();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        servletResponse.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        servletResponse.setContentLength(len);
    }

    @Override
    public void setContentType(String type) {
        servletResponse.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        servletResponse.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return servletResponse.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        servletResponse.flushBuffer();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletResponse.getOutputStream();
    }

    @Override
    public void resetBuffer() {
        servletResponse.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return servletResponse.isCommitted();
    }

    @Override
    public void reset() {
        servletResponse.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        servletResponse.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return servletResponse.getLocale();
    }
}
