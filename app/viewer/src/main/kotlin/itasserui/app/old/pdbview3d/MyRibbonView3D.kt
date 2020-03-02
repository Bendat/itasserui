package itasserui.app.old.pdbview3d

import itasserui.app.old.pdbmodel.Residue
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.DrawMode
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat

/**
 * Mesh wrapper for ribbon view of residues.
 */
class MyRibbonView3D internal constructor(residue: Residue) : Group() {
    var modelSource: Residue? = null
    var modelTarget: Residue? = null
    var mesh: TriangleMesh? = null

    init {
        if (MyRibbonView3D.lastResidue != null) {
            this.modelSource = lastResidue
            this.modelTarget = residue
            val sourceAlpha = Point3D(
                lastResidue!!.cAlphaAtom!!.xCoordinateProperty.get(),
                lastResidue!!.cAlphaAtom!!.yCoordinateProperty.get(),
                lastResidue!!.cAlphaAtom!!.zCoordinateProperty.get()
            )
            val sourceBeta = Point3D(
                lastResidue!!.cBetaAtom!!.xCoordinateProperty.get(),
                lastResidue!!.cBetaAtom!!.yCoordinateProperty.get(),
                lastResidue!!.cBetaAtom!!.zCoordinateProperty.get()
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
            mesh?.points?.addAll(*points)
            mesh?.faces?.addAll(*faces)
            mesh?.texCoords?.addAll(*texArray)
            mesh?.faceSmoothingGroups?.addAll(*smoothing)

            val meshView = MeshView(mesh)
            meshView.drawMode = DrawMode.FILL

            val mat = PhongMaterial(Color.MEDIUMAQUAMARINE)
            mat.specularColor = Color.MEDIUMAQUAMARINE.brighter()
            meshView.material = mat
            this.children.add(meshView)
        }

        lastResidue = residue
    }

    companion object {
        internal var lastResidue: Residue? = null

        fun reset() {
            lastResidue = null
        }
    }
}
