import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Best Practices in LLD: API Design, Versioning, and Security
 * 
 * Demonstrates:
 * 1. DTO (Data Transfer Object) Contracts (Decoupling Database Entities)
 * 2. API Versioning (URI-based V1 vs V2 Evolution)
 * 3. Pagination, Filtering, and Sorting Envelope Pattern
 * 4. RFC 7807 Standardized Error Response Schema
 * 5. Token-Bucket Rate Limiter (HTTP 429 Too Many Requests)
 */

public class APIDesignAndSecurityExample {

    // =========================================================================
    // 1. DATABASE ENTITY (Internal Model - NEVER exposed directly to clients)
    // =========================================================================

    static class CourseEntity {
        private final String id;
        private final String title;
        private final String instructorSecretEmail; // SENSITIVE: Must not leak in API!
        private final double price;
        private final int totalDurationMinutes;
        private final String status;

        public CourseEntity(String id, String title, String instructorSecretEmail, double price, int totalDurationMinutes, String status) {
            this.id = id;
            this.title = title;
            this.instructorSecretEmail = instructorSecretEmail;
            this.price = price;
            this.totalDurationMinutes = totalDurationMinutes;
            this.status = status;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public double getPrice() { return price; }
        public int getTotalDurationMinutes() { return totalDurationMinutes; }
        public String getStatus() { return status; }
    }

    // =========================================================================
    // 2. DTO CONTRACTS (Data Transfer Objects)
    // =========================================================================

    // Version 1 DTO: Basic Course Metadata
    record CourseResponseDTOV1(String id, String title, double price, String status) {}

    // Version 2 DTO: Enhanced Metadata (Includes duration & hours breakdown)
    record CourseResponseDTOV2(String id, String title, double price, String status, String formattedDuration) {}

    // Generic Pagination Envelope
    record PagedResponse<T>(List<T> data, int page, int limit, long totalItems, int totalPages) {}

    // Standardized RFC 7807 API Error Envelope
    record ApiErrorResponse(String timestamp, int status, String error, String message, String path) {}

    // Generic API HTTP Response Envelope
    record HttpResponse<T>(int statusCode, Map<String, String> headers, T body) {}

    // =========================================================================
    // 3. SECURITY & RATE LIMITING (Token-Bucket Algorithm)
    // =========================================================================

    static class TokenBucketRateLimiter {
        private final int maxTokens;
        private final long refillIntervalMs;
        private final Map<String, Bucket> clientBuckets = new ConcurrentHashMap<>();

        public TokenBucketRateLimiter(int maxTokens, long refillIntervalMs) {
            this.maxTokens = maxTokens;
            this.refillIntervalMs = refillIntervalMs;
        }

        static class Bucket {
            int tokens;
            long lastRefillTimestamp;

            Bucket(int tokens) {
                this.tokens = tokens;
                this.lastRefillTimestamp = System.currentTimeMillis();
            }
        }

        public synchronized boolean allowRequest(String clientId) {
            long now = System.currentTimeMillis();
            Bucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new Bucket(maxTokens));

            // Refill tokens based on elapsed time
            if (now - bucket.lastRefillTimestamp > refillIntervalMs) {
                bucket.tokens = maxTokens;
                bucket.lastRefillTimestamp = now;
            }

            if (bucket.tokens > 0) {
                bucket.tokens--;
                return true;
            }
            return false; // Rate limit exceeded!
        }
    }

    // =========================================================================
    // 4. VERSIONED API CONTROLLERS (V1 & V2)
    // =========================================================================

    static class CourseApiController {
        private final List<CourseEntity> database = new ArrayList<>();
        private final TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(3, 5000); // 3 reqs per 5 sec

        public CourseApiController() {
            // Seed internal database
            database.add(new CourseEntity("C-101", "Striver's A2Z DSA Sheet", "striver@internal.org", 0.0, 7200, "PUBLISHED"));
            database.add(new CourseEntity("C-102", "Core Java Multithreading", "team@internal.org", 1499.0, 1800, "PUBLISHED"));
            database.add(new CourseEntity("C-103", "LLD System Design Masterclass", "sourav@internal.org", 2999.0, 3600, "PUBLISHED"));
            database.add(new CourseEntity("C-104", "HLD Distributed Architecture", "raj@internal.org", 4999.0, 4800, "DRAFT"));
        }

        // --- GET /api/v1/courses (Version 1 Endpoint) ---
        public HttpResponse<?> getCoursesV1(String clientIp, int page, int limit) {
            // 1. Rate Limiting Check
            if (!rateLimiter.allowRequest(clientIp)) {
                ApiErrorResponse error = new ApiErrorResponse(
                    Instant.now().toString(), 429, "Too Many Requests", 
                    "API rate limit exceeded (Max 3 reqs/5s). Please retry after 5 seconds.", "/api/v1/courses"
                );
                return new HttpResponse<>(429, Map.of("Retry-After", "5"), error);
            }

            // 2. Pagination Calculation
            int safeLimit = Math.max(1, Math.min(limit, 10));
            int safePage = Math.max(1, page);
            int startIndex = (safePage - 1) * safeLimit;

            if (startIndex >= database.size()) {
                PagedResponse<CourseResponseDTOV1> empty = new PagedResponse<>(Collections.emptyList(), safePage, safeLimit, database.size(), (int) Math.ceil((double) database.size() / safeLimit));
                return new HttpResponse<>(200, Collections.emptyMap(), empty);
            }

            int endIndex = Math.min(startIndex + safeLimit, database.size());
            List<CourseEntity> subList = database.subList(startIndex, endIndex);

            // 3. Map Entity -> V1 DTO
            List<CourseResponseDTOV1> dtos = new ArrayList<>();
            for (CourseEntity e : subList) {
                dtos.add(new CourseResponseDTOV1(e.getId(), e.getTitle(), e.getPrice(), e.getStatus()));
            }

            int totalPages = (int) Math.ceil((double) database.size() / safeLimit);
            PagedResponse<CourseResponseDTOV1> paged = new PagedResponse<>(dtos, safePage, safeLimit, database.size(), totalPages);
            return new HttpResponse<>(200, Collections.emptyMap(), paged);
        }

