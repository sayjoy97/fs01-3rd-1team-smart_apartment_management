package com.jjld.domain.house.dto.login;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails{
    private final Integer houseDong;
    private final Integer houseHo;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.houseDong = Integer.valueOf(request.getParameter("houseDong"));
        this.houseHo = Integer.valueOf(request.getParameter("houseHo"));
    }

    public CustomWebAuthenticationDetails(Integer houseDong, Integer houseHo, boolean active){
        super((HttpServletRequest) null);
        this.houseDong = houseDong;
        this.houseHo = houseHo;
    }

    public Integer getHouseDong(){ return houseDong; }
    public Integer getHouseHo(){ return houseHo; }
}