import os
import sys
import getopt
import common.config as config
import common.test_setup as setup
from locust.main import main

'''
Runs a specified test via the input args.
'''
def run_with_params(argv):

    testFile = ''

    ## container to hold config data
    class configData: pass

    ## Optional Params with defaults
    configData.serverPublicKey = ''
    configData.testRunTime = "1m"
    configData.testNumTotalClients = "100"
    configData.testCreatedClientsPerSecond = "5"

    helpString = ('runtests.py \n--homePath="<path/to/home/directory>" (Required) '
     '\n--clientCertPath="<path/to/client/pem/file>" (Required)'
     '\n--databaseHost="<database-aws-node>.rds.amazonaws.com" (Required)'
     '\n--databaseUsername="keybase-db-username-for-environment" (Required)'
     '\n--databasePassword="keybase-db-password-for-environment" (Required)'
     '\n--testHost="https://<nodeIp>:7443 or https://<environment>.bfd.cms.gov" (Required)'
     '\n--testFile="/<v1/v2>/test_to_run.py" (Required)'
     '\n--serverPublicKey="<server public key>" (Optional, Default: "")'
     '\n--testRunTime="<Test run time, ex. 30s, 1m, 2d 1h>" (Optional, Default 1m)'
     '\n--maxClients="<Max number of clients to create at once, int>" (Optional, Default 100)'
     '\n--clientsPerSecond="<Clients to create per second until maxClients is reached, int>" (Optional, Default 5)')

    try:
        opts, args = getopt.getopt(argv,"h",["homePath=", "clientCertPath=", "databaseHost=", "databaseUsername=",
        "databasePassword=", "testHost=", "serverPublicKey=", "testRunTime=", "maxClients=", "clientsPerSecond=",
        "testFile="])
    except getopt.GetoptError:
        print(helpString)
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print(helpString)
            sys.exit()
        elif opt == "--homePath":
            configData.homePath = arg
        elif opt == "--clientCertPath":
            configData.clientCertPath = arg
        elif opt == "--databaseHost":
            configData.dbHost = arg
        elif opt == "--databaseUsername":
            configData.dbUsername = arg
        elif opt == "--databasePassword":
            configData.dbPassword = arg
        elif opt == "--testHost":
            configData.testHost = arg
        elif opt == "--serverPublicKey":
            configData.serverPublicKey = arg
        elif opt == "--testRunTime":
            configData.testRunTime = arg
        elif opt == "--maxClients":
            configData.testNumTotalClients = arg
        elif opt == "--clientsPerSecond":
            configData.testCreatedClientsPerSecond = arg
        elif opt == "--testFile":
            testFile = arg
        else:
            print(helpString)
            sys.exit()

    ## Check if all required params are set
    if not all([configData.homePath, configData.clientCertPath, configData.dbHost, configData.dbUsername, configData.dbPassword, configData.testHost, testFile]):
        print("Missing required arg (See -h for help on params)")
        sys.exit(2)

    ## write out config file
    config.save(configData)
    setup.set_locust_test_name(testFile)

    # strip off extra command line params for locust, or else it tries to parse them
    sys.argv = sys.argv[:1]
    # call locust to run test
    main()

## Runs the test via run args when this file is run
if __name__ == "__main__":
    run_with_params(sys.argv[1:])