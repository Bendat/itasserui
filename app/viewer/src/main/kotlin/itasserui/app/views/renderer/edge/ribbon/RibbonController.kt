@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.app.views.renderer.edge.ribbon

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import itasserui.app.viewer.pdbmodel.Residue
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point3D
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.DrawMode
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat
import tornadofx.getValue
import tornadofx.setValue

class RibbonController(val residue: Residue) {
    companion object {
        var lastResidue: Option<Residue> = None
        fun reset() {
            lastResidue = None
        }
    }

    val sourceProperty: ObjectProperty<Residue> = SimpleObjectProperty()
    var source by sourceProperty
    val targetProperty: ObjectProperty<Residue> = SimpleObjectProperty()
    var target by targetProperty
    val meshProperty: ObjectProperty<TriangleMesh> = SimpleObjectProperty()
    var mesh by meshProperty

    init {
        arrayOf<Int>().map {  }
        if (lastResidue is Some) {
            val lr = lastResidue as Some
            source = lr.t
            target = residue
            SimpleDoubleProperty()
            lastResidue.map { residue ->
                val sourceAlpha = Point3D(
                    residue.cAlphaAtom!!.xCoordinate,
                    residue.cAlphaAtom!!.yCoordinate,
                    residue.cAlphaAtom!!.zCoordinate
                )

                val sourceBeta = Point3D(
                    residue.cBetaAtom!!.xCoordinate,
                    residue.cBetaAtom!!.yCoordinate,
                    residue.cBetaAtom!!.zCoordinate
                )
                val sourceMirrorBeta = sourceBeta.subtract(sourceAlpha).multiply(-1.0).add(sourceAlpha)
                val targetAlpha = Point3D(
                    residue.cAlphaAtom!!.xCoordinateProperty.get(),
                    residue.cAlphaAtom!!.yCoordinateProperty.get(),
                    residue.cAlphaAtom!!.zCoordinateProperty.get()
                )
                val targetBeta = Point3D(
                    residue.cBetaAtom!!.xCoordinateProperty.get(),
                    residue.cBetaAtom!!.yCoordinateProperty.get(),
                    residue.cBetaAtom!!.zCoordinateProperty.get()
                )
                val targetMirrorBeta = targetBeta.subtract(targetAlpha).multiply(-1.0).add(targetAlpha)
                mesh = TriangleMesh(VertexFormat.POINT_TEXCOORD)

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

                val faces: IntArray

                // Solve minimization problem in order to connect source's and target's c betas or source's cbeta and target's mirrored c beta
                // This unwinds the planes in alphahelices significantly.
                if (sourceMirrorBeta.distance(targetBeta) + sourceBeta.distance(targetMirrorBeta) < sourceMirrorBeta.distance(
                        targetMirrorBeta
                    ) + sourceBeta.distance(targetBeta)
                ) {
                    // This is when mirrored and unmirrored  point wil each be the outer edge
                    faces = intArrayOf(
                        0, 0, 1, 0, 2, 0, // First face connects sCB, sMCB and tCB
                        0, 0, 2, 0, 1, 0, // The first face's back
                        // (in order to be visible from both sides (when rotating)), connects sCB, tCB and sMCB
                        0, 0, 2, 0, 3, 0, // Second face, connects sCB, tCB and tMCB
                        0, 0, 3, 0, 2, 0  // Second face's back (same as above), connects sCB, tMCB and tCB
                    )
                } else {
                    // This is when the two mirrored and the two unmirrored points will be the outer edges
                    faces = intArrayOf(
                        0, 0, 1, 0, 3, 0, // First face connects sCB, sMCB and tMCB
                        0, 0, 3, 0, 1, 0, // The first face's back
                        // (in order to be visible from both sides (when rotating)), connects sCB, tMCB and sMCB
                        0, 0, 3, 0, 2, 0, // Second face, connects sCB, tMCB and tCB
                        0, 0, 2, 0, 3, 0  // Second face's back (same as above), connects sCB, tCB and tMCB
                    )
                }

                val smoothing = intArrayOf(1, 2, 1, 2)

                mesh.points?.addAll(*points)
                mesh.faces?.addAll(*faces)
                mesh.texCoords?.addAll(*texArray)
                mesh.faceSmoothingGroups?.addAll(*smoothing)
                val meshView = MeshView(mesh)
                meshView.drawMode = DrawMode.FILL
                val mat = PhongMaterial(Color.MEDIUMAQUAMARINE)
                mat.specularColor = Color.MEDIUMAQUAMARINE.brighter()
                meshView.material = mat


            }

            lastResidue = Some(residue)
        }

    }
}
