package gov.cms.bfd.server.launcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.cms.bfd.ProcessOutputConsumer;
import gov.cms.bfd.server.launcher.ServerProcess.JvmDebugAttachMode;
import gov.cms.bfd.server.launcher.ServerProcess.JvmDebugEnableMode;
import gov.cms.bfd.server.launcher.ServerProcess.JvmDebugOptions;
import gov.cms.bfd.server.sharedutils.BfdMDC;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for {@link DataServerLauncherApp}.
 *
 * <p>These tests require the application launcher assembly and WAR to be built and available in the
 * local projects' <code>target/</code> directories. Accordingly, they may not run correctly in
 * Eclipse: if the binaries aren't built yet, they'll just fail, but if older binaries exist
 * (because you haven't rebuilt them), it'll run using the old code, which probably isn't what you
 * want.
 */
public final class DataServerLauncherAppIT {
  /** The POSIX signal number for the <code>SIGTERM</code> signal. */
  private static final int SIGTERM = 15;

  /**
   * Verifies that {@link DataServerLauncherApp} exits as expected when launched with no
   * configuration environment variables.
   *
   * @throws IOException (indicates a test error)
   * @throws InterruptedException (indicates a test error)
   */
  @Test
  public void missingConfig() throws IOException, InterruptedException {
    // Start the app with no config env vars.
    ProcessBuilder appRunBuilder =
        ServerProcess.createAppProcessBuilder(
            ServerTestUtils.getSampleWar(), new JvmDebugOptions(JvmDebugEnableMode.DISABLED));
    String javaHome = System.getenv("JAVA_HOME");
    appRunBuilder.environment().clear();
    appRunBuilder.environment().put("JAVA_HOME", javaHome);
    appRunBuilder.redirectErrorStream(true);
    Process appProcess = appRunBuilder.start();

    // Read the app's output.
    ProcessOutputConsumer appRunConsumer = new ProcessOutputConsumer(appProcess);
    Thread appRunConsumerThread = new Thread(appRunConsumer);
    appRunConsumerThread.start();

    // Wait for it to exit with an error.
    appProcess.waitFor(1, TimeUnit.MINUTES);
    appRunConsumerThread.join();

    // Verify that the application exited as expected.
    assertEquals(DataServerLauncherApp.EXIT_CODE_BAD_CONFIG, appProcess.exitValue());
  }

  /**
   * Verifies that {@link DataServerLauncherApp} starts up as expected when properly configured.
   *
   * @throws IOException (indicates a test error)
   */
  @Test
  public void normalUsage() throws IOException {
    ServerProcess serverProcess = null;
    try {
      // Launch the server.
      serverProcess =
          new ServerProcess(
              ServerTestUtils.getSampleWar(),
              new JvmDebugOptions(
                  JvmDebugEnableMode.DISABLED, JvmDebugAttachMode.WAIT_FOR_ATTACH, 8000));

      // Verify that a request works.
      try (CloseableHttpClient httpClient =
              ServerTestUtils.createHttpClient(Optional.of(ClientSslIdentity.TRUSTED));
          CloseableHttpResponse httpResponse =
              httpClient.execute(new HttpGet(serverProcess.getServerUri())); ) {
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());

        String httpResponseContent = EntityUtils.toString(httpResponse.getEntity());
        assertEquals("Johnny 5 is alive on HTTP!", httpResponseContent);
      }

      // Verify that the access log is working, as expected.
      try {
        TimeUnit.MILLISECONDS.sleep(
            100); // Needed in some configurations to resolve a race condition
      } catch (InterruptedException e) {
      }

      Path accessLogJson =
          ServerTestUtils.getLauncherProjectDirectory()
              .resolve("target")
              .resolve("server-work")
              .resolve("access.json");
      assertTrue(Files.isReadable(accessLogJson));
      assertTrue(Files.size(accessLogJson) > 0);
      assertTrue(
          Files.readString(accessLogJson)
              .contains(BfdMDC.HTTP_ACCESS_RESPONSE_OUTPUT_SIZE_IN_BYTES));
      assertTrue(
          Files.readString(accessLogJson).contains(BfdMDC.HTTP_ACCESS_RESPONSE_DURATION_PER_KB));
      // Stop the application.
      serverProcess.close();

      /*
       * Verify that the application exited as expected. Per POSIX (by way of
       * http://unix.stackexchange.com/a/99143), applications that exit due to a signal should
       * return an exit code that is 128 + the signal number.
       */
      assertEquals(128 + SIGTERM, (int) serverProcess.getResultCode().get());
      assertTrue(
          serverProcess
              .getProcessOutput()
              .contains(DataServerLauncherApp.LOG_MESSAGE_SHUTDOWN_HOOK_COMPLETE),
          "Application's housekeeping shutdown hook did not run: "
              + serverProcess.getProcessOutput());
    } finally {
      if (serverProcess != null) serverProcess.close();
    }
  }
}
