package no.publishers.graphql

data class CreatePublisher (
    val name: String,
    val uri: String,
    val organizationId: String,
    val orgPath: String,
    val nbPrefLabel: String?,
    val nnPrefLabel: String?,
    val enPrefLabel: String?
)