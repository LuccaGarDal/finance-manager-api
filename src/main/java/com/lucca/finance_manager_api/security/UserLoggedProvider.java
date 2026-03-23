package com.lucca.finance_manager_api.security;

import com.lucca.finance_manager_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserLoggedProvider {

    public User getUser () { return SecurityUtils.getUserLogged();}

}
