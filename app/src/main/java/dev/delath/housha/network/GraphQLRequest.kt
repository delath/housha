package dev.delath.housha.network

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any?> = emptyMap()
)

