package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldBean {
  private String from;
  private String to;
  @Builder.Default private boolean optional = true;
  private ColumnBean column;
  private String transformer;
  @Singular Map<String, String> transformerOptions = new HashMap<>();

  public String getTo() {
    return to == null ? from : to;
  }

  public boolean hasTransformer() {
    return !Strings.isNullOrEmpty(transformer);
  }

  public Optional<String> transformerOption(String optionName) {
    if (transformerOptions == null || transformerOptions.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(transformerOptions.get(optionName)).map(String::trim);
    }
  }

  public Optional<List<String>> transformerListOption(String optionName) {
    return transformerOption(optionName).map(value -> ImmutableList.copyOf(value.split(" *, *")));
  }
}