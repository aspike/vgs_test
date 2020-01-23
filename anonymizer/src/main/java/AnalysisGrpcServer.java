import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnalysisGrpcServer {

    private static final Logger logger = LogManager.getLogger(AnalysisGrpcServer.class.getName());
    private final Server server;
    private final int port;


    public AnalysisGrpcServer(int port, AnonymizationClient anonymizationClient) {
        this.port = port;
        var analysisGrpcService = new AnalysisGrpcService(anonymizationClient);

        this.server = ServerBuilder
                .forPort(port)
                .addService(analysisGrpcService)
                .build();
    }

    public void start() throws Exception {
        server.start();
        logger.info("Server started, listening on {}", this.port);
        server.awaitTermination();
    }


}
