package no.digdir.organizationcatalog.utils

import org.springframework.http.HttpStatus
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.sql.DriverManager

fun apiGet(
    endpoint: String,
    port: Int,
    acceptHeader: String?,
    otherHeaders: List<Pair<String, String>> = emptyList(),
): Map<String, Any> =
    try {
        val connection = URI(getApiAddress(port, endpoint)).toURL().openConnection() as HttpURLConnection
        acceptHeader?.let { connection.addRequestProperty("Accept", it) }
        otherHeaders.forEach { connection.addRequestProperty(it.first, it.second) }
        connection.connect()

        if (isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body" to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode,
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " ",
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " ",
        )
    }

fun apiAuthorizedRequest(
    endpoint: String,
    port: Int,
    body: String?,
    token: String?,
    method: String,
): Map<String, Any> =
    try {
        val connection = URI("http://localhost:$port$endpoint").toURL().openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.setRequestProperty("Content-type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        if (!token.isNullOrEmpty()) connection.setRequestProperty("Authorization", "Bearer $token")

        connection.doOutput = true
        connection.connect()

        if (body != null) {
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(body)
            writer.close()
        }

        if (isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body" to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode,
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " ",
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " ",
        )
    }

private fun isOK(response: Int?): Boolean =
    if (response == null) {
        false
    } else {
        HttpStatus.resolve(response)?.is2xxSuccessful == true
    }

fun resetDB() {
    val container = ApiTestContext.postgresContainer
    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
    conn.use { c ->
        val stmt = c.createStatement()
        stmt.execute("DELETE FROM organization_domains")
        stmt.execute("DELETE FROM org_pref_labels")
        stmt.execute("DELETE FROM organizations")

        val orgs = organizationsDBPopulation()
        for (org in orgs) {
            val ps =
                c.prepareStatement(
                    """INSERT INTO organizations
                    (organization_id, name, org_type, org_path, sub_organization_of, issued,
                     municipality_number, industry_code, sector_code, pref_label_nb, pref_label_nn,
                     pref_label_en, org_status, homepage, allow_delegated_registration, subordinate)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                )
            ps.setString(1, org.organizationId)
            ps.setString(2, org.name)
            ps.setString(3, org.orgType)
            ps.setString(4, org.orgPath)
            ps.setString(5, org.subOrganizationOf)
            if (org.issued != null) ps.setObject(6, org.issued) else ps.setNull(6, java.sql.Types.DATE)
            ps.setString(7, org.municipalityNumber)
            ps.setString(8, org.industryCode)
            ps.setString(9, org.sectorCode)
            ps.setString(10, org.prefLabel?.nb)
            ps.setString(11, org.prefLabel?.nn)
            ps.setString(12, org.prefLabel?.en)
            ps.setString(13, org.orgStatus?.name)
            ps.setString(14, org.homepage)
            if (org.allowDelegatedRegistration != null) {
                ps.setBoolean(15, org.allowDelegatedRegistration!!)
            } else {
                ps.setNull(15, java.sql.Types.BOOLEAN)
            }
            ps.setBoolean(16, org.subordinate)
            ps.executeUpdate()
            ps.close()
        }
        stmt.close()
    }
}

data class JenaAndHeader(
    val acceptHeader: String,
    val jenaType: String,
)
