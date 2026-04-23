package com.drsecuritygps.app.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReportsResolutionTest {
    @Test
    fun `resolves vehicle history report from spanish catalog`() {
        val catalog = ReportCatalog(
            types = listOf(
                ReportCatalogChoice(id = "14", label = "Historial de vehículos"),
                ReportCatalogChoice(id = "3", label = "Recorridos y Paradas"),
            ),
            formats = listOf(ReportCatalogChoice(id = "xls", label = "Excel XLS")),
        )

        val resolved = catalog.resolveType(ReportKind.VehicleHistory)

        assertNotNull(resolved)
        assertEquals("14", resolved.id)
    }

    @Test
    fun `resolves drives stops report from english catalog`() {
        val catalog = ReportCatalog(
            types = listOf(
                ReportCatalogChoice(id = "3", label = "Drives and stops"),
                ReportCatalogChoice(id = "5", label = "Overspeed"),
            ),
        )

        val resolved = catalog.resolveType(ReportKind.DrivesStops)

        assertNotNull(resolved)
        assertEquals("3", resolved.id)
    }

    @Test
    fun `resolves xls format from id or label`() {
        val catalog = ReportCatalog(
            formats = listOf(
                ReportCatalogChoice(id = "pdf", label = "PDF"),
                ReportCatalogChoice(id = "xls", label = "Spreadsheet"),
            ),
        )

        val resolved = catalog.resolveFormat()

        assertNotNull(resolved)
        assertEquals("xls", resolved.id)
    }

    @Test
    fun `validates report range ordering`() {
        assertNull(
            validateReportRange(
                ReportRange(
                    fromDate = "2026-03-26",
                    fromTime = "00:00",
                    toDate = "2026-03-26",
                    toTime = "12:45",
                ),
            ),
        )

        assertEquals(
            "La fecha Desde debe ser anterior o igual a Hasta.",
            validateReportRange(
                ReportRange(
                    fromDate = "2026-03-26",
                    fromTime = "16:00",
                    toDate = "2026-03-26",
                    toTime = "12:45",
                ),
            ),
        )
    }
}
