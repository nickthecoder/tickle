package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.Layout
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.stage.*

class LayoutTab(name: String, layout: Layout) : EditorTab("Layout $name", layout) {

    init {
        val taskPane = TaskPane(this, LayoutTask(name, layout))
        content = taskPane.borderPane
    }
}

class LayoutTask(val name: String, val layout: Layout) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val stagesP = MultipleParameter("stages", isBoxed = true) {
        LayoutStageParameter()
    }

    val viewsP = MultipleParameter("views", isBoxed = true) {
        LayoutViewParameter()
    }

    override val taskD = TaskDescription("editLayout")
            .addParameters(nameP, stagesP, viewsP)

    init {
        stagesP.asListDetail(height = 100) { if (it.stageNameP.value.isBlank()) "<new>" else it.stageNameP.value }
        viewsP.asListDetail(height = 100) { if (it.viewNameP.value.isBlank()) "<new>" else it.viewNameP.value }

        layout.stages.forEach { name, layoutStage ->
            val inner = stagesP.newValue()
            inner.stageNameP.value = name
            inner.stageClassP.value = Class.forName(layoutStage.stageString)
        }


        layout.views.forEach { name, layoutView ->
            val inner = viewsP.newValue()
            inner.viewNameP.value = name
            inner.stageNameP.value = layoutView.stageName
            inner.viewClassP.value = Class.forName(layoutView.viewString)

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
            println("FlexPosition = ${layoutView.position}")
        }
    }

    override fun customCheck() {
        val l = Resources.instance.optionalLayout(nameP.value)
        if (l != null && l != layout) {
            throw ParameterException(nameP, "This name is already used.")
        }

        viewsP.innerParameters.forEach {
            val stageName = it.stageNameP.value
            if (!stagesP.innerParameters.map { it.stageNameP.value }.contains(stageName)) {
                throw ParameterException(it.stageNameP, "This is not a name of a stage in this layout")
            }
        }
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.renameLayout(name, nameP.value)
        }
    }
}

class LayoutStageParameter() : MultipleGroupParameter("stage") {

    val stageNameP = StringParameter("stageName")
    val stageClassP = ChoiceParameter<Class<*>>("class", value = GameStage::class.java)

    init {
        addParameters(stageNameP, stageClassP)
        ClassLister.setChoices(stageClassP, Stage::class.java)
    }
}


class LayoutViewParameter() : MultipleGroupParameter("view") {

    val viewNameP = StringParameter("viewName")
    val stageNameP = StringParameter("stageName")
    val viewClassP = ChoiceParameter<Class<*>>("class", value = ZOrderStageView::class.java)

    // X
    val hAlignmentP = ChoiceParameter<FlexHAlignment>("hAlignment", value = FlexHAlignment.LEFT).enumChoices()
    val leftRightMarginP = IntParameter("leftRightMargin", label = "Margin")
    val hPositionP = DoubleParameter("hPosition", label = "Position", value = 0.5)

    val remainingWidthP = InformationParameter("remainingWidth", label = "Remaining Width", information = "")
    val widthP = IntParameter("width", label = "Fixed Width")
    val widthRatioP = DoubleParameter("widthRatio", label = "Ratio")
    val hOneOfP = OneOfParameter("hOneOf", label = "Width", choiceLabel = "Choose")
            .addParameters(remainingWidthP, widthP, widthRatioP).asHorizontal(labelPosition = LabelPosition.NONE)

    val xGroup = SimpleGroupParameter("x")
            .addParameters(hAlignmentP, leftRightMarginP, hPositionP, hOneOfP)

    // Y
    val vAlignmentP = ChoiceParameter<FlexVAlignment>("vAlignment", value = FlexVAlignment.TOP).enumChoices()
    val topBottomMarginP = IntParameter("topBottomMargin", label = "Margin")
    val vPositionP = DoubleParameter("vPosition", label = "Position", value = 0.5)

    val remainingHeightP = InformationParameter("remainingHeight", label = "Remaining Height", information = "")
    val heightP = IntParameter("height", label = "Fixed Height")
    val heightRatioP = DoubleParameter("heightRatio", label = "Ratio")
    val vOneOfP = OneOfParameter("vOneOf", label = "Height", choiceLabel = "Choose")
            .addParameters(remainingHeightP, heightP, heightRatioP).asHorizontal(labelPosition = LabelPosition.NONE)

    val yGroup = SimpleGroupParameter("y")
            .addParameters(vAlignmentP, topBottomMarginP, vPositionP, vOneOfP)


    init {
        hPositionP.hidden = true
        hOneOfP.value = widthP
        asPlain()

        hAlignmentP.listen {
            leftRightMarginP.hidden = hAlignmentP.value == FlexHAlignment.MIDDLE
            hPositionP.hidden = !leftRightMarginP.hidden
        }

        vAlignmentP.listen {
            topBottomMarginP.hidden = vAlignmentP.value == FlexVAlignment.MIDDLE
            vPositionP.hidden = !topBottomMarginP.hidden
        }

        addParameters(viewNameP, stageNameP, viewClassP, xGroup, yGroup)
        ClassLister.setChoices(viewClassP, View::class.java)
    }
}
