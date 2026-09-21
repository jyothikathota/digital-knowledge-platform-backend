package com.dkp.content;

import com.dkp.content.entity.Content;
import com.dkp.content.repository.ContentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentDataInitializer implements CommandLineRunner {

    private final ContentRepository contentRepository;

    public ContentDataInitializer(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public void run(String... args) {
        if (contentRepository.count() > 0) {
            return;
        }

        contentRepository.saveAll(List.of(
                new Content(
                        null,
                        "Service-Oriented Architecture",
                        "Thomas Erl",
                        "BOOK",
                        "SOA",
                        "An introduction to service-oriented architecture principles and design patterns.",
                        "https://example.com/resources/soa"
                ),
                new Content(
                        null,
                        "Distributed Consensus in Practice",
                        "Leslie Lamport",
                        "ARTICLE",
                        "DISTRIBUTED SYSTEMS",
                        "A paper exploring consensus, agreement, and fault tolerance in distributed systems.",
                        "https://example.com/resources/distributed-consensus"
                ),
                new Content(
                        null,
                        "JWT and OAuth 2.0 for Secure APIs",
                        "Vladimir Dzhuvinov",
                        "JOURNAL",
                        "SECURITY",
                        "A journal resource covering token-based authentication and authorization for modern APIs.",
                        "https://example.com/resources/jwt-oauth"
                ),
                new Content(
                        null,
                        "Building AI Agents",
                        "Harrison Chase",
                        "BOOK",
                        "ARTIFICIAL INTELLIGENCE",
                        "A practical introduction to designing and orchestrating intelligent AI agent systems.",
                        "https://example.com/resources/ai-agents"
                )
        ));
    }
}