package socialnet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;
import socialnet.api.response.CurrencyRs;
import socialnet.exception.CurrencyParseException;
import socialnet.utils.CbRfParser;

import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class CurrencyService {
    private static final String USD = "R01235";
    private static final String EUR = "R01239";

    public CurrencyRs getCurrency(LocalDate date) {
        try {
            String usd = getCurrencyByDateAndName(date, "usd");
            String eur = getCurrencyByDateAndName(date, "eur");
            return new CurrencyRs(eur, usd);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new CurrencyRs("Сервис недоступен.","Сервис недоступен.");
    }
    public String getCurrencyByDateAndName(LocalDate date, String currency) throws RuntimeException {
        String currencyId;
        if (currency.equalsIgnoreCase("usd")) currencyId = USD;
        else currencyId = EUR;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String sDate = formatter.format(date);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.cbr.ru/scripts/XML_dynamic.asp")
                .path("/")
                .query("date_req1={date1}&date_req2={date2}&VAL_NM_RQ={CurrencyId}")
                .buildAndExpand(sDate, sDate, currencyId);

        SAXParser parser = CbRfParser.createParser();
        CbRfParser currencyParser = new CbRfParser();
        try {
            parser.parse(uriComponents.toUriString(), currencyParser);
            return currencyParser.getCurValue();
        } catch (SAXException | IOException e) {
            throw new CurrencyParseException(e.getMessage());
        }
    }
}
