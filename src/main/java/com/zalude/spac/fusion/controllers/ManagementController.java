package com.zalude.spac.fusion.controllers;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by azurn on 11/12/16.
 */
@RestController
@RequestMapping(path = "/management")
public class ManagementController {

  @Value("${spac.auth0.jwt}")
  private String jwt;

  @Value("${spac.login.username}")
  private String username;

  @Value("${spac.login.password}")
  private String password;

  @RequestMapping(method = RequestMethod.POST, value = "/login")
  public ResponseEntity login(@RequestBody Map<String, Object> body) {
    if (validateLogin((String) body.get("username"), (String) body.get("password"))) {
      val identityInfo = new HashMap<String, Object>();
      identityInfo.put("jwt", jwt);
      return new ResponseEntity(identityInfo, HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
  }

  private boolean validateLogin(String username, String password) {
    return (username != null
        && username.equals(this.username)
        && password != null
        && password.equals(this.password));
  }
}
