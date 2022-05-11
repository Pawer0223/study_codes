package com.example.filter_and_interceptor.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RequestWrapper extends HttpServletRequestWrapper {

    private final String body;

    private String readBody(HttpServletRequest request) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String buffer;
        while ((buffer = input.readLine()) != null) {
            builder.append(buffer.trim());
        }
        return builder.toString();
    }

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = readBody(request);
    }

    public String getBody() {
        return this.body;
    }
}
