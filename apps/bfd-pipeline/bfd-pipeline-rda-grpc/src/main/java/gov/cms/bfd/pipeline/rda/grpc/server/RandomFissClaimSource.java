package gov.cms.bfd.pipeline.rda.grpc.server;

import gov.cms.bfd.pipeline.rda.grpc.RdaChange;
import gov.cms.mpsm.rda.v1.FissClaimChange;
import gov.cms.mpsm.rda.v1.fiss.FissClaim;
import java.util.NoSuchElementException;

/**
 * A ClaimSource implementation that generates and returns random FissClaim objects. The random
 * number seed and number of claims to return are specified in the constructor.
 */
public class RandomFissClaimSource implements MessageSource<FissClaim> {
  /** The random claim generator. */
  private final RandomFissClaimGenerator generator;
  /** The maximum number of claims to send. */
  private final int maxToSend;
  /** The number of sent claims. */
  private int sent;

  /**
   * Creates a new instance.
   *
   * @param seed the seed for randomization
   * @param maxToSend the max number of claims to send
   */
  public RandomFissClaimSource(long seed, int maxToSend) {
    this(RandomClaimGeneratorConfig.builder().seed(seed).build(), maxToSend);
  }

  /**
   * Creates a new instance.
   *
   * @param config the random generator configuration
   * @param maxToSend the max number of claims to send
   */
  public RandomFissClaimSource(RandomClaimGeneratorConfig config, int maxToSend) {
    generator = new RandomFissClaimGenerator(config);
    sent = 0;
    generator.setSequence(sent);
    this.maxToSend = maxToSend;
  }

  @Override
  public MessageSource<FissClaim> skip(long numberToSkip) throws Exception {
    sent += numberToSkip;
    generator.incrementSequence(numberToSkip);
    return this;
  }

  @Override
  public boolean hasNext() {
    return sent < maxToSend;
  }

  @Override
  public FissClaim next() {
    if (sent >= maxToSend) {
      throw new NoSuchElementException();
    }
    sent += 1;
    return generator.randomClaim();
  }

  @Override
  public void close() {}

  /**
   * Wraps the generator such that a message source is returned.
   *
   * @return the message source
   */
  public MessageSource<FissClaimChange> toClaimChanges() {
    return WrappedClaimSource.wrapFissClaims(
        this, generator.getClock(), RdaChange.MIN_SEQUENCE_NUM);
  }
}
