import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class.getName());
    public static final String GRPC_PORT_ENV_VAR = "GRPC_PORT";
    public static final String GRPC_SERVER_ENV_VAR = "GRPC_SERVER_HOST";

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.INFO);
        logger.info("VGS Anonymizer Start");

        if (args.length > 0 && args[0].equals("test")) {
            runTestClient();
        } else {
            runServer();
        }

        logger.info("VGS Anonymizer Exiting");
    }

    private static void runTestClient() {
        int serverPort = 0;
        String serverHost = null;
        try {
            serverPort = Integer.parseInt(System.getenv(GRPC_PORT_ENV_VAR));
            serverHost = System.getenv(GRPC_SERVER_ENV_VAR);
        } catch (NumberFormatException e) {
            logger.fatal("Failed to parse GRPC port. " +
                    "Please provide valid grpc server port as {} env var", GRPC_PORT_ENV_VAR);

            System.exit(1);
        }

        String testText = TestUtil.loadTestFixture();
        String analysedText = new AnalysisGrpcClient(serverHost, serverPort).analyze(testText);
        logger.info("Input: {}", testText);
        logger.info("Output: {}", analysedText);
    }

    public static void runServer() {

        int grpcPort = 0;
        try {
            grpcPort = Integer.parseInt(System.getenv(GRPC_PORT_ENV_VAR));
        } catch (NumberFormatException e) {
            logger.fatal("Failed to parse GRPC port. " +
                    "Please provide valid grpc port as {} env var", GRPC_PORT_ENV_VAR, e);
            System.exit(1);
        }

        try {
            AnonymizationClient anonymizationHttpClient = new AnonymizationClient();
            anonymizationHttpClient.init();
            new AnalysisGrpcServer(grpcPort, anonymizationHttpClient).start();
        } catch (Exception e) {
            logger.fatal("Failed to start GRPC server", e);
        }
    }
}
