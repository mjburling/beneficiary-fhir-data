<!-- AUTOGENERATED FILE: Do not modify -->
Table of Contents
=================

* [How to Detach BFD Server Instances for Development or Debugging](./how-to-detach-bfd-server-instances-for-development-or-debugging.md#how-to-detach-bfd-server-instances-for-development-or-debugging)
   * [Scale Out to Avoid Potential Performance Degradation](./how-to-detach-bfd-server-instances-for-development-or-debugging.md#scale-out-to-avoid-potential-performance-degradation)
   * [Detach the Instance](./how-to-detach-bfd-server-instances-for-development-or-debugging.md#detach-the-instance)
   * [Scale In After Detaching an Instance](./how-to-detach-bfd-server-instances-for-development-or-debugging.md#scale-in-after-detaching-an-instance)
   * [Patching Regularly When Instances Endure Beyond ~1 Day](./how-to-detach-bfd-server-instances-for-development-or-debugging.md#patching-regularly-when-instances-endure-beyond-1-day)
   * [Terminating the Instance](./how-to-detach-bfd-server-instances-for-development-or-debugging.md#terminating-the-instance)

* [How to Run the Regression and Load Test Suites](./how-to-run-load-and-regression-locust-tests.md#how-to-run-the-regression-and-load-test-suites)
   * [Glossary](./how-to-run-load-and-regression-locust-tests.md#glossary)
   * [FAQ](./how-to-run-load-and-regression-locust-tests.md#faq)
      * [I specified --host with a valid URL like so example.com, but my tests aren't running. What am I doing wrong?](./how-to-run-load-and-regression-locust-tests.md#i-specified---host-with-a-valid-url-like-so-examplecom-but-my-tests-arent-running-what-am-i-doing-wrong)
   * [Prerequisites](./how-to-run-load-and-regression-locust-tests.md#prerequisites)
   * [Instructions](./how-to-run-load-and-regression-locust-tests.md#instructions)
      * [How to Run the Regression Suite Locally Against a Local BFD Server](./how-to-run-load-and-regression-locust-tests.md#how-to-run-the-regression-suite-locally-against-a-local-bfd-server)
      * [How to Run the Regression Suite Locally Against any BFD Server SDLC Environment](./how-to-run-load-and-regression-locust-tests.md#how-to-run-the-regression-suite-locally-against-any-bfd-server-sdlc-environment)
      * [How to Run the Regression Suite On a Detached Instance Against any BFD Server SDLC Environment](./how-to-run-load-and-regression-locust-tests.md#how-to-run-the-regression-suite-on-a-detached-instance-against-any-bfd-server-sdlc-environment)
      * [How to Run a Scaling Load Test Using the bfd-run-server-load Jenkins Job](./how-to-run-load-and-regression-locust-tests.md#how-to-run-a-scaling-load-test-using-the-bfd-run-server-load-jenkins-job)
      * [How to Run a Static Load Test Using the bfd-run-server-load Jenkins Job](./how-to-run-load-and-regression-locust-tests.md#how-to-run-a-static-load-test-using-the-bfd-run-server-load-jenkins-job)

* [How to Mark SNYK Findings as Ignored](./how-to-mark-snyk-findings-ignored.md#how-to-mark-snyk-findings-as-ignored)

* [How to Run Synthea Automation](./how-to-run-synthea-automation.md#how-to-run-synthea-automation)
   * [Prod Load Additional Steps](./how-to-run-synthea-automation.md#prod-load-additional-steps)
   * [Troubleshooting (High Level)](./how-to-run-synthea-automation.md#troubleshooting-high-level)

* [How to Load Cloudwatch Historical Data](./how-to-load-cloudwatch-historical-data.md#how-to-load-cloudwatch-historical-data)
   * [Export the data from Cloudwatch.](./how-to-load-cloudwatch-historical-data.md#export-the-data-from-cloudwatch)
   * [Create/Update the Glue export table.](./how-to-load-cloudwatch-historical-data.md#createupdate-the-glue-export-table)
   * [Determine the column list for the staging table.](./how-to-load-cloudwatch-historical-data.md#determine-the-column-list-for-the-staging-table)
   * [Create a non-partitioned Parquet Glue table to serve as the staging table.](./how-to-load-cloudwatch-historical-data.md#create-a-non-partitioned-parquet-glue-table-to-serve-as-the-staging-table)
   * [Load the staging table from the export table.](./how-to-load-cloudwatch-historical-data.md#load-the-staging-table-from-the-export-table)
   * [Load the target table from the staging table.](./how-to-load-cloudwatch-historical-data.md#load-the-target-table-from-the-staging-table)
   * [Verify the load.](./how-to-load-cloudwatch-historical-data.md#verify-the-load)

* [How to Resolve SNYK-bot PRs](./how-to-resolve-snyk-bot-prs.md#how-to-resolve-snyk-bot-prs)

* [How to Investigate Firehose Ingestion Processing Failures](./how-to-investigate-firehose-ingestion-processing-failures.md#how-to-investigate-firehose-ingestion-processing-failures)
   * [Glossary](./how-to-investigate-firehose-ingestion-processing-failures.md#glossary)
   * [FAQ](./how-to-investigate-firehose-ingestion-processing-failures.md#faq)
      * [How would "processing failures" occur?](./how-to-investigate-firehose-ingestion-processing-failures.md#how-would-processing-failures-occur)
      * [Has there ever been processing failures?](./how-to-investigate-firehose-ingestion-processing-failures.md#has-there-ever-been-processing-failures)
   * [Prerequisites](./how-to-investigate-firehose-ingestion-processing-failures.md#prerequisites)
   * [Instructions](./how-to-investigate-firehose-ingestion-processing-failures.md#instructions)

* [Recreating server-regression QuickSight Dashboards](./how-to-create-server-regression-dashboards.md#recreating-server-regression-quicksight-dashboards)
   * [Glossary](./how-to-create-server-regression-dashboards.md#glossary)
   * [FAQ](./how-to-create-server-regression-dashboards.md#faq)
      * [Will the hash in each of the Dataset SQL queries below ever change? What is it used for?](./how-to-create-server-regression-dashboards.md#will-the-hash-in-each-of-the-dataset-sql-queries-below-ever-change-what-is-it-used-for)
   * [Instructions](./how-to-create-server-regression-dashboards.md#instructions)

* [How to Create BFD Insights QuickSight Dashboards](./how-to-create-bfd-insights-quicksight.md#how-to-create-bfd-insights-quicksight-dashboards)

* [How to Recover from migrator Failures](./how-to-recover-from-migrator-failures.md#how-to-recover-from-migrator-failures)
   * [Failures](./how-to-recover-from-migrator-failures.md#failures)
      * [Undeployable State](./how-to-recover-from-migrator-failures.md#undeployable-state)
         * [Performance Steps](./how-to-recover-from-migrator-failures.md#performance-steps)
      * [Invalid App Configuration (1)](./how-to-recover-from-migrator-failures.md#invalid-app-configuration-1)
         * [Performance Steps](./how-to-recover-from-migrator-failures.md#performance-steps-1)
      * [Migration Failed (2)](./how-to-recover-from-migrator-failures.md#migration-failed-2)
         * [Performance Steps](./how-to-recover-from-migrator-failures.md#performance-steps-2)
      * [Validation Failed (3)](./how-to-recover-from-migrator-failures.md#validation-failed-3)
         * [Performance Steps](./how-to-recover-from-migrator-failures.md#performance-steps-3)
   * [References](./how-to-recover-from-migrator-failures.md#references)

* [How to Resolve SNYK Report Findings](./how-to-resolve-snyk-report-findings.md#how-to-resolve-snyk-report-findings)

* [How to Re-run a Failed BFD Pipeline Load](./how-to-rerun-failed-pipeline-load.md#how-to-re-run-a-failed-bfd-pipeline-load)





Created by [gh-md-toc](https://github.com/ekalinin/github-markdown-toc)