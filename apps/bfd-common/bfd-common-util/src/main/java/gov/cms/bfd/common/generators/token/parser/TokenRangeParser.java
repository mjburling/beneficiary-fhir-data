package gov.cms.bfd.common.generators.token.parser;

import com.google.common.annotations.VisibleForTesting;
import gov.cms.bfd.common.generators.token.TokenParser;
import gov.cms.bfd.common.generators.token.TokenPattern;
import gov.cms.bfd.common.generators.token.pattern.TokenRange;
import gov.cms.bfd.common.generators.token.pattern.TokenSingleton;
import java.util.Queue;

public class TokenRangeParser implements TokenParser {

  @Override
  public TokenPattern parse(Queue<Character> patternStream) {
    TokenPattern pattern;
    char token = patternStream.remove();

    if (!patternStream.isEmpty() && patternStream.peek() == '-') {
      pattern = parseRange(token, patternStream);
    } else {
      pattern = new TokenSingleton(token);
    }

    return pattern;
  }

  @VisibleForTesting
  TokenPattern parseRange(char lowerBound, Queue<Character> patternStream) {
    TokenPattern pattern;
    patternStream.remove(); // remove hyphen

    if (!patternStream.isEmpty()) {
      char upperBound = patternStream.remove();

      if (lowerBound >= 'a') {
        pattern = parseWithBoundsCheck(lowerBound, upperBound, 'z');
      } else if (lowerBound >= 'A') {
        pattern = parseWithBoundsCheck(lowerBound, upperBound, 'Z');
      } else {
        pattern = parseWithBoundsCheck(lowerBound, upperBound, '9');
      }
    } else {
      throw new IllegalArgumentException("Unexpected end of token pattern.");
    }

    return pattern;
  }

  @VisibleForTesting
  TokenPattern parseWithBoundsCheck(char lowerBound, char upperBound, char maxBound) {
    if (upperBound >= lowerBound && upperBound <= maxBound) {
      return new TokenRange(lowerBound, upperBound);
    }

    throw new IllegalArgumentException(
        "Invalid range defined in token pattern '" + lowerBound + "-" + upperBound + "'");
  }
}
