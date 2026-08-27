# Requirements: Design a Distributed Web Crawler

## 📋 Functional Requirements (FR)
1. **URL Crawling**: Starting from a seed list of URLs, download web pages, extract text content, and parse all outbound links (`<a href="...">`).
2. **URL Frontier**: Maintain an ordered queue of discovered URLs to visit next.
3. **Deduplication**: Prevent downloading the same URL twice and detect duplicate page content.
4. **Politeness & robots.txt**: Respect domain crawl delays, rate limits, and `robots.txt` exclusion rules.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Massive Scalability**: Crawl **1 Billion web pages per month** ($\approx 400\text{ pages/sec}$).
2. **Robustness & Fault Tolerance**: Handle broken HTML, slow host timeouts, redirect loops, and crawler traps (infinite calendar URL loops).
3. **Politeness Policy**: Do not overwhelm any single target web server with concurrent requests.
4. **Extensibility**: Easily plug in new parsers (e.g. PDF parser, image extractor, AI text vectorizer).
