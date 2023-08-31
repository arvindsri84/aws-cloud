package com.arvindsri84.apps.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/secure")
public class DepositMoney {

    private static final Logger LOG = LoggerFactory.getLogger(DepositMoney.class);

    @GetMapping( path = "/deposit")
    public String deposit(Integer money){

        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Principal {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(principal instanceof org.springframework.security.oauth2.jwt.Jwt){
            var jwt = (org.springframework.security.oauth2.jwt.Jwt)principal;
            LOG.info("Claims {}", jwt.getClaims());
            LOG.info("username " + jwt.getClaim("username"));
        }

        LOG.info("Name {}", SecurityContextHolder.getContext().getAuthentication().getName());
        LOG.info("Details {}", SecurityContextHolder.getContext().getAuthentication().getDetails());
        return "Hello, thanks for depositing INR " + money;
    }
}
