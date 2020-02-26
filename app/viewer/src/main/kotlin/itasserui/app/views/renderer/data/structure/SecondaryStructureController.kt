package itasserui.app.views.renderer.data.structure

import itasserui.app.views.renderer.components.line.Line
import itasserui.app.views.renderer.components.line.LineView
import itasserui.lib.pdb.parser.Alphahelix
import itasserui.lib.pdb.parser.Betasheet
import itasserui.lib.pdb.parser.Residue
import itasserui.lib.pdb.parser.SecondaryStructure
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Point3D
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.DrawMode
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat
import tornadofx.*
import java.lang.System.arraycopy

class SecondaryStructureController(val structure: SecondaryStructure, override val scope: Scope) :
    Controller() {
    val view: SecondaryStructureView by inject()
    val radiusProperty = 20.0.toProperty()
    var color: ObjectProperty<Color> = SimpleObjectProperty(Color.RED)
    val residues = structure.toList()


    fun compute() {
        if (structure.isEmpty()) {
            println("Structure empty")
            return
        }
        when (structure.structureType) {
            is Alphahelix -> makeAlphaHelix()
            is Betasheet -> makeBetaSheet()
        }
    }

    private fun makeBetaSheet() {
        val children = view.root.children

        val mesh = triangleMesh()
        val material = PhongMaterial(Color.CORNFLOWERBLUE)
        material.specularColor = Color.CORNFLOWERBLUE.brighter()

        val points = FloatArray(residues.size * 3 * 4)
        val texArray = floatArrayOf(0F, 0F)

        val faces = IntArray(residues.size * 6 * 8)
        val smoothing = IntArray(residues.size*8)

        val first = residues.first()
        val second = residues[1]

        val sourceCAlpha = first.cAlphaAtom.position
        val targetCAlpha = second.cAlphaAtom.position

        val sourceCAMirrored = second.cBetaAtom.position
            .subtract(first.cAlphaAtom.position)
            .multiply(-1.0)
            .add(first.cAlphaAtom.position)

        val sourceCBMirrored = first.cBetaAtom
            .position
            .subtract(first.cAlphaAtom.position)
            .multiply(-1.0)
            .add(first.cAlphaAtom.position)

        var direction = targetCAlpha.subtract(sourceCAlpha)
            .crossProduct(sourceCAMirrored.subtract(sourceCAlpha))
            .normalize()
            .multiply(sourceCAlpha.distance(sourceCAMirrored))
            .multiply(BETA_SHEET_DEPTH)

        lastRef = "CB"

        val initialBeta = first.cBetaAtom.position
        setPoints(0, points, initialBeta, sourceCBMirrored, direction)

        val endCap = triangleMesh()
        with(endCap) {
            this.texCoords.addAll(0.0F, 0.0F)
            this.faces.addAll(
                0, 0, 2, 0, 1, 0,
                1, 0, 2, 0, 3, 0
            )
            this.points.addAll(points, 0, 12)
            this.faceSmoothingGroups.addAll(1, 1)
        }
        val endCapView = MeshView(endCap)
        children += endCapView

        for (i in 1 until residues.size) {
            val residue = residues[i]
            val lastResidue = residues[i - 1]
            val lastBeta = lastResidue.cBetaAtom
            val lastBetaMirror = lastBeta
                .position
                .subtract(residue.cAlphaAtom.position)
                .multiply(-1.0)
                .add(residue.cAlphaAtom.position)

            val currentBeta = residue.cBetaAtom
            val currentBetaMirrored = currentBeta
                .position
                .subtract(residue.cAlphaAtom.position)
                .multiply(-1.0)
                .add(residue.cAlphaAtom.position)

            val isCrossing = (lastBetaMirror
                .distance(currentBeta.position) +
                    lastBeta.position.distance(currentBetaMirrored)) <
                    (lastBetaMirror.distance(currentBetaMirrored) +
                            lastBeta.position.distance(currentBeta.position))
            direction = computeDirection(lastResidue, residue, isCrossing)
            val facePositions = (i - 1) * 6 * 8
            setPoints(i * 4 * 3, points, currentBeta.position, currentBetaMirrored, direction)

            if (isCrossing) {
                when (lastRef) {
                    "CBM" -> connectCBM(faces, facePositions, i)
                    else -> connectOther(faces, facePositions, i)
                }
            }
            println(smoothing.map { it })
            // Set smoothing
            // original topping
            smoothing[(i - 1) * 8] = 1 shl 1
            smoothing[(i - 1) * 8 + 1] = 1 shl 1
            // shifted topping
            smoothing[(i - 1) * 8 + 2] = 1 shl 1
            smoothing[(i - 1) * 8 + 3] = 1 shl 1
            //side 1
            smoothing[(i - 1) * 8 + 4] = 1 shl 2
            smoothing[(i - 1) * 8 + 5] = 1 shl 2
            //side 2
            smoothing[(i - 1) * 8 + 6] = 1 shl 2
            smoothing[(i - 1) * 8 + 7] = 1 shl 2
        }
        mesh.apply {
            this.points.addAll(*points)
            this.faces.addAll(*faces)
            this.texCoords.addAll(*texArray)
            this.faceSmoothingGroups.addAll(*smoothing)
        }
        val meshView = MeshView(mesh)
        meshView.drawMode = DrawMode.FILL
        meshView.material = material

        children.add(meshView)

        createArrowHead(
            children,
            points, material,
            residues[residues.size - 2],
            residues[residues.size - 1]
        )
    }

    private fun createArrowHead(
        children: ObservableList<Node>,
        points: FloatArray,
        material: PhongMaterial,
        secondLast: Residue,
        last: Residue
    ) {
        val lastCAlpha = secondLast.cAlphaAtom
        val cAlpha = last.cAlphaAtom

        val direction = cAlpha.position
            .subtract(lastCAlpha.position)
            .multiply(0.5)

        val newPoints = FloatArray(10 * 3)
        arraycopy(points, points.size - 12, newPoints, 0, 12)

        fun point(index: Int) =
            newPoints[index].toDouble()

        fun point3d(start: Int) =
            Point3D(point(start), point(start + 1), point(start + 2))

        val cBeta = point3d(0)
        val cBetaMirrored = point3d(3)
        val cbetaShift = point3d(6)
        val cBetaShitMirrored = point3d(9)
        val cAlphaShift = cbetaShift.midpoint(cBetaShitMirrored)

        val outerDirection = cBeta.subtract(cAlpha.position)
        val outerPoint = cBeta.add(outerDirection)
        val outerPointShifted = cbetaShift.add(outerDirection)

        val outerMirrored = cBetaMirrored.add(outerDirection.multiply(-1.0))
        val outerMirroredShifted = cBetaShitMirrored.add(outerDirection.multiply(-1.0))
        val arrowTip = cAlpha.position.add(direction)
        val arrowTipShift = cAlphaShift.add(direction)

        fun setPoints(start: Int, point: Point3D) {
            newPoints[start] = point.x.toFloat()
            newPoints[start + 1] = point.y.toFloat()
            newPoints[start + 2] = point.z.toFloat()
        }

        setPoints(12, outerPoint)
        setPoints(15, outerPointShifted)
        setPoints(18, outerMirrored)
        setPoints(21, outerMirroredShifted)
        setPoints(24, arrowTip)
        setPoints(27, arrowTipShift)

        val faces = IntArray(10 * 6)
        when (lastRef) {
            "CBM" -> cbmFace(faces)
            else -> cbface(faces)
        }
        val arrowHead = triangleMesh()
        with(arrowHead) {
            this.points.addAll(*newPoints)
            this.faces.addAll(*faces)
            this.faceSmoothingGroups.addAll(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        }
        val arrowHeadMeshView = MeshView(arrowHead)
        arrowHeadMeshView.material = material
        children.add(arrowHeadMeshView)
    }

    private fun triangleMesh() = TriangleMesh(VertexFormat.POINT_TEXCOORD)

    private fun cbface(faces: IntArray) {
        // Last ref was CBeta therfore take the same faces as above, but tunr them (take the other side, by
        // flipping the las two numbers in each face)
        // to arrow side
        faces[0] = 0
        faces[2] = 5
        faces[4] = 2

        faces[6] = 0
        faces[8] = 4
        faces[10] = 5

        //to mirrored arrowside
        faces[12] = 1
        faces[14] = 3
        faces[16] = 6

        faces[18] = 3
        faces[20] = 7
        faces[22] = 6

        // to arrow tip
        faces[24] = 4
        faces[26] = 8
        faces[28] = 5

        faces[30] = 5
        faces[32] = 8
        faces[34] = 9

        //to arrow tip mirrored

        faces[36] = 6
        faces[38] = 7
        faces[40] = 9

        faces[42] = 9
        faces[44] = 8
        faces[46] = 6

        //Arrow top (shifted)
        faces[48] = 5
        faces[50] = 9
        faces[52] = 7

        //Arrow lower
        faces[54] = 6
        faces[56] = 8
        faces[58] = 4
    }

    private fun cbmFace(faces: IntArray) {
        faces[0] = 0
        faces[2] = 2
        faces[4] = 5

        faces[6] = 0
        faces[8] = 5
        faces[10] = 4

        //to mirrored arrowside
        faces[12] = 1
        faces[14] = 6
        faces[16] = 3

        faces[18] = 3
        faces[20] = 6
        faces[22] = 7

        // to arrow tip
        faces[24] = 4
        faces[26] = 5
        faces[28] = 8

        faces[30] = 5
        faces[32] = 9
        faces[34] = 8

        //to arrow tip mirrored

        faces[36] = 6
        faces[38] = 9
        faces[40] = 7

        faces[42] = 9
        faces[44] = 6
        faces[46] = 8

        //Arrow top (shifted)
        faces[48] = 5
        faces[50] = 7
        faces[52] = 9

        //Arrow lower
        faces[54] = 6
        faces[56] = 4
        faces[58] = 8
    }

    private fun connectOther(faces: IntArray, facePositions: Int, i: Int) {
        // First face connects sCB, sMCB and tCB
        faces[facePositions] = i * 4 - 4
        faces[facePositions + 2] = i * 4
        faces[facePositions + 4] = i * 4 - 3

        // Second face, connects sCB, tCB and tMCB
        faces[facePositions + 6] = i * 4 - 4
        faces[facePositions + 8] = i * 4 + 1
        faces[facePositions + 10] = i * 4

        // Shifted topping (in the following four faces each node is the shifted one)

        // First face connects sCB, sMCB and tCB
        faces[facePositions + 12] = i * 4 - 2
        faces[facePositions + 14] = i * 4 - 1
        faces[facePositions + 16] = i * 4 + 2

        // Second face, connects sCB, tCB and tMCB
        faces[facePositions + 18] = i * 4 - 2
        faces[facePositions + 20] = i * 4 + 2
        faces[facePositions + 22] = i * 4 + 3

        //faces for the two sides:

        // first side, first triangle of two
        // source cb and shifted source cb with target mirrored cb (front and back)
        faces[facePositions + 24] = i * 4 - 4
        faces[facePositions + 26] = i * 4 - 2
        faces[facePositions + 28] = i * 4 + 1

        //second triangle completing the first side rectangle
        //target mirrored cb, source shifted cb target mirrored shifted cb
        faces[facePositions + 30] = i * 4 + 1
        faces[facePositions + 32] = i * 4 - 2
        faces[facePositions + 34] = i * 4 + 3


        //second side, first triangle of two
        //source mirrored cb, source shifted mirrored cb with target cb (front and back)
        faces[facePositions + 36] = i * 4 - 3
        faces[facePositions + 38] = i * 4
        faces[facePositions + 40] = i * 4 - 1

        //second triangle completing the second side rectangle
        // target cb, source shifted mirrored cb to target shifted cb
        faces[facePositions + 42] = i * 4
        faces[facePositions + 44] = i * 4 + 2
        faces[facePositions + 46] = i * 4 - 1
    }

    private fun connectCBM(faces: IntArray, facePositions: Int, i: Int) {
        // First face connects sCB, sMCB and tCB
        faces[facePositions] = i * 4 - 4
        faces[facePositions + 2] = i * 4 - 3
        faces[facePositions + 4] = i * 4

        // Second face, connects sCB, tCB and tMCB
        faces[facePositions + 6] = i * 4 - 4
        faces[facePositions + 8] = i * 4
        faces[facePositions + 10] = i * 4 + 1

        // Shifted topping (in the following four faces each node is the shifted one)

        // First face connects sCB, sMCB and tCB
        faces[facePositions + 12] = i * 4 - 2
        faces[facePositions + 14] = i * 4 + 2
        faces[facePositions + 16] = i * 4 - 1

        // Second face, connects sCB, tCB and tMCB
        faces[facePositions + 18] = i * 4 - 2
        faces[facePositions + 20] = i * 4 + 3
        faces[facePositions + 22] = i * 4 + 2

        //faces for the two sides:

        // first side, first triangle of two
        // source cb and shifted source cb with target mirrored cb (front and back)
        faces[facePositions + 24] = i * 4 - 4
        faces[facePositions + 26] = i * 4 + 1
        faces[facePositions + 28] = i * 4 - 2

        //second triangle completing the first side rectangle
        //target mirrored cb, source shifted cb target mirrored shifted cb
        faces[facePositions + 30] = i * 4 + 1
        faces[facePositions + 32] = i * 4 + 3
        faces[facePositions + 34] = i * 4 - 2


        //second side, first triangle of two
        //source mirrored cb, source shifted mirrored cb with target cb (front and back)
        faces[facePositions + 36] = i * 4 - 3
        faces[facePositions + 38] = i * 4 - 1
        faces[facePositions + 40] = i * 4

        //second triangle completing the second side rectangle
        // target cb, source shifted mirrored cb to target shifted cb
        faces[facePositions + 42] = i * 4
        faces[facePositions + 44] = i * 4 - 1
        faces[facePositions + 46] = i * 4 + 2
    }

    private fun computeDirection(source: Residue, target: Residue, crossing: Boolean): Point3D {
        val sourceAlpha = source.cAlphaAtom

        val targetAlpha = target.cAlphaAtom
        val targetBeta = target.cBetaAtom
        val targetMirrorBeta = targetBeta
            .position
            .subtract(target.cAlphaAtom.position)
            .multiply(-1.0)
            .add(target.cAlphaAtom.position)

        val dest = sourceAlpha.position.subtract(targetAlpha.position)
        val ref: Point3D

        when {
            crossing -> when (lastRef) {
                "CB" -> {
                    ref = targetMirrorBeta.subtract(targetAlpha.position)
                    lastRef = "CBM"
                }
                else -> {
                    ref = targetBeta.position.subtract(targetAlpha.position)
                    lastRef = "CB"
                }
            }

            else -> ref = when (lastRef) {
                "CB" -> targetBeta.position.subtract(targetAlpha.position)
                else -> targetMirrorBeta.subtract(targetAlpha.position)

            }
        }

        return dest
            .crossProduct(ref)
            .normalize()
            .multiply(sourceAlpha.position.distance(targetAlpha.position))
            .multiply(BETA_SHEET_DEPTH)

    }

    private fun setPoints(
        idx: Int,
        points: FloatArray,
        cBeta: Point3D,
        mirroredCBeta: Point3D,
        directionShift: Point3D
    ) {
        // Set C Beta
        points[idx] = cBeta.x.toFloat()
        points[idx + 1] = cBeta.y.toFloat()
        points[idx + 2] = cBeta.z.toFloat()
        // Set mirrored C Beta
        points[idx + 3] = mirroredCBeta.x.toFloat()
        points[idx + 4] = mirroredCBeta.y.toFloat()
        points[idx + 5] = mirroredCBeta.z.toFloat()
        // Set shifted C Beta. (Shift both points in order to have a plane depth -> 3D effect)
        points[idx + 6] = cBeta.add(directionShift).x.toFloat()
        points[idx + 7] = cBeta.add(directionShift).y.toFloat()
        points[idx + 8] = cBeta.add(directionShift).z.toFloat()
        // Set shifted mirrored C Beta
        points[idx + 9] = mirroredCBeta.add(directionShift).x.toFloat()
        points[idx + 10] = mirroredCBeta.add(directionShift).y.toFloat()
        points[idx + 11] = mirroredCBeta.add(directionShift).z.toFloat()
    }

    private fun makeAlphaHelix() {
        val children = view.root.children
        val first = structure.first()
        val last = structure.last()
        children += LineView(
            Line(first.nAtom.position, last.nAtom.position),
            radiusProperty,
            color
        ).root
    }

    companion object {
        const val BETA_SHEET_DEPTH = 0.2
        var lastRef: String? = null
    }
}

class SecondaryStructureView(val structure: SecondaryStructure) : View("Secondary Structure") {
    override val scope: Scope = Scope()
    val controller = SecondaryStructureController(structure, scope)

    override val root = group {
        setInScope(this@SecondaryStructureView, scope)
        setInScope(controller, scope)
        userData = controller
    }
}