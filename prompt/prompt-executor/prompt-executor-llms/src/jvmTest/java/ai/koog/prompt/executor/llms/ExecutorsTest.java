package ai.koog.prompt.executor.llms;

import ai.koog.prompt.dsl.Prompt;
import ai.koog.prompt.executor.clients.LLMClient;
import ai.koog.prompt.executor.model.JavaPromptExecutor;
import ai.koog.prompt.llm.LLMProvider;
import ai.koog.prompt.llm.LLModel;
import ai.koog.prompt.message.Message;
import ai.koog.prompt.message.RequestMetaInfo;
import ai.koog.prompt.params.LLMParams;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutorsTest {

    @Mock
    LLModel model;

    @Mock
    LLMProvider provider;

    LLMClient llmClient;

    @BeforeEach
    void beforeEach() {
        llmClient = new MockOpenAILLMClient("Hello from LLM");
        when(model.getProvider()).thenReturn(provider);
    }

    @Test
    void shouldExecutePromptAsync() {
        // given
        @NotNull JavaPromptExecutor promptExecutor = Executors.promptExecutor(
            provider,
            llmClient
        );
        assertThat(promptExecutor).isNotNull();

        final var requestMeta = RequestMetaInfo.Companion.getEmpty();

        final var systemMessage = new Message.System(
            "You are helpful assistant", requestMeta);

        final var userMessage = new Message.User(
            "Say Hello", requestMeta);

        final Prompt prompt = new Prompt(
            List.of(systemMessage, userMessage),
            UUID.randomUUID().toString(),
            new LLMParams()
        );

        // when
        final var future = promptExecutor.executeAsync(prompt, model);

        // then
        assertThat(future)
            .succeedsWithin(Duration.ofSeconds(3))
            .satisfies(responses -> {
                    assertThat(responses)
                        .hasSize(1)
                        .first().satisfies(assistantResponse -> {
                            assertThat(assistantResponse)
                                .isNotNull()
                                .isInstanceOf(Message.Assistant.class);
                            assertThat(assistantResponse.getRole()).isEqualTo(Message.Role.Assistant);
                            assertThat(assistantResponse.getContent()).isEqualTo("Hello from LLM");
                        });
                }
            );
    }
}
