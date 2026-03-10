package com.example.tpms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;
import java.util.*;

@Service
public class GroqService {


    @Value("${groq.api.key}")
    private String apiKey;

    private final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    HttpClient httpClient = HttpClient.create()
            .resolver(DefaultAddressResolverGroup.INSTANCE);

    WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

    public String getStructuredData(String rawText) {

        String cleanText = rawText.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll(" +", " ");

        String prompt = "### ROLE\n" +
                "You are a highly Senior Technical Recruiter and Data Analystr. Your mission is to extract data with 100% accuracy. Mapping data to the wrong company or institution is a critical failure.\n\n" +
                "### DATA INTEGRITY RULES\n" +
                "1. **CHRONOLOGY FIRST**: Look at the dates carefully. 'Present' or the most recent date is the CURRENT job. \n" +
                "2. **STRICT BOUNDARIES**: When you see a new Company name, STOP adding points to the previous company. Data under '8Bit' must stay in '8Bit'. Data under 'Kugelblitz' must stay in 'Kugelblitz'.\n" +
                "3. **CONTACT INFO RECOVERY**: Look at the very top of the text for Name, Email, Phone, and LinkedIn. Do not return null if they exist.\n" +
                "4. **NO MIXING**: Do not swap highlights between roles. If you are unsure, do not hallucinate.\n\n" +
                "### JSON STRUCTURE (STRICTLY FOLLOW)\n" +
                "{\n" +
                "   \"profile summery \": \"string\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"email\": \"string\",\n" +
                "  \"phone\": \"string\",\n" +
                "  \"linkedin\": \"string\",\n" +
                "  \"location\": \"string\",\n" +
                "  \"experience\": [\n" +
                "    { \"company\": \"\", \"role\": \"\", \"duration\": \"\", \"highlights\": [], \"location\": \"\" }\n" +
                "  ],\n" +
                "  \"education\": [\n" +
                "    { \"institution\": \"\", \"degree\": \"\", \"completion_year\": \"\", \"score\": \"\" }\n" +
                "  ],\n" +
                "  \"projects\": [\n" +
                "    { \"title\": \"\", \"technologies\": [], \"details\": [] }\n" +
                "  ]\n" +
        "  \"skills\": { \"front_end\": [], \"back_end\": [], \"databases\": [], \"devops\": [] }\n" +
                "}\n\n" +
                "### RESUME TEXT FOR ANALYSIS\n" +
                "---START---\n" + cleanText + "\n---END---\n\n" +
                "Extract everything now. Ensure 'Present' job is the first item in the experience array.";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant");
        requestBody.put("temperature", 0.0);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);
        requestBody.put("response_format", Map.of("type", "json_object"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");
            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7, content.lastIndexOf("```"));
            }
            return content.trim();
        } catch (Exception e) {
            return "{\"error\": \"Failed to parse with Groq: " + e.getMessage() + "\"}";
        }
    }

    public Object processResumeDirectly(MultipartFile file) throws Exception {
            String extractedText;
            try (InputStream is = file.getInputStream();
                 PDDocument document = PDDocument.load(is)) {
                PDFTextStripper stripper = new PDFTextStripper();
                extractedText = stripper.getText(document);
            }
            return sendToGroq(extractedText);
    }

    private Object sendToGroq(String text) throws Exception { // Change return type to Object
        String cleanText = text.replaceAll("[\"\\r\\n\\t]", " ").trim();
        if (cleanText.length() > 15000) {
            cleanText = cleanText.substring(0, 15000);
        }

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "Extract resume data into JSON format with keys: name, email, phone, skills, experience, and education. Output ONLY the JSON."),
                        Map.of("role", "user", "content", "Parse this resume: " + cleanText)
                },
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.1
        );
        try {
            String response = webClient.post()
                    .uri("https://api.groq.com/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String contentString = root.path("choices").get(0).path("message").path("content").asText();
            return mapper.readValue(contentString, Object.class);

        } catch (Exception e) {
            System.err.println("Detailed Error: " + e.getMessage());
            throw e;

        }
    }

    }
