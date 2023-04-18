package com.qi.billiards.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

class TableView(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    private val tableLayout = TableLayout(context)
    private val tableHeader = TableRow(context)

    // 表头样式
    private var headerTextSize = 16f
    private var headerTextColor = Color.BLACK
    private var headerBackgroundColor = Color.GRAY
    private var headerHeight = 80

    init {
        addView(tableLayout)
        tableLayout.addView(tableHeader)

        // 设置默认表头样式
        tableHeader.setBackgroundColor(headerBackgroundColor)
        tableHeader.minimumHeight = headerHeight
    }

    /**
     * 设置表头数据
     */
    fun setTableHeaderData(headerData: List<String>) {
        tableHeader.removeAllViews()
        for (i in headerData.indices) {
            val headerTextView = TextView(context).apply {
                text = headerData[i]
                textSize = headerTextSize
                gravity = Gravity.CENTER
                setTextColor(headerTextColor)
            }

            tableHeader.addView(headerTextView, i)
        }
        requestLayout()
        post {
            makeColumnsEqualWidth()
        }
    }

    /**
     * 设置表格数据
     */
    fun setTableData(tableData: List<List<String>>) {
        tableLayout.removeViews(1, childCount - 1)
        for (i in tableData.indices) {
            val rowData = tableData[i]
            val tableRow = TableRow(context)

            for (j in rowData.indices) {
                val dataTextView = TextView(context).apply {
                    text = rowData[j]
                    textSize = 14f
                    gravity = Gravity.CENTER
                    setTextColor(Color.BLACK)
                }
                tableRow.addView(dataTextView, j)
            }

            tableLayout.addView(tableRow, i + 1)
        }
        requestLayout()
        post {
            adjustHeight()
        }
    }

    /**
     * 自适应内容的高度
     */
    private fun adjustHeight() {
        // 计算表头的高度
        var height = tableHeader.height

        // 计算表格数据的高度
        for (i in 0 until tableLayout.childCount) {
            val rowView = tableLayout.getChildAt(i) as TableRow
            height += rowView.height
        }

        // 设置 TableView 的高度
        val params = layoutParams
        params.height = height
        layoutParams = params
    }

    /**
     * 设置表头高度
     */
    fun setHeaderHeight(height: Int) {
        this.headerHeight = height
        tableHeader.minimumHeight = headerHeight
        requestLayout()
    }

    /**
     * 设置表头背景色
     */
    fun setHeaderBackgroundColor(color: Int) {
        this.headerBackgroundColor = color
        tableHeader.setBackgroundColor(headerBackgroundColor)
    }

    /**
     * 设置表头文本字体大小
     */
    fun setHeaderTextSize(size: Float) {
        this.headerTextSize = size
        for (i in 0 until tableHeader.childCount) {
            val headerTextView = tableHeader.getChildAt(i) as TextView
            headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headerTextSize)
        }
    }

    /**
     * 设置表头文本字体颜色
     */
    fun setHeaderTextColor(color: Int) {
        this.headerTextColor = color
        for (i in 0 until tableHeader.childCount) {
            val headerTextView = tableHeader.getChildAt(i) as TextView
            headerTextView.setTextColor(color)
        }
    }

    /**
     * 将表格每列宽度设置为平均值
     */
    private fun makeColumnsEqualWidth() {
        for (i in 0 until tableLayout.childCount) {
            val rowView = tableLayout.getChildAt(i) as TableRow
            val columns = rowView.childCount
            val averageWidth = measuredWidth / columns
            for (j in 0 until columns) {
                val cellView = rowView.getChildAt(j) as TextView
                val params = cellView.layoutParams as TableRow.LayoutParams
                params.width = averageWidth
                params.gravity = Gravity.CENTER
                cellView.layoutParams = params
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val childWidth = r - l - paddingLeft - paddingRight
        val childHeight = b - t - paddingTop - paddingBottom

        tableLayout.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + childWidth,
            paddingTop + childHeight
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredWidth - paddingLeft - paddingRight,
            MeasureSpec.EXACTLY
        )
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight - paddingTop - paddingBottom,
            MeasureSpec.EXACTLY
        )

        tableLayout.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }
}

