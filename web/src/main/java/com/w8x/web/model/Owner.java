package com.w8x.web.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Owner {

    private String name;
    private String email;
    private String login;
    private long id;
    private String node_id;
    private String avatar_url;
    private String gravatar_id;
    private String url;
    private String html_url;
    private String followers_url;
    private String following_url;
    private String gists_url;
    private String starred_url;
    private String subscriptions_url;
    private String organizations_url;
    private String repos_url;
    private String events_url;
    private String received_events_url;
    private String type;
    private boolean site_admin;


}