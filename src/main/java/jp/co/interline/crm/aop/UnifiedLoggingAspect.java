//package jp.co.interline.crm.aop;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//@Aspect
//@Component
//public class UnifiedLoggingAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(UnifiedLoggingAspect.class);
//    private static final RestTemplate restTemplate = createLoggingRestTemplate();
//
//    // API 번호 매핑을 위한 사전 설정 (URL에 따른 API 번호)
//    private static final Map<String, Integer> apiNumberMap = new HashMap<>() {{
//        put("http://example.com/api1", 1111);
//        put("http://example.com/api2", 2222);
//        put("http://example.com/api3", 3333);
//        put("http://example.com/api4", 4444);
//        put("http://example.com/api5", 5555);
//        put("http://example.com/api6", 6666);
//    }};
//
//    // 1. 컨트롤러 요청 로그
//    @Pointcut("execution(* com.example.controller..*(..))")
//    private void controllerMethods() {}
//
//    @Before("controllerMethods()")
//    public void logControllerRequest(JoinPoint joinPoint) {
//        logger.info("[Controller Request] Method: {} - Args: {}", joinPoint.getSignature(), joinPoint.getArgs());
//    }
//
//    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
//    public void logControllerResponse(JoinPoint joinPoint, Object result) {
//        logger.info("[Controller Response] Method: {} - Result: {}", joinPoint.getSignature(), result);
//    }
//
//    // 2. RestTemplate 요청 및 응답 로깅을 위한 설정
//    private static RestTemplate createLoggingRestTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getInterceptors().add((request, body, execution) -> {
//            // URL에 따른 API 번호 설정
//            Integer apiNumber = apiNumberMap.getOrDefault(request.getURI().toString(), 0);
//
//            // RestTemplate 요청 로그 (API 번호 포함)
//            logger.info("[API Request] API Number: {} - URI: {}, Method: {}, Body: {}",
//                    apiNumber, request.getURI(), request.getMethod(), new String(body, StandardCharsets.UTF_8));
//
//            ClientHttpResponse response = execution.execute(request, body);
//
//            // RestTemplate 응답 로그
//            StringBuilder responseBody = new StringBuilder();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    responseBody.append(line);
//                }
//            }
//            logger.info("[API Response] API Number: {} - Status: {}, Body: {}",
//                    apiNumber, response.getStatusCode(), responseBody.toString());
//
//            return response;
//        });
//        return restTemplate;
//    }
//
//    // 외부에서 RestTemplate을 사용할 수 있도록 제공하는 메서드
//    public static RestTemplate getRestTemplate() {
//        return restTemplate;
//    }
//}
