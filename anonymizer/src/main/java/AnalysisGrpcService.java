import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.AnalysisServiceProto;

public class AnalysisGrpcService extends proto.AnalysisServiceGrpc.AnalysisServiceImplBase {

    private static final Logger logger = LogManager.getLogger(AnalysisGrpcService.class.getName());
    private final AnonymizationClient anonymizationClient;


    public AnalysisGrpcService(AnonymizationClient anonymizationClient) {
        this.anonymizationClient = anonymizationClient;
    }


    @Override
    public void analyze(AnalysisServiceProto.AnalysisRequest request, StreamObserver<AnalysisServiceProto.AnalysisReply> responseObserver) {
        logger.debug("Incoming analyze GRPC request.");
        try {
            AnalysisServiceProto.AnalysisReply reply = AnalysisServiceProto.AnalysisReply
                    .newBuilder()
                    .setText(this.anonymizationClient.anonymize(request.getText()))
                    .build();
            responseObserver.onNext(reply);
            logger.debug("Successfully processed analyze GRPC request.");

        } catch (Exception e) {
            logger.error("Failed to anonymize text");
            e.printStackTrace();
        }
        responseObserver.onCompleted();
    }


}
