package com.example.civiladvocacy;

import java.io.Serializable;

public class Official implements Serializable
{

    public String name;
    public String office;
    public String party;

    public String fbLink;
    public String twLink;
    public String ytLink;
    public String webUrl;

    public String address;
    public String email;
    public String phoneNumber;
    public String imageUrl;

    public Official(String name, String office, String party, String fbLink, String twLink, String ytLink, String webURL, String address, String email, String phoneNumber, String imageUrl)
    {
        this.name = name;
        this.office = office;
        this.party = party;
        this.fbLink = fbLink;
        this.twLink = twLink;
        this.ytLink = ytLink;
        this.webUrl = webURL;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }

    public String getName()
    {
        return name;
    }

    public String getOffice()
    {
        return office;
    }

    public String getParty()
    {
        return party;
    }

    public String getFbLink()
    {
        return fbLink;
    }

    public String getTwLink()
    {
        return twLink;
    }

    public String getYtLink()
    {
        return ytLink;
    }

    public String getWebUrl()
    {
        return webUrl;
    }

    public String getAddress()
    {
        return address;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }
}


