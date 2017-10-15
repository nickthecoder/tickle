package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.Layout
import uk.co.nickthecoder.tickle.LayoutStage
import uk.co.nickthecoder.tickle.LayoutView
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.stage.*

class LayoutTab(val name: String, val layout: Layout)

    : EditTab(name, layout, graphicName = "layout.png") {

    val stagesTask = StagesTask()
    val viewsTask = ViewsTask()

    val stagesForm = TaskForm(stagesTask)
    val viewsForm = TaskForm(viewsTask)

    val minorTabs = MyTabPane<MyTab>()

    val stagesTab = MyTab("Stages", stagesForm.build())
    val viewsTab = MyTab("Views", viewsForm.build())

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(stagesTab)
        minorTabs.add(viewsTab)
        borderPane.center = minorTabs

        addDeleteButton { Resources.instance.deleteLayout(name) }
    }

    override fun save(): Boolean {
        if (stagesForm.check()) {
            if (viewsForm.check()) {
                stagesTask.run()
                viewsTask.run()
                return true
            } else {
                viewsTab.isSelected = true
            }
        } else {
            stagesTab.isSelected = true
        }
        return false
    }


    inner class StagesTask() : AbstractTask() {

        val nameP = StringParameter("name", value = name)

        val stagesP = MultipleParameter("stages", minItems = 1, isBoxed = true) {
            LayoutStageParameter()
        }

        override val taskD = TaskDescription("editLayout")
                .addParameters(nameP, stagesP)

        init {
            stagesP.asListDetail(width = 200, height = 200) { if (it.stageNameP.value.isBlank()) "<new>" else it.stageNameP.value }

            layout.layoutStages.forEach { name, layoutStage ->
                val inner = stagesP.newValue()
                inner.stageNameP.value = name
                inner.stageClassP.value = Class.forName(layoutStage.stageString)
            }

        }

        override fun customCheck() {
            val l = Resources.instance.optionalLayout(nameP.value)
            if (l != null && l != layout) {
                throw ParameterException(nameP, "This name is already used.")
            }

        }

        override fun run() {
            if (nameP.value != name) {
                Resources.instance.renameLayout(name, nameP.value)
            }

            layout.layoutStages.clear()

            stagesP.innerParameters.forEach { inner ->
                val layoutStage = LayoutStage()
                layout.layoutStages[inner.stageNameP.value] = layoutStage

                with(layoutStage) {
                    layoutStage.stageString = inner.stageClassP.value!!.name
                }
            }
        }
    }

    inner class ViewsTask() : AbstractTask() {

        val viewsP = MultipleParameter("views", label = "", minItems = 1) {
            LayoutViewParameter()
        }

        override val taskD = TaskDescription("editLayoutViews")
                .addParameters(viewsP)

        init {
            viewsP.asListDetail(width = 200, height = 350) { if (it.viewNameP.value.isBlank()) "<new>" else it.viewNameP.value }

            layout.layoutViews.forEach { name, layoutView ->
                val inner = viewsP.newValue()
                inner.viewNameP.value = name
                inner.stageNameP.value = layoutView.stageName
                inner.viewClassP.value = Class.forName(layoutView.viewString)
                inner.zOrderP.value = layoutView.zOrder

                with(layoutView.position) {
                    inner.hAlignmentP.value = hAlignment
                    inner.leftRightMarginP.value = leftRightMargin
                    inner.hPositionP.value = hPosition.toDouble()
                    inner.widthP.value = width
                    inner.widthRatioP.value = widthRatio?.toDouble()
                    if (width != null) {
                        inner.hOneOfP.value = inner.widthP
                    } else if (widthRatio != null) {
                        inner.hOneOfP.value = inner.widthRatioP
                    } else {
                        inner.hOneOfP.value = inner.remainingWidthP
                    }

                    inner.vAlignmentP.value = vAlignment
                    inner.topBottomMarginP.value = topBottomMargin
                    inner.vPositionP.value = vPosition.toDouble()
                    inner.heightP.value = height
                    inner.heightRatioP.value = heightRatio?.toDouble()
                    if (height != null) {
                        inner.vOneOfP.value = inner.heightP
                    } else if (heightRatio != null) {
                        inner.vOneOfP.value = inner.heightRatioP
                    } else {
                        inner.vOneOfP.value = inner.remainingHeightP
                    }
                }
            }
        }


        override fun customCheck() {

            viewsP.innerParameters.forEach {
                val stageName = it.stageNameP.value
                if (!stagesTask.stagesP.innerParameters.map { it.stageNameP.value }.contains(stageName)) {
                    throw ParameterException(it.stageNameP, "This is not a name of a stage in this layout")
                }
            }
        }

        override fun run() {

            layout.layoutViews.clear()

            viewsP.innerParameters.forEach { inner ->
                val layoutView = LayoutView()
                layout.layoutViews[inner.viewNameP.value] = layoutView

                with(layoutView) {
                    stageName = inner.stageNameP.value
                    viewString = inner.viewClassP.value!!.name
                    zOrder = inner.zOrderP.value!!
                }

                with(layoutView.position) {

                    // X
                    hAlignment = inner.hAlignmentP.value!!
                    if (hAlignment == FlexHAlignment.MIDDLE) {
                        hPosition = inner.hPositionP.value!!
                    } else {
                        leftRightMargin = inner.leftRightMarginP.value!!
                    }
                    width = if (inner.hOneOfP.value == inner.widthP) inner.widthP.value!! else null
                    widthRatio = if (inner.hOneOfP.value == inner.widthRatioP) inner.widthRatioP.value!! else null

                    // Y
                    vAlignment = inner.vAlignmentP.value!!
                    if (vAlignment == FlexVAlignment.MIDDLE) {
                        vPosition = inner.vPositionP.value!!
                    } else {
                        topBottomMargin = inner.topBottomMarginP.value!!
                    }
                    height = if (inner.vOneOfP.value == inner.heightP) inner.heightP.value!! else null
                    heightRatio = if (inner.vOneOfP.value == inner.heightRatioP) inner.heightRatioP.value!! else null
                }
            }
        }
    }


    inner class LayoutStageParameter() : MultipleGroupParameter("stage") {

        val stageNameP = StringParameter("stageName")
        val stageClassP = ChoiceParameter<Class<*>>("class", value = GameStage::class.java)
        val createViewP = ButtonParameter("createView", label = "", buttonText = "Create Whole Screen View") { createView() }

        init {
            addParameters(stageNameP, stageClassP, createViewP)
            ClassLister.setChoices(stageClassP, Stage::class.java)
        }

        fun createView() {
            viewsTask.viewsP.innerParameters.forEach { inner ->
                if (inner.viewNameP.value == stageNameP.value) {
                    return
                }
            }
            val inner = viewsTask.viewsP.newValue()
            inner.viewNameP.value = stageNameP.value
            inner.stageNameP.value = stageNameP.value
        }
    }


    inner class LayoutViewParameter() : MultipleGroupParameter("view") {

        val viewNameP = StringParameter("viewName")
        val stageNameP = StringParameter("stageName")
        val viewClassP = ChoiceParameter<Class<*>>("class", value = ZOrderStageView::class.java)
        val zOrderP = IntParameter("zOrder")

        // X
        val hAlignmentP = ChoiceParameter<FlexHAlignment>("hAlignment", label = "", value = FlexHAlignment.LEFT).enumChoices()
        val leftRightMarginP = IntParameter("leftRightMargin", label = "Margin", value = 0)
        val hPositionP = DoubleParameter("hPosition", label = "Position", value = 0.5)
        val hAlignGroupP = SimpleGroupParameter("hAlignGroup", label = "Align")
                .addParameters(hAlignmentP, leftRightMarginP, hPositionP).asHorizontal(LabelPosition.LEFT)

        val remainingWidthP = InformationParameter("remainingWidth", label = "Remaining", information = "")
        val widthP = IntParameter("width", label = "Fixed Width")
        val widthRatioP = DoubleParameter("widthRatio", label = "Ratio")
        val hOneOfP = OneOfParameter("hOneOf", label = "Width", value = remainingWidthP, choiceLabel = "Choose")
                .addParameters(remainingWidthP, widthP, widthRatioP).asHorizontal(labelPosition = LabelPosition.NONE)

        val xGroup = SimpleGroupParameter("x")
                .addParameters(hAlignGroupP, hOneOfP)

        // Y
        val vAlignmentP = ChoiceParameter<FlexVAlignment>("vAlignment", label = "", value = FlexVAlignment.TOP).enumChoices()
        val topBottomMarginP = IntParameter("topBottomMargin", label = "Margin", value = 0)
        val vPositionP = DoubleParameter("vPosition", label = "Position", value = 0.5)
        val vAlignGroupP = SimpleGroupParameter("vAlignGroup", label = "Align")
                .addParameters(vAlignmentP, topBottomMarginP, vPositionP).asHorizontal(LabelPosition.LEFT)

        val remainingHeightP = InformationParameter("remainingHeight", label = "Remaining", information = "")
        val heightP = IntParameter("height", label = "Fixed Height")
        val heightRatioP = DoubleParameter("heightRatio", label = "Ratio")
        val vOneOfP = OneOfParameter("vOneOf", label = "Height", value = remainingHeightP, choiceLabel = "Choose")
                .addParameters(remainingHeightP, heightP, heightRatioP).asHorizontal(labelPosition = LabelPosition.NONE)

        val yGroup = SimpleGroupParameter("y")
                .addParameters(vAlignGroupP, vOneOfP)


        init {
            asPlain()
            hPositionP.hidden = true
            vPositionP.hidden = true

            hAlignmentP.listen {
                leftRightMarginP.hidden = hAlignmentP.value == FlexHAlignment.MIDDLE
                hPositionP.hidden = !leftRightMarginP.hidden
            }

            vAlignmentP.listen {
                topBottomMarginP.hidden = vAlignmentP.value == FlexVAlignment.MIDDLE
                vPositionP.hidden = !topBottomMarginP.hidden
            }

            addParameters(viewNameP, stageNameP, viewClassP, zOrderP, xGroup, yGroup)
            ClassLister.setChoices(viewClassP, View::class.java)
        }
    }

}
