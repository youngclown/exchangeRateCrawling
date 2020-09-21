package com.bymin.crawling.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// { "통화명": "미국 USD", "현찰사실때":"1182.23", "현찰파실때":"1141.57", "송금_전신환보내실때":"1173.20", "송금_전신환받으실때":"1150.60", "매매기준율":"1161.90" }
@Getter
@Setter
@ToString
public class Kebhana {
  String 통화명;
  String 현찰사실때;
  String 현찰파실때;
  String 송금_전신환보내실때;
  String 송금_전신환받으실때;
  String 매매기준율;
}
