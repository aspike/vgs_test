import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.AnalysisServiceGrpc;
import proto.AnalysisServiceProto;

public class AnalysisGrpcClient {

    private static final Logger logger = LogManager.getLogger(AnalysisGrpcClient.class.getName());
    private AnalysisServiceGrpc.AnalysisServiceBlockingStub blockingStub;

    public AnalysisGrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public AnalysisGrpcClient(ManagedChannelBuilder<?> channelBuilder) {
        var channel = channelBuilder.build();
        blockingStub = AnalysisServiceGrpc.newBlockingStub(channel);
    }

    public String analyze(String text) {
        logger.debug("Sending analyze request");
        AnalysisServiceProto.AnalysisRequest request = AnalysisServiceProto.AnalysisRequest.newBuilder()
                .setText(text)
                .build();
        String response = blockingStub.analyze(request).getText();
        logger.debug("Analyze response: {}", response);
        return response;
    }

}
