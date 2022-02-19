package com.dimafeng.testcontainers

import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest
import com.google.cloud.bigtable.data.v2.models.RowMutation
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class BigtableEmulatorContainerSpec
    extends AnyWordSpecLike
    with Matchers
    with ForAllTestContainer {

  override val container: BigtableEmulatorContainer =
    BigtableEmulatorContainer()

  "Bigtable emulator container" should {

    "start up and access table data" in {
      val tableAdminClient = container.tableAdminClient
      val dataClient = container.dataClient

      try {
        val tableId = "test-table"
        tableAdminClient.createTable(
          CreateTableRequest.of(tableId).addFamily("name")
        )

        val firstNameMutation = RowMutation
          .create(tableId, "alovelace")
          .setCell("name", "first", "Ada")

        dataClient.mutateRow(firstNameMutation)

        val row = dataClient.readRow(tableId, "alovelace")
        val cell = row.getCells("name").get(0)
        cell.getValue.toStringUtf8 shouldEqual "Ada"
      } finally {
        tableAdminClient.close()
        dataClient.close()
      }
    }
  }
}
