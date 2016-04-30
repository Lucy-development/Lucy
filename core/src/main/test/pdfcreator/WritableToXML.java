package pdfcreator;

import java.util.Map;

/**
 * Created by Priit on 26.01.2016.
 */
public interface WritableToXML {

    /**
     * @return Map where key equals to XML id
     * value corresponds to the data to be inserted
     */
    Map<String, String> getContentForXML();

}
