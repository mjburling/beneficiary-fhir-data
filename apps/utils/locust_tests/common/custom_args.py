"""Code in this file is related to defining and registering custom Locust arguments for testing"""

from locust.argument_parser import LocustArgumentParser
from locust.util.timespan import parse_timespan


def register_custom_args(parser: LocustArgumentParser):
    parser.add_argument(
        "--client-cert-path",
        type=str,
        required=True,
        help='Specifies path to client cert, ex: "<path/to/client/pem/file>" (Required)',
        dest="client_cert_path",
        env_var="LOCUST_BFD_CLIENT_CERT_PATH",
    )
    parser.add_argument(
        "--database-uri",
        type=str,
        required=True,
        help=(
            'Specfies database URI path, ex: "https://<nodeIp>:7443 or'
            ' https://<environment>.bfd.cms.gov" (Required)'
        ),
        dest="database_uri",
        env_var="LOCUST_BFD_DATABASE_URI",
    )
    parser.add_argument(
        "--spawned-runtime",
        type=parse_timespan,
        help=(
            "Specifies the test runtime limit that begins after all users have spawned when running"
            " tests with the custom UserInitAwareLoadShape load shape, which should be all of the"
            " tests in this repository. If unspecified, tests run indefinitely even after all users"
            ' have spawned. Specifying "0<s/h/m>" will stop the tests immediately once all users'
            " have spawned. Note that this is not the same option as --run-time, which handles the"
            " total runtime limit for the Locust run including non-test tasks and does not"
            " compensate for spawn rate."
        ),
        dest="spawned_runtime",
        env_var="LOCUST_USERS_SPAWNED_RUNTIME",
    )
    parser.add_argument(
        "--server-public-key",
        type=str,
        help='"<server public key>" (Optional, Default: "")',
        dest="server_public_key",
        env_var="LOCUST_BFD_SERVER_PUBLIC_KEY",
        default="",
    )
    parser.add_argument(
        "--table-sample-percent",
        type=float,
        help="<% of table to sample> (Optional, Default: 0.25)",
        dest="table_sample_percent",
        env_var="LOCUST_DATA_TABLE_SAMPLE_PERCENT",
        default=0.25,
    )
    parser.add_argument(
        "--stats-config",
        type=str,
        help=(
            '"<If set, stores stats in JSON to S3 or local file. Key-value list seperated by'
            ' semi-colons. See README.>" (Optional)'
        ),
        dest="stats_config",
        env_var="LOCUST_STATS_CONFIG",
    )