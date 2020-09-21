package com.bymin.crawling.schedule;

import com.bymin.crawling.domain.ExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ScheduleCrawling {
  private static final Map<String, String> crawlingMap = new HashMap<>();
  public ScheduleCrawling() {
//    crawlingMap.put("JPY", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_JPYKRW");
//    crawlingMap.put("USD", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_USDKRW");
//    crawlingMap.put("EUR", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_EURKRW");
//    crawlingMap.put("CNH", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_CNYKRW");
  }

  private static final SimpleDateFormat hh = new SimpleDateFormat("HH");
  private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
  private static final SimpleDateFormat yyyyMMddDot = new SimpleDateFormat("yyyy.MM.dd");

  @Scheduled(fixedDelay = (1000 * 60 * 60)) // 1시간
  public void crawlingExchangeRate() {

    String statsDttm = yyyyMMdd.format(new Date());
    String statsHh = hh.format(new Date());


    try {
      String url;
      Document doc;

      for (String curUnit : crawlingMap.keySet()) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setStatsDttm(statsDttm);
        exchangeRate.setStatsHh(statsHh);
        exchangeRate.setIso(curUnit);

        url = crawlingMap.get(curUnit);
        doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                .ignoreContentType(true).get();

        Elements list = doc.getElementsByClass("section_exchange").get(0).getElementsByTag("tr");

        for (Element element : list) {
          if (element.text().contains(yyyyMMddDot.format(new Date()))) { // yyyyMMdd.format(new Date());
            Elements tdList = element.getElementsByTag("td");
            if ("JPY".equals(curUnit)) {
              exchangeRate.setExchangeRate(Float.parseFloat(tdList.get(6).text().replace(",", "")) / 100);
            } else {
              exchangeRate.setExchangeRate(Float.parseFloat(tdList.get(6).text().replace(",", "")));
            }
            exchangeRate.setExchangeRateTpCode("02");
            break;
          }
        }

        log.info("exchangeRate > {}",exchangeRate);
      }


      url = "http://fx.kebhana.com/FER1101M.web";
      doc = Jsoup.connect(url)
              .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
              .ignoreContentType(true).get();

      log.info("doc > {}",doc.text());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
