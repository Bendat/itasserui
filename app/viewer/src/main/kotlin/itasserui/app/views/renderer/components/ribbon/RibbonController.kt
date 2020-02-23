@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.app.views.renderer.components.ribbon

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import itasserui.lib.pdb.parser.Peptide
import itasserui.lib.pdb.parser.Residue
import itasserui.lib.pdb.parser.ResidueStub
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.DrawMode
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat.POINT_TEXCOORD
import tornadofx.*

class RibbonController(val residue: Residue, override val scope: Scope) : Controller() {
    val view: RibbonFragment by inject()
    val sourceProperty: Property<Peptide> = SimpleObjectProperty(ResidueStub)
    var source: Peptide by sourceProperty
    val targetProperty: Property<Peptide> = SimpleObjectProperty()
    var target: Peptide by targetProperty
    val meshProperty: Property<TriangleMesh> = TriangleMesh(POINT_TEXCOORD).toProperty()
    var mesh by meshProperty

    init {
        if (lastResidue is Some) {
            val lr = lastResidue as Some
            source = lr.t
            target = residue
            SimpleDoubleProperty()
            lastResidue.map { lastResidue ->
                val sourceAlpha = lastResidue.cAlphaAtom.position
                val sourceBeta = lastResidue.cBetaAtom.position
                val sourceMirrorBeta = sourceBeta
                    .subtract(sourceAlpha)
                    .multiply(-1.0)
                    .add(sourceAlpha)

                val targetAlpha = residue.cAlphaAtom.position
                val targetBeta = residue.cBetaAtom.position
                val targetMirrorBeta = targetBeta
                    .subtract(targetAlpha)
                    .multiply(-1.0)
                    .add(targetAlpha)

                val points = floatArrayOf(
                    sourceBeta.x.toFloat(),
                    sourceBeta.y.toFloat(),
                    sourceBeta.z.toFloat(),
                    sourceMirrorBeta.x.toFloat(),
                    sourceMirrorBeta.y.toFloat(),
                    sourceMirrorBeta.z.toFloat(),
                    targetBeta.x.toFloat(),
                    targetBeta.y.toFloat(),
                    targetBeta.z.toFloat(),
                    targetMirrorBeta.x.toFloat(),
                    targetMirrorBeta.y.toFloat(),
                    targetMirrorBeta.z.toFloat()
                )

                val texArray = floatArrayOf(0f, 0f)


                // Solve minimization problem in order to connect source's and target's c betas or source's cbeta and target's mirrored c beta
                // This unwinds the planes in alphahelices significantly.
                // This is when mirrored and unmirrored  point wil each be the outer edge
                // This is when the two mirrored and the two unmirrored points will be the outer edges
                val condtion = (sourceMirrorBeta.distance(targetBeta) +
                        sourceBeta.distance(targetMirrorBeta)) <
                        (sourceMirrorBeta.distance(targetMirrorBeta) +
                                sourceBeta.distance(targetBeta))
                val faces: IntArray =
                    if (condtion) intArrayOf(
                        0, 0, 1, 0, 2, 0, // First face connects sCB, sMCB and tCB
                        0, 0, 2, 0, 1, 0, // The first face's back
                        // (in order to be visible from both sides (when rotating)), connects sCB, tCB and sMCB
                        0, 0, 2, 0, 3, 0, // Second face, connects sCB, tCB and tMCB
                        0, 0, 3, 0, 2, 0  // Second face's back (same as above), connects sCB, tMCB and tCB
                    ) else intArrayOf(
                        0, 0, 1, 0, 3, 0, // First face connects sCB, sMCB and tMCB
                        0, 0, 3, 0, 1, 0, // The first face's back
                        // (in order to be visible from both sides (when rotating)), connects sCB, tMCB and sMCB
                        0, 0, 3, 0, 2, 0, // Second face, connects sCB, tMCB and tCB
                        0, 0, 2, 0, 3, 0  // Second face's back (same as above), connects sCB, tCB and tMCB
                    )

                val smoothing = intArrayOf(1, 2, 1, 2)

                mesh.points.addAll(*points)
                mesh.faces.addAll(*faces)
                mesh.texCoords.addAll(*texArray)
                mesh.faceSmoothingGroups.addAll(*smoothing)
                val meshView = MeshView(mesh)
                meshView.drawMode = DrawMode.FILL
                val mat = PhongMaterial(Color.MEDIUMAQUAMARINE)
                mat.specularColor = Color.MEDIUMAQUAMARINE.brighter()
                meshView.material = mat
                view.root.children.add(meshView)
            }

            lastResidue = Some(residue)
        }

    }

    companion object {
        var lastResidue: Option<Residue> = None
        @Suppress("unused")
        fun reset() {
            lastResidue = None
        }
    }

}


