import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public class AnonymizationClient {
    private static final Logger logger = LogManager.getLogger(AnonymizationClient.class.getName());
    private final String ANONYMIZE_TEMPLATE_NAME = "anonymize_base";
    private final String ANALYZE_TEMPLATE_NAME = "analyze_base";

    private final HttpClient httpClient;
    private String presidioAddress;
    private final String anonymizeEndpoint;
    private final String anonymizeTemplateEndpoint;
    private final String analyzeTemplateEndpoint;

    public AnonymizationClient() {
        httpClient = HttpClient.newHttpClient();
        presidioAddress = System.getenv("PRESIDIO_ADDRESS");

        anonymizeEndpoint = "/api/v1/projects/vgs/anonymize";
        anonymizeTemplateEndpoint = "/api/v1/templates/vgs/anonymize/" + ANONYMIZE_TEMPLATE_NAME;
        analyzeTemplateEndpoint = "/api/v1/templates/vgs/analyze/" + ANALYZE_TEMPLATE_NAME;

        logger.info("Presidio address: {}", presidioAddress);
    }

    public void init() throws Exception {
        uploadAnalyzeTemplate("analyze_template.json");
        uploadAnonymizeTemplate("anonymize_template.json");
    }


    public String anonymize(String text) throws Exception {
        //language=JSON
        String json = String.format(
                "{\"text\": \"%s\", \"AnalyzeTemplateId\": \"%s\", \"AnonymizeTemplateId\": \"%s\"}",
                text,
                ANALYZE_TEMPLATE_NAME,
                ANONYMIZE_TEMPLATE_NAME);

        URI uri = URI.create(presidioAddress + anonymizeEndpoint);

        logger.debug("Will anonymize text {}\nuri:{}\n", json, uri);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("Status code: " + response.statusCode());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to anonymize text");
        }

        return parseResponse(response.body());
    }

    private static final String parseResponse(String httpBody) {
        JSONObject obj = new JSONObject(httpBody);
        return obj.getString("text");
    }

    private void uploadAnalyzeTemplate(String templateFilename) throws Exception {
        String templateJson = loadTemplate(templateFilename);
        URI uri = URI.create(presidioAddress + analyzeTemplateEndpoint);
        uploadTemplate(templateJson, uri);
    }


    private void uploadAnonymizeTemplate(String templateFilename) throws Exception {
        String templateJson = loadTemplate(templateFilename);
        URI uri = URI.create(presidioAddress + anonymizeTemplateEndpoint);
        uploadTemplate(templateJson, uri);
    }

    private void uploadTemplate(String templateJson, URI uri) throws Exception {
        logger.debug("Will upload template to: {}\n{}", uri, templateJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(templateJson))
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.error("Failed to upload template {}", templateJson);
            throw new Exception("Failed to upload template");
        }
    }

    private String loadTemplate(String fileName) {
        logger.debug("Loading template {}", fileName);
        InputStream in = getClass().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.lines().collect(Collectors.joining());
    }
}
