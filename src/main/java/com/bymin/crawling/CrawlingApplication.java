package com.bymin.crawling;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//@SpringBootApplication
public class CrawlingApplication {

  public static void main(String[] args) {
//    SpringApplication.run(CrawlingApplication.class, args);


    try {
//      URL url = new URL("https://finance.naver.com/marketindex/");
//      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),"EUC-KR"));

//      String URL = "https://finance.naver.com/marketindex/?tabSel=exchange#tab_section";
//      Document doc = Jsoup.connect(URL).get();
//      System.out.println(doc.outerHtml());
//      Element exchangeList = doc.getElementById("exchangeList");
//
//      List<Element> spanList = exchangeList.getElementsByClass("value");
//      for (Element e:
//      spanList) {
//        System.out.println(e.outerHtml());
//      }
//      String line;
//      while ((line = reader.readLine()) != null) {
//        System.out.println(line);
//      }
      returnDocument("https://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_JPYKRW");
    } catch (Exception e) {
      e.printStackTrace();
    }


    // oY6SJ9Wnt4Ptd9N9RfMAAU3T8UMbQA5Q
  }

  private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy.MM.dd");

  public static void returnDocument(String url) {

    try {
      Document doc = Jsoup.connect(url)
              .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
              .ignoreContentType(true).get();


      Elements list = doc.getElementsByClass("section_exchange").get(0).getElementsByTag("tr");

      for (Element element : list) {
        if (element.text().contains(yyyyMMdd.format(new Date()))) { // yyyyMMdd.format(new Date());
          Elements tdList = element.getElementsByTag("td");
          System.out.println(tdList.get(6).text().replace(",", ""));
          break;
        }
      }

//      Gson gson = new Gson();
//      Type userListType = new TypeToken<ArrayList<ExchangeJSON>>() {
//      }.getType();
//      ArrayList<ExchangeJSON> lists = gson.fromJson(doc.text(), userListType);
//
//      List<String> exchangeList = Arrays.asList("USD","JPY","EUR","HKD");
//      for (ExchangeJSON eJson:lists) {
//
//        for (String curUnit:exchangeList) {
//          if(eJson.getCur_unit().contains(curUnit)) {
//            System.out.println(eJson);
//          }
//        }
//      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
