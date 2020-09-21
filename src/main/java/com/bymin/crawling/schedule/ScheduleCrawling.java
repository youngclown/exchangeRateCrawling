package com.bymin.crawling.schedule;

import com.bymin.crawling.domain.ExchangeJson;
import com.bymin.crawling.domain.ExchangeRate;
import com.bymin.crawling.domain.Kebhana;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ScheduleCrawling {

  private static final SimpleDateFormat hh = new SimpleDateFormat("HH");
  private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
  private static final SimpleDateFormat yyyyMMddDot = new SimpleDateFormat("yyyy.MM.dd");
  private static final String EXCHANGERATE = "EXCHANGERATE";
  private static final String REDIS_DEPT = ":";

  private static final Map<String, String> map = new HashMap<>();


  public ScheduleCrawling() {
    map.put("JPY", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_JPYKRW");
    map.put("USD", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_USDKRW");
    map.put("EUR", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_EURKRW");
    map.put("CNH", "http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_CNYKRW");
  }

  @Scheduled(fixedDelay = (1000 * 60 * 60)) // 1시간
  public void crawlingExchangeRate() {
    try {
      String statsDttm = yyyyMMdd.format(new Date());
      String statsHh = hh.format(new Date());


      // 20200916
      String url = "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?data=AP01&authkey=oY6SJ9Wnt4Ptd9N9RfMAAU3T8UMbQA5Q&searchdate=" + statsDttm;
      Document doc = Jsoup.connect(url)
              .header("content-type", "application/json;charset=UTF-8")
              .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
              .header("accept-encoding", "gzip, deflate, br")
              .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
              .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
              .ignoreContentType(true).get();

      log.info("url start {}", doc.text());
      ArrayList<ExchangeJson> koreaeximLists = new ArrayList<>();
      ArrayList<ExchangeRate> exchangeRates = new ArrayList<>();
      try {
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<ExchangeJson>>() {
        }.getType();
        koreaeximLists = gson.fromJson(doc.text(), userListType);
      } catch (Exception e) {
        e.printStackTrace();
      }

      url = "http://fx.kebhana.com/FER1101M.web";
      doc = Jsoup.connect(url)
              .header("content-type", "application/json;charset=UTF-8")
              .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
              .header("accept-encoding", "gzip, deflate, br")
              .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
              .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
              .ignoreContentType(true).get();

      ArrayList<Kebhana> kebhanaArrayList = new ArrayList<>();
      try {

        String buffer = doc.text();
        buffer = buffer.substring(51);
        buffer = buffer.substring(0, buffer.length() - 2);
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<Kebhana>>() {
        }.getType();
        kebhanaArrayList = gson.fromJson(buffer, userListType);
      } catch (Exception e) {
        e.printStackTrace();
      }


      for (String curUnit : map.keySet()) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setStatsDttm(statsDttm);
        exchangeRate.setStatsHh(statsHh);
        exchangeRate.setIso(curUnit);

        boolean dafaultPass = false;
        try {
          url = map.get(curUnit);
          doc = Jsoup.connect(url)
                  .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                  .ignoreContentType(true).get();

          Elements exchangeList = doc.getElementsByClass("section_exchange").get(0).getElementsByTag("tr");

          for (Element element : exchangeList) {
            if (element.text().contains(yyyyMMddDot.format(new Date()))) { // yyyyMMdd.format(new Date());
              Elements tdList = element.getElementsByTag("td");
              if ("JPY".equals(curUnit)) {
                exchangeRate.setExchangeRate(Float.parseFloat(tdList.get(6).text().replace(",", "")) / 100);
              } else {
                exchangeRate.setExchangeRate(Float.parseFloat(tdList.get(6).text().replace(",", "")));
              }
              exchangeRate.setExchangeRateTpCode("02");
              exchangeRates.add(exchangeRate);
              dafaultPass = true;
              break;
            }
          }
        } catch (Exception e) {
          e.printStackTrace(); // TODO 텔레그램 알람.
        }

        if (dafaultPass) continue;

        for (Kebhana kebhana : kebhanaArrayList) {
          if (kebhana != null && kebhana.get통화명().contains(curUnit)) {
            if ("JPY".equals(curUnit)) {
              exchangeRate.setExchangeRate(Float.parseFloat(kebhana.get송금_전신환받으실때().replace(",", "")) / 100);
            } else {
              exchangeRate.setExchangeRate(Float.parseFloat(kebhana.get송금_전신환받으실때().replace(",", "")));
            }
            exchangeRate.setExchangeRateTpCode("03");
            exchangeRates.add(exchangeRate);
            dafaultPass = true;
            break;
          }
        }

        if (dafaultPass) continue;

        for (ExchangeJson eJson : koreaeximLists) {
          if (eJson.getCur_unit().contains(curUnit)) {
            if ("JPY".equals(curUnit)) {
              exchangeRate.setExchangeRate(Float.parseFloat(eJson.getTtb().replace(",", "")) / 100);
            } else {
              exchangeRate.setExchangeRate(Float.parseFloat(eJson.getTtb().replace(",", "")));
            }
            exchangeRate.setExchangeRateTpCode("01");
            exchangeRates.add(exchangeRate);
            dafaultPass = true;
            break;
          }
        }

        // false ?? redis setting!!!!!
        if (!dafaultPass) {
          log.info(">>>>>>>>>>>> ERROR >>>>>>>>>>>>> {} _ {}", statsDttm, statsHh);
        }
      }

      StringBuilder botMessage = new StringBuilder();
      // MixExchangeRate> exchangeRates
      botMessage.append(statsDttm).append(", ").append(statsHh).append("시 : 환율\n");
      for (ExchangeRate mixExchangeRate : exchangeRates) {
        botMessage.append(mixExchangeRate.getIso()).append("\t").append(mixExchangeRate.getExchangeRate()).append("\t").append("01".equals(mixExchangeRate.getExchangeRateTpCode()) ? "DEFAULT" : "NAVER").append("\n");
      }

      log.info(botMessage.toString());  // 텔레그램 호출 API 추가

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

