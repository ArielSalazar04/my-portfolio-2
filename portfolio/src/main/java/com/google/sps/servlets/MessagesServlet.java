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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;

@WebServlet("/messages")
public class MessagesServlet extends HttpServlet {
    
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String languageCode = request.getQueryString().split("=")[1];
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        Gson gson = new Gson();
        Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        List<String> messages = new ArrayList<>();
        
        for (Entity entity : results.asIterable()){
            String content = (String) entity.getProperty("content");
            Translation translation = translate.translate(content, Translate.TranslateOption.targetLanguage(languageCode));
            content = translation.getTranslatedText();
            messages.add(content);
        }
        
        String json = gson.toJson(messages);

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String content = getParameter(request, "text-input", "").trim();
        
        if (!content.equals("")){
            Entity messageEntity = new Entity("Message");
            Date date = new Date(System.currentTimeMillis());

            messageEntity.setProperty("content", content);
            messageEntity.setProperty("timestamp", date);
            datastore.put(messageEntity);
        }

        response.sendRedirect("message-me.html");
    }
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) 
            return defaultValue;
        return value;
    }
}