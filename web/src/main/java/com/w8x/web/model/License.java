package com.w8x.web.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class License {
  private String key;
  private String name;
  private String spdx_id;
  private String url;
  private String node_id;
}