        // --- GET /api/v2/courses (Version 2 Enhanced Endpoint) ---
        public HttpResponse<?> getCoursesV2(String clientIp, int page, int limit) {
            if (!rateLimiter.allowRequest(clientIp)) {
                ApiErrorResponse error = new ApiErrorResponse(
                    Instant.now().toString(), 429, "Too Many Requests", 
                    "API rate limit exceeded. Retry after 5s.", "/api/v2/courses"
                );
                return new HttpResponse<>(429, Map.of("Retry-After", "5"), error);
            }

            int safeLimit = Math.max(1, Math.min(limit, 10));
            int safePage = Math.max(1, page);
            int startIndex = (safePage - 1) * safeLimit;
            int endIndex = Math.min(startIndex + safeLimit, database.size());
            List<CourseEntity> subList = database.subList(startIndex, endIndex);

            // Map Entity -> V2 DTO (Enhanced with Duration calculations)
            List<CourseResponseDTOV2> dtos = new ArrayList<>();
            for (CourseEntity e : subList) {
                String durationFormatted = (e.getTotalDurationMinutes() / 60) + " hrs " + (e.getTotalDurationMinutes() % 60) + " mins";
                dtos.add(new CourseResponseDTOV2(e.getId(), e.getTitle(), e.getPrice(), e.getStatus(), durationFormatted));
            }

            int totalPages = (int) Math.ceil((double) database.size() / safeLimit);
            PagedResponse<CourseResponseDTOV2> paged = new PagedResponse<>(dtos, safePage, safeLimit, database.size(), totalPages);
            return new HttpResponse<>(200, Map.of("X-API-Version", "2.0"), paged);
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 🌐 Enterprise API Design, Versioning, and Security ===");
        CourseApiController apiController = new CourseApiController();
        String clientIp = "192.168.1.100";

        // --- Demo 1: Version 1 Paginated GET Request ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ DEMO 1: GET /api/v1/courses?page=1&limit=2 (Paginated DTOs)");
        System.out.println("-----------------------------------------------------------");
        HttpResponse<?> responseV1 = apiController.getCoursesV1(clientIp, 1, 2);
        System.out.println("HTTP Status: " + responseV1.statusCode());
        System.out.println("Response Body:\n" + responseV1.body());

        // --- Demo 2: Version 2 Enhanced Contract ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ DEMO 2: GET /api/v2/courses?page=1&limit=2 (Enhanced V2 DTOs)");
        System.out.println("-----------------------------------------------------------");
        HttpResponse<?> responseV2 = apiController.getCoursesV2(clientIp, 1, 2);
        System.out.println("HTTP Status: " + responseV2.statusCode());
        System.out.println("Headers: " + responseV2.headers());
        System.out.println("Response Body:\n" + responseV2.body());

        // --- Demo 3: Rate Limiting & Throttling (HTTP 429) ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ DEMO 3: Rate Limiter Throttling (Bursting Quota)");
        System.out.println("-----------------------------------------------------------");
        // Request #3 (Token 1 left -> Allowed)
        System.out.println("📡 Request #3 from " + clientIp + " -> Status: " + apiController.getCoursesV1(clientIp, 1, 1).statusCode());
        
        // Request #4 (Quota Exhausted -> HTTP 429 Too Many Requests!)
        HttpResponse<?> throttledResponse = apiController.getCoursesV1(clientIp, 1, 1);
        System.out.println("📡 Request #4 from " + clientIp + " -> Status: " + throttledResponse.statusCode());
        System.out.println("Headers: " + throttledResponse.headers());
        System.out.println("Error Payload:\n" + throttledResponse.body());

        // --- Demo 4: Rate Limiter Token Refill Recovery ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ DEMO 4: Token Bucket Refill After 5s Window");
        System.out.println("-----------------------------------------------------------");
        System.out.println("⏳ Waiting 5.1s for Token Bucket refill...");
        Thread.sleep(5100);

        HttpResponse<?> recoveredResponse = apiController.getCoursesV1(clientIp, 1, 1);
        System.out.println("📡 Request #5 after cooldown -> Status: " + recoveredResponse.statusCode() + " (Successfully Refilled!)");

        System.out.println("\n===========================================================");
        System.out.println("🎯 API Design, Versioning, DTOs, and Rate Limiting Verified!");
        System.out.println("===========================================================");
    }
}
