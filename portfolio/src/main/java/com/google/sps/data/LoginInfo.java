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

package com.google.sps.data;

/** Class containing login information. */
public class LoginInfo {

  private boolean loggedIn;
  private String redirectUrl;
  private String userEmail;
  private String nickname;

  // using default constructor only

  public boolean getLoginStatus() {
    return loggedIn;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getNickname() {
    return nickname;
  }

  public void setLoginStatus(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
