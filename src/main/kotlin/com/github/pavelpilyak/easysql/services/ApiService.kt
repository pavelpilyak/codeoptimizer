package com.github.pavelpilyak.aioptimizer.services

import com.amazon.ion.system.IonTextWriterBuilder.json
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.pavelpilyak.aioptimizer.MyBundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Service(Service.Level.PROJECT)
class ApiService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun optimizeCode(code: String): String {
        val values = mapOf("code" to code)

        val objectMapper = ObjectMapper()
        val requestBody: String = objectMapper
            .writeValueAsString(values)

        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .uri(URI.create("https://codeoptimizer.online/api/optimize"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val mapper = ObjectMapper()
        val map: MutableMap<*, *>? = mapper.readValue(response.body(), MutableMap::class.java)

        val optimizedCode = map?.get("code") ?: throw Error("Something went wrong")

        return optimizedCode.toString();
    }
}
