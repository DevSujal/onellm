# OneLLM

**Unified Java SDK for calling any LLM API with a single interface.**

[![Java](https://img.shields.io/badge/Java-11%2B-orange)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-Central-blue)](https://mvnrepository.com/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

OneLLM is a lightweight Java library that provides a unified interface to 10+ LLM providers. Write your code once and switch between providers by just changing the model name.

## Features

- 🔌 **10 Providers**: OpenAI, Anthropic, Google, Azure, Groq, Cerebras, Ollama, OpenRouter, xAI, Copilot
- 🚀 **Simple API**: One interface for all providers
- ⚡ **Streaming Support**: Real-time token streaming for all providers
- 🔄 **Async Support**: `CompletableFuture` for non-blocking calls
- 🎯 **Auto-Routing**: Automatically routes to the correct provider based on model name
- ♻️ **Retry Logic**: Built-in retry with exponential backoff
- 🪶 **Lightweight**: Minimal dependencies (Gson, HttpClient5, SLF4J)

## Installation

### Maven

```xml
<dependency>
    <groupId>io.onellm</groupId>
    <artifactId>onellm</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.onellm:onellm:1.0.0'
```

## Quick Start

```java
import io.onellm.OneLLM;
import io.onellm.core.LLMRequest;
import io.onellm.core.LLMResponse;

// Build with your API keys
OneLLM llm = OneLLM.builder()
    .openai(System.getenv("OPENAI_API_KEY"))
    .anthropic(System.getenv("ANTHROPIC_API_KEY"))
    .google(System.getenv("GOOGLE_API_KEY"))
    .groq(System.getenv("GROQ_API_KEY"))
    .cerebras(System.getenv("CEREBRAS_API_KEY"))
    .build();

// Call any model - auto-routes to correct provider
LLMResponse response = llm.complete(
    LLMRequest.builder()
        .model("gpt-4")  // Routes to OpenAI
        .user("What is the capital of France?")
        .build()
);

System.out.println(response.getContent());
```

## Supported Providers & Models

| Provider | Model Prefixes | Example Models |
|----------|---------------|----------------|
| **OpenAI** | `gpt-4`, `gpt-3.5`, `o1`, `o3` | `gpt-4`, `gpt-4-turbo`, `gpt-4o` |
| **Anthropic** | `claude-3`, `claude-4` | `claude-3-opus`, `claude-3.5-sonnet` |
| **Google** | `gemini` | `gemini-pro`, `gemini-2.0-flash` |
| **Azure** | `azure/` | `azure/gpt-4` (requires deployment) |
| **Groq** | `llama`, `mixtral`, `gemma` | `llama-3.1-70b`, `mixtral-8x7b` |
| **Cerebras** | `cerebras/` | `cerebras/llama3.1-70b` |
| **Ollama** | `ollama/`, `local/` | `ollama/llama2`, `local/mistral` |
| **OpenRouter** | Any org/model format | `openai/gpt-4`, `anthropic/claude-3-opus` |
| **xAI** | `grok` | `grok-2`, `grok-beta` |
| **Copilot** | `copilot`, `github/` | `copilot/gpt-4` |

## Examples

### Streaming Response

```java
llm.streamComplete(
    LLMRequest.builder()
        .model("claude-3-opus")
        .user("Write a short poem about code")
        .build(),
    new StreamHandler() {
        @Override
        public void onChunk(String chunk) {
            System.out.print(chunk);  // Print as tokens arrive
        }
        
        @Override
        public void onComplete(LLMResponse response) {
            System.out.println("\n\nDone! Latency: " + response.getLatencyMs() + "ms");
        }
        
        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }
);
```

### Async Calls

```java
CompletableFuture<LLMResponse> future = llm.completeAsync(
    LLMRequest.builder()
        .model("gemini-pro")
        .system("You are a helpful assistant")
        .user("Explain quantum computing briefly")
        .temperature(0.7)
        .maxTokens(500)
        .build()
);

future.thenAccept(response -> {
    System.out.println("Response: " + response.getContent());
    System.out.println("Tokens used: " + response.getUsage().getTotalTokens());
});
```

### Multi-Provider Comparison

```java
// Compare responses from different providers
List<String> models = List.of("gpt-4", "claude-3-opus", "gemini-pro");

for (String model : models) {
    LLMResponse response = llm.complete(
        LLMRequest.builder()
            .model(model)
            .user("What is 2+2?")
            .build()
    );
    
    System.out.printf("[%s] via %s: %s (in %dms)%n",
        model,
        response.getProvider(),
        response.getContent(),
        response.getLatencyMs()
    );
}
```

### Using Azure OpenAI

```java
OneLLM llm = OneLLM.builder()
    .azure(
        System.getenv("AZURE_OPENAI_KEY"),
        "my-resource",      // Azure resource name
        "gpt4-deployment"   // Deployment name
    )
    .build();

LLMResponse response = llm.complete(
    LLMRequest.builder()
        .model("azure/gpt-4")
        .user("Hello from Azure!")
        .build()
);
```

### Using Local Ollama

```java
OneLLM llm = OneLLM.builder()
    .ollama()  // localhost:11434 by default
    .build();

LLMResponse response = llm.complete(
    LLMRequest.builder()
        .model("ollama/llama2")
        .user("Hello locally!")
        .build()
);
```

## Error Handling

```java
try {
    LLMResponse response = llm.complete(request);
} catch (ModelNotFoundException e) {
    System.err.println("Unknown model: " + e.getModel());
} catch (ProviderNotConfiguredException e) {
    System.err.println("Provider not configured: " + e.getProviderName());
} catch (LLMException e) {
    if (e.isRateLimitError()) {
        System.err.println("Rate limited, retry later");
    } else if (e.isAuthenticationError()) {
        System.err.println("Invalid API key for: " + e.getProvider());
    } else {
        System.err.println("API error: " + e.getMessage());
    }
}
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/onellm/onellm-java.git
cd onellm-java

# Build with Maven
mvn clean package

# Run tests
mvn test
```

## License

MIT License - see [LICENSE](LICENSE) for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.
