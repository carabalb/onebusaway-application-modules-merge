/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.webapp.actions.admin;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.onebusaway.users.model.UserIndex;
import org.onebusaway.users.model.UserIndexKey;
import org.onebusaway.users.services.UserIndexTypes;
import org.onebusaway.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Results( {@Result(type = "redirectAction", name = "userCreated", params = {
    "actionName", "user-for-id", "id", "${userId}", "parse", "true"})})
public class CreateLoginAction extends ActionSupport {

  private static final long serialVersionUID = 1L;

  private PasswordEncoder _passwordEncoder;

  private UserService _userService;

  private String _userName;

  private String _password;

  private String _password2;

  private int _userId;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    _passwordEncoder = passwordEncoder;
  }

  @Autowired
  public void setUserService(UserService userService) {
    _userService = userService;
  }

  public void setUserName(String userName) {
    _userName = userName;
  }

  public String getUserName() {
    return _userName;
  }

  public void setPassword(String password) {
    _password = password;
  }

  public String getPassword() {
    return _password;
  }

  public void setPassword2(String password2) {
    _password2 = password2;
  }

  public String getPassword2() {
    return _password2;
  }

  public int getUserId() {
    return _userId;
  }

  @SkipValidation
  @Override
  public String execute() {
    return SUCCESS;
  }

  @Validations(requiredStrings = {
      @RequiredStringValidator(fieldName = "userName", key = ""),
      @RequiredStringValidator(fieldName = "password", key = ""),
      @RequiredStringValidator(fieldName = "password2", key = "")})
  public String submit() {

    if (!_password.equals(_password2))
      return INPUT;

    String credentials = _passwordEncoder.encodePassword(_password, _userName);
    UserIndexKey key = new UserIndexKey(UserIndexTypes.USERNAME, _userName);
    UserIndex userIndex = _userService.getOrCreateUserForIndexKey(key,
        credentials, false);

    if (userIndex == null)
      return null;

    _userId = userIndex.getUser().getId();

    return "userCreated";
  }
}
