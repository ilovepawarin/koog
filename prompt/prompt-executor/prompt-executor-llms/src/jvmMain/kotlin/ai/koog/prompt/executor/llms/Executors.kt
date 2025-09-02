package ai.koog.prompt.executor.llms

import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.model.JavaPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import org.jetbrains.annotations.ApiStatus

/**
 * Provides utility methods for creating instances of `JavaPromptExecutor`.
 *
 * The `Executors` object is designed to simplify the process of configuring and initializing
 * prompt executors that interact with Large Language Model (LLM) clients. It supports both
 * single and multiple LLM providers, enabling flexibility in execution configurations.
 *
 * This utility ensures that appropriate `JavaPromptExecutor` instances are created with
 * the required delegates or configurations.
 *
 * Note: This API is experimental and may change in future versions.
 */
@ApiStatus.Experimental
public object Executors {

    @JvmStatic
    public fun promptExecutor(llmClients: Map<LLMProvider, LLMClient>): JavaPromptExecutor =
        JavaPromptExecutor(
            delegate = MultiLLMPromptExecutor(llmClients)
        )

    @JvmStatic
    public fun promptExecutor(llmProvider: LLMProvider, llmClient: LLMClient): JavaPromptExecutor =
        promptExecutor(mapOf(llmProvider to llmClient))
}
