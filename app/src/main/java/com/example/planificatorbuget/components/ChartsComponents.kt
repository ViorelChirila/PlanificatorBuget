package com.example.planificatorbuget.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.GroupBarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.Period
import com.example.planificatorbuget.utils.calculateMean
import com.example.planificatorbuget.utils.calculateYAxisSteps
import com.example.planificatorbuget.utils.filterTransactionsByPeriod
import com.example.planificatorbuget.utils.formatDateToString
import java.util.Date

enum class ChartType {
    BAR_CHART,
    LINE_CHART
}
@Composable
fun TransactionsBarChart(
    modifier: Modifier = Modifier,
    transactions: List<TransactionModel> = emptyList(),
    period: Period = Period.LAST_7_DAYS,
    transactionType: String = "Cheltuiala",
    showNameDay: Boolean = true,
    xAxisRotationAngle: Float = 0f,
    xAxisBottomPadding: Dp = 20.dp,
    xAxisLabelFontSize: TextUnit = 0.sp,
    onMeanCalculated: (Double) -> Unit = {}
) {
    val expensesData = filterTransactionsByPeriod(transactions, period, transactionType,showAsDayName = showNameDay)

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
        .bottomPadding(xAxisBottomPadding)
        .axisLabelAngle(xAxisRotationAngle)
        .labelData { index -> barDataList[index].label }
        .backgroundColor(Color.White)
        .startDrawPadding(30.dp)
        .axisLabelFontSize(if (xAxisLabelFontSize.value == 0f) 15.sp else xAxisLabelFontSize)
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

@Composable
fun TransactionsLineChart(
    modifier: Modifier = Modifier,
    transactions: List<TransactionModel> = emptyList(),
    period: Period = Period.LAST_7_DAYS,
    transactionType: String = "Cheltuiala",
    showNameDay: Boolean = true,
    xAxisRotationAngle: Float = 0f,
    xAxisBottomPadding: Dp = 20.dp,
    xAxisLabelFontSize: TextUnit = 0.sp,
    lineStyleColor: Color = Color.Black,
    shadowUnderLine: Color = Color.Black,
    onMeanCalculated: (Double) -> Unit = {}
) {
    val expensesData = filterTransactionsByPeriod(transactions, period, transactionType,showAsDayName = showNameDay)

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

    val pointsData = barDataList.map { Point(it.point.x, it.point.y) }

    val meanValue = calculateMean(expensesData)
    onMeanCalculated(meanValue)

    val xAxisData = AxisData.Builder()
        .axisStepSize(60.dp)
        .steps(pointsData.size - 1)
        .bottomPadding(xAxisBottomPadding)
        .axisLabelAngle(xAxisRotationAngle)
        .labelData { index -> barDataList[index].label }
        .backgroundColor(Color.White)
        .startDrawPadding(0.dp)
        .axisLabelFontSize(if (xAxisLabelFontSize.value == 0f) 15.sp else xAxisLabelFontSize)
        .build()


    val maxRange = pointsData.maxOfOrNull { it.y } ?: 0f
    val yAxisLabels = calculateYAxisSteps(maxRange.toDouble(), 5)
    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> yAxisLabels.getOrElse(index) { "" } }
        .backgroundColor(Color.White)
        .build()


    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(color = lineStyleColor),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(color = shadowUnderLine),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )


    LineChart(
        modifier = modifier,
        lineChartData = lineChartData
    )

}

@Composable
fun GroupedBarChart(
    modifier: Modifier = Modifier,
    xAxisRotationAngle: Float = 0f,
    xAxisBottomPadding: Dp = 20.dp,
    xAxisLabelFontSize: TextUnit = 0.sp,
    chartData: List<GroupBar>
) {
    val groupBarPlotData = BarPlotData(
        groupBarList = chartData,
        barColorPaletteList = listOf(Color(0xFF258B41), Color.Red)
    )

    val xAxisData = AxisData.Builder()
        .axisStepSize(60.dp)
        .steps(chartData.size - 1)
        .bottomPadding(40.dp)
        .labelData { index -> chartData[index].label }
        .bottomPadding(xAxisBottomPadding)
        .axisLabelAngle(xAxisRotationAngle)
        .startDrawPadding(30.dp)
        .backgroundColor(Color.White)
        .build()

    var maxRange = 0f
    for (groupBar in chartData) {
        for (barData in groupBar.barList) {
            if (barData.point.y > maxRange) {
                maxRange = barData.point.y
            }
        }
    }
    val yAxisLabels = calculateYAxisSteps(maxRange.toDouble(), 5)
    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> yAxisLabels.getOrElse(index) { "" } }
        .backgroundColor(Color.White)
        .build()

    val groupBarChartData = GroupBarChartData(
        barPlotData = groupBarPlotData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.White,
    )
    GroupBarChart(modifier = modifier, groupBarChartData = groupBarChartData)
}


@Composable
fun BudgetEvolutionLineChart(
    modifier: Modifier = Modifier,
    xAxisRotationAngle: Float = 0f,
    xAxisBottomPadding: Dp = 20.dp,
    xAxisLabelFontSize: TextUnit = 0.sp,
    lineStyleColor: Color = Color.Black,
    shadowUnderLine: Color = Color.Black,
    intersectionPointColor: Color = Color.Black,
    intersectionPointRadius: Dp =6.dp,
    chartData: List<Pair<Date,Double>>
){
    val pointsData = chartData.mapIndexed { index, data ->
        Point(
            (index + 1).toFloat(),
            data.second.toFloat()
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(pointsData.size - 1)
        .bottomPadding(xAxisBottomPadding)
        .axisLabelAngle(xAxisRotationAngle)
        .labelData { index -> formatDateToString(chartData[index].first) }
        .backgroundColor(Color.White)
        .startDrawPadding(0.dp)
        .axisLabelFontSize(if (xAxisLabelFontSize.value == 0f) 15.sp else xAxisLabelFontSize)
        .build()

    val maxRange = pointsData.maxOfOrNull { it.y } ?: 0f
    val yAxisLabels = calculateYAxisSteps(maxRange.toDouble(), 5)
    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> yAxisLabels.getOrElse(index) { "" }+ " lei"}
        .backgroundColor(Color.White)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(color = lineStyleColor),
                    IntersectionPoint(color = intersectionPointColor,radius = intersectionPointRadius),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(color = shadowUnderLine),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White,
        containerPaddingEnd = 50.dp
    )

    LineChart(
        modifier = modifier,
        lineChartData = lineChartData
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