
import io.onellm.OneLLM;
import io.onellm.core.LLMRequest;
import io.onellm.core.LLMResponse;

public class Main {

    public static void main(String[] args) {
        try (OneLLM llm = OneLLM.builder().ollama().build()) {
            System.out.println("Configured providers: " + llm.listProviders());

            var llmRequest = new LLMRequest.Builder()
                    .model("local/gemma3:270m")
                    .user("write a code in python to add two numbers.")
                    .build();

            var llmResponse = llm.complete(llmRequest);

            System.out.println(
                    llmResponse.getContent()
            );

            System.out.println("Done.");

        } catch (Exception e) {
            System.err.println("Error while running test Main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
