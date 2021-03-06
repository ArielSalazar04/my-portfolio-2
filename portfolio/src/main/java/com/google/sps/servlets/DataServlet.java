// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
    private List<String> quotes;
    
    @Override
    public void init(){
        quotes = new ArrayList<>();
        quotes.add("Courage is not having the strength to go on; it is going on when you don't have the strength.\n- Theodore Roosevelt");
        quotes.add("It is not difficult to overcome your fears, you just have to know your weaknesses.\n- Rubén Rodríguez");
        quotes.add("I never lose. I either win or I learn.\n- Nelson Mandela");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String quote = quotes.get(ThreadLocalRandom.current().nextInt(0, quotes.size()));
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().println(quote);
    }
}
