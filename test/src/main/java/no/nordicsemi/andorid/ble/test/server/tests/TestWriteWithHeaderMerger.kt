package no.nordicsemi.andorid.ble.test.server.tests

import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.andorid.ble.test.server.data.HEADER_BASED_MERGER
import no.nordicsemi.andorid.ble.test.server.data.TestCase
import no.nordicsemi.andorid.ble.test.server.repository.ServerConnection
import no.nordicsemi.andorid.ble.test.server.tasks.TaskManager
import no.nordicsemi.andorid.ble.test.spec.HeaderBasedPacketMerger

class TestWriteWithHeaderMerger : TaskManager {

    override suspend fun start(
        scope: CoroutineScope,
        serverConnection: ServerConnection
    ) {
        serverConnection.testWriteCallback()
            .merge(HeaderBasedPacketMerger())
    }

    // Handle task completion
    override fun onTaskCompleted(): TestCase {
        return TestCase(HEADER_BASED_MERGER, true)

    }

    // Handle task failure
    override fun onTaskFailed(): TestCase {
        return TestCase(HEADER_BASED_MERGER, false)
    }
}