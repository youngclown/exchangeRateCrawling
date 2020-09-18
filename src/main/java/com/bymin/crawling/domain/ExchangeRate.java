package com.bymin.crawling.domain;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExchangeRate {
  String statsDttm;
  String statsHh;
  String iso = "KRW";
  float exchangeRate;
  String exchangeRateTpCode = "01";
}
