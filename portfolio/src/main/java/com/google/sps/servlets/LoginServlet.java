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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.LoginInfo;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        LoginInfo loginInfo = new LoginInfo();

        if (userService.isUserLoggedIn()) {
            String userEmail = userService.getCurrentUser().getEmail();
            String urlToRedirectToAfterUserLogsOut = "/photography.html";
            String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

            // If user has not set a nickname, redirect to nickname page
            String nickname = getUserNickname(userService.getCurrentUser().getUserId());

            // user is logged in and has a nickname
            loginInfo.setUserEmail(userEmail);
            loginInfo.setRedirectUrl(logoutUrl);
            loginInfo.setLoginStatus(true);
            loginInfo.setNickname(nickname);
        } else {
            String urlToRedirectToAfterUserLogsIn = "/photography.html";
            String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

            loginInfo.setRedirectUrl(loginUrl);
            loginInfo.setLoginStatus(false);
        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(loginInfo));
    }

    /** Returns the nickname of the user with id, or null if the user has not set a nickname. */
    private String getUserNickname(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query =
            new Query("UserInfo")
                .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity == null) {
            return null;
        }
        String nickname = (String) entity.getProperty("nickname");
        return nickname;
    }
}
