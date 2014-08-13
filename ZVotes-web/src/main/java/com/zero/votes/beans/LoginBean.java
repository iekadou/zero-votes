/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.beans;

import com.zero.votes.web.util.Message;
import java.io.Serializable; 
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
 
 
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
 
    private static final long serialVersionUID = 4864684684864L;
 
    private static final String[] users = {"test:test","lol:123"};
    
    private String username;
    private String password;
    private boolean loggedIn;
 
    public String doLogin() {
        for (String user: users) {
            String dbUsername = user.split(":")[0];
            String dbPassword = user.split(":")[1];
             
            if (dbUsername.equals(username) && dbPassword.equals(password)) {
                loggedIn = true;
                return UrlsPy.ACCOUNT.getUrl(true);
            }
        }
        
        Message.addInternationalizedMessage(FacesMessage.SEVERITY_ERROR, "LoginFailed");
         
        return UrlsPy.LOGIN.getUrl();
         
    }

    public String doLogout() {
        loggedIn = false;
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        
        Message.addInternationalizedMessage(FacesMessage.SEVERITY_INFO, "LogoutSuccessful");
        
        return UrlsPy.LOGIN.getUrl();
    }

    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public boolean isLoggedIn() {
        return loggedIn;
    }
 
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

}
