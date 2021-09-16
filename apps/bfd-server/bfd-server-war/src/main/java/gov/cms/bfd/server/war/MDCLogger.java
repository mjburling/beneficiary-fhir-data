package gov.cms.bfd.server.war;

import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.TokenParam;
import com.google.common.base.Strings;
import org.slf4j.MDC;

public class MDCLogger {

  private static MDCLogger single_instance = null;

  /**
   * Singleton method makes sure there is one instance and one instance only of the MDCLogger Class
   */
  public static synchronized MDCLogger Singleton() {
    if (single_instance == null) {
      single_instance = new MDCLogger();
    }
    return single_instance;
  }

  public void Log(String key, String value) {
    if (Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(value)) return;

    MDC.put(key, value);
  }

  public void Log(String key, TokenParam value) {
    if (value == null) return;
    Log(key, value.getValueNotNull());
  }

  public void Log(String key, DateRangeParam value) {
    if (value == null) return;
    Log(key, value.toString());
  }

  public void Log(String key, int value) {
    Log(key, String.valueOf(value));
  }
}
