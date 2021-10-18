package gov.cms.bfd.sharedutils.generators.token.parser;

import com.google.common.annotations.VisibleForTesting;
import gov.cms.bfd.sharedutils.generators.exceptions.ParsingException;
import gov.cms.bfd.sharedutils.generators.token.TokenParserFactory;
import gov.cms.bfd.sharedutils.generators.token.pattern.TokenPattern;
import gov.cms.bfd.sharedutils.generators.token.pattern.TokenRepeat;
import java.util.Collection;
import java.util.Queue;

/**
 * This is the base class for the group parsing implementations.
 *
 * @param <C> The type of collection the implementing class uses for it's internal container.
 */
public abstract class AbstractTokenGroupParser<C extends Collection<TokenPattern>>
    implements TokenParser {

  @Override
  public TokenPattern parse(Queue<Character> patternStream) {
    C patterns = createCollection();

    TokenPattern previousPattern = null;

    while (!patternStream.isEmpty()) {
      TokenParser tokenParser = createTokenParser(patternStream);
      TokenPattern pattern = tokenParser.parse(patternStream);

      if (pattern instanceof TokenRepeat) {
        if (previousPattern != null) {
          pattern = new TokenRepeat(previousPattern, ((TokenRepeat) pattern).getRepeats());
          pattern.init();
          patterns.add(pattern);
          previousPattern = null;
        } else {
          throw new ParsingException("Repeat definition has no associated preceding token");
        }
      } else {
        if (previousPattern != null) {
          patterns.add(previousPattern);
        }

        previousPattern = pattern;
      }
    }

    if (previousPattern != null) {
      patterns.add(previousPattern);
    }

    if (patterns.size() == 1) {
      return patterns.iterator().next();
    } else if (!patterns.isEmpty()) {
      return createTokenPattern(patterns);
    }

    throw new ParsingException("Illegal empty allOf parser pattern.");
  }

  /**
   * Helper method to make unit testing easier.
   *
   * @param patternStream The pattern stream to help create the correct {@link TokenParser}.
   * @return A {@link TokenParser} for the next data of the pattern stream.
   */
  @VisibleForTesting
  TokenParser createTokenParser(Queue<Character> patternStream) {
    return TokenParserFactory.createTokenParser(patternStream);
  }

  /**
   * Creates an instance of the implementing class's collection container type.
   *
   * <p>This is implemented by the child class.
   *
   * @return Should provide a new instance of the implementing class's collection container type.
   */
  protected abstract C createCollection();

  /**
   * Creates an instance of the implementing class's specific {@link TokenPattern} implementation.
   *
   * <p>This is implemented by the child class.
   *
   * @param patterns The patterns to be added to the child implementation's {@link TokenPattern}
   *     implementation instance.
   * @return The {@link TokenPattern} implementation object instance created.
   */
  protected abstract TokenPattern createTokenPattern(C patterns);
}
