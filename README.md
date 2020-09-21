# TOY 프로젝트 VOL.6


## 환율 정보 수집 크롤링 프로젝트

### 네이버 화면 크롤링 

```json
{
  '미국 USD':'http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_USDKRW',
  '일본 JPY':'http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_JPYKRW',
  '유럽연합 EUR':'http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_EURKRW',
  '중국 CNY':'http://finance.naver.com/marketindex/exchangeDailyQuote.nhn?marketindexCd=FX_CNYKRW'
}
```

![이미지](/images/daily.PNG){: width="100%"}


### 하나은행 제공 json 
```
http://fx.kebhana.com/FER1101M.web
```

### 한국수출입은행 json

```
https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?data=AP01&authkey=보유키생성&searchdate=날짜
```

