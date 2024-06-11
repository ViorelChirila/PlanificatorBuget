package com.example.planificatorbuget.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.Period
import com.example.planificatorbuget.utils.calculateMean
import com.example.planificatorbuget.utils.calculateYAxisSteps
import com.example.planificatorbuget.utils.filterTransactionsByPeriod

@Composable
fun TransactionsBarChart(
    modifier: Modifier = Modifier,
    transactions: List<TransactionModel> = emptyList(),
    period: Period = Period.LAST_7_DAYS,
    transactionType: String = "Cheltuiala",
    onMeanCalculated: (Double) -> Unit = {}
) {
    val expensesData = filterTransactionsByPeriod(transactions, period, transactionType)

    val barDataList =
        expensesData.mapIndexed { index, data ->
            BarData(
                point = Point(
                    (index + 1).toFloat(),
                    if (transactionType == "Cheltuiala") data.second.toFloat() * -1 else data.second.toFloat()
                ),
                label = data.first,
                color = if (transactionType == "Cheltuiala") Color.Red else Color(0xFF258B41)
            )
        }

    val meanValue = calculateMean(expensesData)
    onMeanCalculated(meanValue)

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barDataList.size - 1)
        .bottomPadding(20.dp)
        .labelData { index -> barDataList[index].label }
        .backgroundColor(Color.White)
        .startDrawPadding(20.dp)
        .build()


    val maxRange = barDataList.maxOfOrNull { it.point.y } ?: 0f
    val yAxisLabels = calculateYAxisSteps(maxRange.toDouble(), 5)
    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> yAxisLabels.getOrElse(index) { "" } }
        .backgroundColor(Color.White)
        .build()


    val barChartData = BarChartData(
        backgroundColor = Color.White,
        chartData = barDataList,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        horizontalExtraSpace = 20.dp,
    )


    BarChart(
        modifier = modifier,
        barChartData = barChartData
    )

}

@Preview
@Composable
fun LegendComponent() {
    Legends(
        legendsConfig = LegendsConfig(
            legendLabelList = listOf(
                LegendLabel(
                    name = "Venituri",
                    color = Color(0xFF258B41)
                ),
                LegendLabel(
                    name = "Cheltuieli",
                    color = Color.Red
                )
            ),
            legendsArrangement = Arrangement.Center,
            gridColumnCount = 2,
            colorBoxSize = 15.dp,
            spaceBWLabelAndColorBox = 5.dp
        ),
        modifier = Modifier
            .width(250.dp)
            .padding(start = 5.dp)
    )
}