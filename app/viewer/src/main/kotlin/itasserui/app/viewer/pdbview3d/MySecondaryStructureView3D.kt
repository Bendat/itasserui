package itasserui.app.viewer.pdbview3d

import itasserui.app.viewer.pdbmodel.Residue
import itasserui.app.viewer.pdbmodel.SecondaryStructure
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.DrawMode
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat

/**
 * Allows for a cartoon-like display of secondary structures. Which can be both alphahelices or betasheets.
 */
internal class MySecondaryStructureView3D
/**
 * Create a view of a secondary structure.
 *
 * @param structure The model structure for which a view structure should be created.
 */
    (var structure: SecondaryStructure) : Group() {
    var radius: DoubleProperty = SimpleDoubleProperty()
    var color: ObjectProperty<Color> = SimpleObjectProperty()
    var wasComputed: Boolean = false
    var listOfResidues: List<Residue> = structure.residuesContained

    fun wasComputed(): Boolean {
        return wasComputed
    }

    fun compute() {
        if (wasComputed) {
            this.children.clear()
        }
        wasComputed = true
        if (structure.secondaryStructureType == SecondaryStructure.StructureType.betasheet) {
            // structure is alpha helix
            val mesh = TriangleMesh(VertexFormat.POINT_TEXCOORD)

            val material = PhongMaterial(Color.CORNFLOWERBLUE)
            material.specularColor = Color.CORNFLOWERBLUE.brighter()

            // set up the mesh arrays

            // 3 coords per point four points per residue
            val points = FloatArray(listOfResidues.size * 3 * 4)
            val texArray = floatArrayOf(0f, 0f)

            // Six ints per face, eight triangles per residue
            // (top rectangle, bottom rectangle and two sides of the depth of the two rectangles).
            val faces = IntArray(listOfResidues.size * 6 * 8)

            // and eight triangles per residue
            val smoothing = IntArray(listOfResidues.size * 8)

            // Compute the direction of shifting for 3D beta sheet. Need the second residue for determining
            // which direction to shift the initial coordinates. For the following residues the for loop
            // will do that based on its context.
            val first = listOfResidues[0]
            val second = listOfResidues[1]
            val tarCA = getCAlpha(second)
            val sourCA = getCAlpha(first)
            val sourMirCB = getMirroredCBeta(first)
            // Subtract position vector of source C beta and target c alpha. then compute their crossproduct. Normalize the resulting vector and
            // multiply it by 0.3 times the length of the C-C bond
            var direction = tarCA.subtract(sourCA).crossProduct(sourMirCB.subtract(sourCA)).normalize()
                .multiply(sourCA.distance(sourMirCB)).multiply(BETA_SHEET_DEPTH)
            lastRef = "CB"


            // Set first source coordinates. This is important since otherwise for first element there is nothing to connect.
            val initialBeta = getCBeta(first)
            val initialMirrorBeta = getMirroredCBeta(first)
            setPoints(0, points, initialBeta, initialMirrorBeta, direction)

            // We need to take care of the open end in the sheet at the beginning (first residue between c beta,
            // mirrored cbeta, shifted cbeta and shifted mirrored cbeta
            val endCap = TriangleMesh(VertexFormat.POINT_TEXCOORD)
            endCap.texCoords.addAll(0.0F, 0.0F)
            endCap.faces.addAll(
                0, 0, 2, 0, 1, 0,
                1, 0, 2, 0, 3, 0
            )
            //Copy start points from other mesh
            endCap.points.addAll(points, 0, 12)
            endCap.faceSmoothingGroups.addAll(1, 1)
            val endCapView = MeshView(endCap)
            endCapView.material = material
            this.children.add(endCapView)

            for (i in 1 until listOfResidues.size) {
                val residue = listOfResidues[i]
                val lastResidue = listOfResidues[i - 1]
                // We need those for computation which points to connect.
                val lastBeta = getCBeta(lastResidue)
                val lastMirrorBeta = getMirroredCBeta(lastResidue)

                // These are the currently important two points. They will be connected with the last ones by two triangles
                val currentBeta = getCBeta(residue)
                val currentMirrorBeta = getMirroredCBeta(residue)


                // Solve minimization problem in order to connect source's and target's c betas or source's cbeta and target's mirrored c beta
                // This unwinds the planes in alpha helices significantly.
                val crossing =
                    lastMirrorBeta.distance(currentBeta) + lastBeta.distance(currentMirrorBeta) < lastMirrorBeta.distance(
                        currentMirrorBeta
                    ) + lastBeta.distance(currentBeta)

                //Get the shifting direction vector for this residue's atoms
                direction = computeDirection(lastResidue, residue, crossing)

                // Set the current residues coordinates as points (the last residues points were set in the
                // last step (or for the first element in the initialization step)
                // four points per residue 3 coordinates per point
                setPoints(i * 4 * 3, points, currentBeta, currentMirrorBeta, direction)

                val positionInFaces = (i - 1) * 6 * 8
                if (crossing) {
                    // Only use position 0, +2, +4, ... for the faces to link to point (the other 3 points are texcoords
                    // which are by default initialized with 0 which is deterministic behaviour in Java and exactly
                    // what we want.

                    if (lastRef == "CBM") {
                        //Original topping

                        // First face connects sCB, sMCB and tCB
                        faces[positionInFaces] = i * 4 - 4
                        faces[positionInFaces + 2] = i * 4 - 3
                        faces[positionInFaces + 4] = i * 4

                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 6] = i * 4 - 4
                        faces[positionInFaces + 8] = i * 4
                        faces[positionInFaces + 10] = i * 4 + 1

                        // Shifted topping (in the following four faces each node is the shifted one)

                        // First face connects sCB, sMCB and tCB
                        faces[positionInFaces + 12] = i * 4 - 2
                        faces[positionInFaces + 14] = i * 4 + 2
                        faces[positionInFaces + 16] = i * 4 - 1

                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 18] = i * 4 - 2
                        faces[positionInFaces + 20] = i * 4 + 3
                        faces[positionInFaces + 22] = i * 4 + 2

                        //faces for the two sides:

                        // first side, first triangle of two
                        // source cb and shifted source cb with target mirrored cb (front and back)
                        faces[positionInFaces + 24] = i * 4 - 4
                        faces[positionInFaces + 26] = i * 4 + 1
                        faces[positionInFaces + 28] = i * 4 - 2

                        //second triangle completing the first side rectangle
                        //target mirrored cb, source shifted cb target mirrored shifted cb
                        faces[positionInFaces + 30] = i * 4 + 1
                        faces[positionInFaces + 32] = i * 4 + 3
                        faces[positionInFaces + 34] = i * 4 - 2


                        //second side, first triangle of two
                        //source mirrored cb, source shifted mirrored cb with target cb (front and back)
                        faces[positionInFaces + 36] = i * 4 - 3
                        faces[positionInFaces + 38] = i * 4 - 1
                        faces[positionInFaces + 40] = i * 4

                        //second triangle completing the second side rectangle
                        // target cb, source shifted mirrored cb to target shifted cb
                        faces[positionInFaces + 42] = i * 4
                        faces[positionInFaces + 44] = i * 4 - 1
                        faces[positionInFaces + 46] = i * 4 + 2

                    } else {

                        // First face connects sCB, sMCB and tCB
                        faces[positionInFaces] = i * 4 - 4
                        faces[positionInFaces + 2] = i * 4
                        faces[positionInFaces + 4] = i * 4 - 3

                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 6] = i * 4 - 4
                        faces[positionInFaces + 8] = i * 4 + 1
                        faces[positionInFaces + 10] = i * 4

                        // Shifted topping (in the following four faces each node is the shifted one)

                        // First face connects sCB, sMCB and tCB
                        faces[positionInFaces + 12] = i * 4 - 2
                        faces[positionInFaces + 14] = i * 4 - 1
                        faces[positionInFaces + 16] = i * 4 + 2

                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 18] = i * 4 - 2
                        faces[positionInFaces + 20] = i * 4 + 2
                        faces[positionInFaces + 22] = i * 4 + 3

                        //faces for the two sides:

                        // first side, first triangle of two
                        // source cb and shifted source cb with target mirrored cb (front and back)
                        faces[positionInFaces + 24] = i * 4 - 4
                        faces[positionInFaces + 26] = i * 4 - 2
                        faces[positionInFaces + 28] = i * 4 + 1

                        //second triangle completing the first side rectangle
                        //target mirrored cb, source shifted cb target mirrored shifted cb
                        faces[positionInFaces + 30] = i * 4 + 1
                        faces[positionInFaces + 32] = i * 4 - 2
                        faces[positionInFaces + 34] = i * 4 + 3


                        //second side, first triangle of two
                        //source mirrored cb, source shifted mirrored cb with target cb (front and back)
                        faces[positionInFaces + 36] = i * 4 - 3
                        faces[positionInFaces + 38] = i * 4
                        faces[positionInFaces + 40] = i * 4 - 1

                        //second triangle completing the second side rectangle
                        // target cb, source shifted mirrored cb to target shifted cb
                        faces[positionInFaces + 42] = i * 4
                        faces[positionInFaces + 44] = i * 4 + 2
                        faces[positionInFaces + 46] = i * 4 - 1
                    }


                } else {
                    // This is when the two mirrored and the two unmirrored points will be the outer edges

                    if (lastRef == "CBM") {
                        //Original topping

                        // Same as above, but with slightly different connecting the nodes.
                        // First face connects sCB, tMCB and sMCB
                        faces[positionInFaces] = i * 4 - 4
                        faces[positionInFaces + 2] = i * 4 + 1
                        faces[positionInFaces + 4] = i * 4 - 3
                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 6] = i * 4 - 4
                        faces[positionInFaces + 8] = i * 4
                        faces[positionInFaces + 10] = i * 4 + 1

                        // Shifted topping (in the following four faces each node is the shifted one)

                        // First face connects sCB, tMCB and sMCB
                        faces[positionInFaces + 12] = i * 4 - 2
                        faces[positionInFaces + 14] = i * 4 - 1
                        faces[positionInFaces + 16] = i * 4 + 3
                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 18] = i * 4 - 2
                        faces[positionInFaces + 20] = i * 4 + 3
                        faces[positionInFaces + 22] = i * 4 + 2


                        //faces for the two sides:

                        // first side, first triangle of two
                        // source cb and shifted source cb with target cb (front and back)
                        faces[positionInFaces + 24] = i * 4 - 4
                        faces[positionInFaces + 26] = i * 4 - 2
                        faces[positionInFaces + 28] = i * 4


                        //second triangle completing the first side rectangle
                        //target cb, source shifted cb target shifted cb
                        faces[positionInFaces + 30] = i * 4
                        faces[positionInFaces + 32] = i * 4 - 2
                        faces[positionInFaces + 34] = i * 4 + 2


                        //second side, first triangle of two
                        //source mirrored cb, source shifted mirrored cb with target mirrored cb (front and back)
                        faces[positionInFaces + 36] = i * 4 - 3
                        faces[positionInFaces + 38] = i * 4 + 1
                        faces[positionInFaces + 40] = i * 4 - 1


                        //second triangle completing the second side rectangle
                        // target mirrored cb, source shifted mirrored cb to target shifted mirrored cb
                        faces[positionInFaces + 42] = i * 4 + 1
                        faces[positionInFaces + 44] = i * 4 + 3
                        faces[positionInFaces + 46] = i * 4 - 1

                    } else {
                        // LAST REF WAS CB SO SIMPLE THING TO DO IS TO SWITCH THE ORDER OF THE LAST TWO COORDINATES OF
                        // EACH FACE IN ORDER TO SWITCH ITS ORIENTATION
                        //Original topping

                        // Same as above, but with slightly different connecting the nodes.
                        // First face connects sCB, tMCB and sMCB
                        faces[positionInFaces] = i * 4 - 4
                        faces[positionInFaces + 2] = i * 4 - 3
                        faces[positionInFaces + 4] = i * 4 + 1
                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 6] = i * 4 - 4
                        faces[positionInFaces + 8] = i * 4 + 1
                        faces[positionInFaces + 10] = i * 4

                        // Shifted topping (in the following four faces each node is the shifted one)

                        // First face connects sCB, tMCB and sMCB
                        faces[positionInFaces + 12] = i * 4 - 2
                        faces[positionInFaces + 14] = i * 4 + 3
                        faces[positionInFaces + 16] = i * 4 - 1
                        // Second face, connects sCB, tCB and tMCB
                        faces[positionInFaces + 18] = i * 4 - 2
                        faces[positionInFaces + 20] = i * 4 + 2
                        faces[positionInFaces + 22] = i * 4 + 3


                        //faces for the two sides:

                        // first side, first triangle of two
                        // source cb and shifted source cb with target cb (front and back)
                        faces[positionInFaces + 24] = i * 4 - 4
                        faces[positionInFaces + 26] = i * 4
                        faces[positionInFaces + 28] = i * 4 - 2


                        //second triangle completing the first side rectangle
                        //target cb, source shifted cb target shifted cb
                        faces[positionInFaces + 30] = i * 4
                        faces[positionInFaces + 32] = i * 4 + 2
                        faces[positionInFaces + 34] = i * 4 - 2


                        //second side, first triangle of two
                        //source mirrored cb, source shifted mirrored cb with target mirrored cb (front and back)
                        faces[positionInFaces + 36] = i * 4 - 3
                        faces[positionInFaces + 38] = i * 4 - 1
                        faces[positionInFaces + 40] = i * 4 + 1


                        //second triangle completing the second side rectangle
                        // target mirrored cb, source shifted mirrored cb to target shifted mirrored cb
                        faces[positionInFaces + 42] = i * 4 + 1
                        faces[positionInFaces + 44] = i * 4 - 1
                        faces[positionInFaces + 46] = i * 4 + 3
                    }


                }

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

            // Set the necessary arrays for the full mesh of the beta sheet.
            mesh.points.addAll(*points)
            mesh.faces.addAll(*faces)
            mesh.texCoords.addAll(*texArray)
            mesh.faceSmoothingGroups.addAll(*smoothing)
            // Convert as mesh view in order to have a node to add to the scene graph
            val meshView = MeshView(mesh)
            meshView.drawMode = DrawMode.FILL
            meshView.material = material

            this.children.add(meshView)

            createArrowHead(
                points, material,
                listOfResidues[listOfResidues.size - 2],
                listOfResidues[listOfResidues.size - 1]
            )

        } else {
            // structure is alphahelix. Simple case.
            radius = SimpleDoubleProperty(20.0)
            color = SimpleObjectProperty(Color.RED)
            val start = structure.firstResidue
            val end = structure.lastResidue
            // Start alphahelix from the starting residue's N atom and end at ending residue's C atom -> draw 3D line
            val shape = MyLine3D(
                start.nAtom!!.xCoordinateProperty,
                start.nAtom!!.yCoordinateProperty,
                start.nAtom!!.zCoordinateProperty,
                end.cAlphaAtom!!.xCoordinateProperty,
                end.cAlphaAtom!!.yCoordinateProperty,
                end.cAlphaAtom!!.zCoordinateProperty,
                radius,
                color
            )
            this.children.add(shape)
        }
    }

    /**
     * At the last residue of the beta sheet create a mesh structure which is pointing its direction in form of an
     * arrowhead.
     *
     * @param points     The points array of the mesh structure. The last four points are taken from there in order to
     * get the starting points for the arrow.
     * @param material   The material which will be set to the mesh structure.
     * @param secondLast The residue before last. Used to get the direction of the arrow.
     * @param last       The last residue in the structure. Used to get the direction of the arrow (using the C alphas).
     */
    private fun createArrowHead(points: FloatArray, material: PhongMaterial, secondLast: Residue, last: Residue) {
        val lastCAlpha = Point3D(
            secondLast.cAlphaAtom!!.xCoordinateProperty.get(),
            secondLast.cAlphaAtom!!.yCoordinateProperty.get(),
            secondLast.cAlphaAtom!!.zCoordinateProperty.get()
        )
        val cAlpha = Point3D(
            last.cAlphaAtom!!.xCoordinateProperty.get(),
            last.cAlphaAtom!!.yCoordinateProperty.get(),
            last.cAlphaAtom!!.zCoordinateProperty.get()
        )
        val direction = cAlpha.subtract(lastCAlpha).multiply(0.5) // calpha - lastCalpha ^= lastCalpha -> calpha

        val newPoints = FloatArray(10 * 3) // 10 points a 3 coordinates
        // Points 0:cbeta, 1:cbeta mirrored, 2: cbeta shifted, 3:cbeta mirrored shifted
        System.arraycopy(points, points.size - 12, newPoints, 0, 12)
        val cBeta = Point3D(newPoints[0].toDouble(), newPoints[1].toDouble(), newPoints[2].toDouble())
        val cBetaMir = Point3D(newPoints[3].toDouble(), newPoints[4].toDouble(), newPoints[5].toDouble())
        val cBetaShif = Point3D(newPoints[6].toDouble(), newPoints[7].toDouble(), newPoints[8].toDouble())
        val cBetaShifMir = Point3D(newPoints[9].toDouble(), newPoints[10].toDouble(), newPoints[11].toDouble())
        val cAlphaShifted = cBetaShif.midpoint(cBetaShifMir)

        val outerDirection = cBeta.subtract(cAlpha)


        val outerPoint = cBeta.add(outerDirection)
        val outerPointShifted = cBetaShif.add(outerDirection)

        val outerMirroredPoint = cBetaMir.add(outerDirection.multiply(-1.0))
        val outerMirroredPointShifted = cBetaShifMir.add(outerDirection.multiply(-1.0))

        val arrowTip = cAlpha.add(direction)
        val arrowTipShifted = cAlphaShifted.add(direction)

        //  arrow side, Point 4
        newPoints[12] = outerPoint.x.toFloat()
        newPoints[13] = outerPoint.y.toFloat()
        newPoints[14] = outerPoint.z.toFloat()
        // arrow side shifted, point 5
        newPoints[15] = outerPointShifted.x.toFloat()
        newPoints[16] = outerPointShifted.y.toFloat()
        newPoints[17] = outerPointShifted.z.toFloat()
        //arrow mirrored side, point 6
        newPoints[18] = outerMirroredPoint.x.toFloat()
        newPoints[19] = outerMirroredPoint.y.toFloat()
        newPoints[20] = outerMirroredPoint.z.toFloat()
        // arrow mirrored side shifted, point 7
        newPoints[21] = outerMirroredPointShifted.x.toFloat()
        newPoints[22] = outerMirroredPointShifted.y.toFloat()
        newPoints[23] = outerMirroredPointShifted.z.toFloat()

        //arrow tip, point 8
        newPoints[24] = arrowTip.x.toFloat()
        newPoints[25] = arrowTip.y.toFloat()
        newPoints[26] = arrowTip.z.toFloat()
        // arrow tip shifted, point 9
        newPoints[27] = arrowTipShifted.x.toFloat()
        newPoints[28] = arrowTipShifted.y.toFloat()
        newPoints[29] = arrowTipShifted.z.toFloat()

        // Define the faces
        val faces = IntArray(10 * 6) // Ten faces: 4 for  tip sides, four for each outer and two for upper and lower
        if (lastRef == "CBM") {
            // to arrow side
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
        } else {
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

        val arrowHead = TriangleMesh(VertexFormat.POINT_TEXCOORD)
        arrowHead.points.addAll(*newPoints)
        arrowHead.faces.addAll(*faces)
        arrowHead.texCoords.addAll(0.0F, 0.0F)
        arrowHead.faceSmoothingGroups.addAll(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val arrowHeadMeshView = MeshView(arrowHead)
        arrowHeadMeshView.material = material
        this.children.add(arrowHeadMeshView)
    }

    /**
     * Set points in the points array for the triangle mesh.
     *
     * @param idx            The index in the points array where to put the points.
     * @param points         The points array which will be used to store the points in. Will be in the form for the
     * triangle mesh structure.
     * @param cBeta          The c Beta point to put in.
     * @param mirroredCBeta  The mirrored point of C beta (should be mirrored at the residue's c alpha.
     * @param directionShift The shift direction (only the direction, not the location vector) with appropriate length
     * where to put the shifted c beta and shifted mirrored c beta.
     */
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

    /**
     * Get the direction (with appropriate length) to shift the target's points (C alpha, C beta and mirrored C beta)
     * in 3D space in order to create points to reference to in order to get a beta sheet with a depth.
     *
     * @param source   the last residue
     * @param target   The residue to get the shift for.
     * @param crossing Are the two C Betas of the two residues on 'the same side of the backbone' or crossing. Crossing
     * means that Cbeta of the source is connected to mirrored C Beta of the target and vice versa for
     * mirrored CBeta of the source.
     * @return The direction to shift the three reference points of the target by.
     */
    private fun computeDirection(source: Residue, target: Residue, crossing: Boolean): Point3D {
        val result: Point3D
        val sourceAlpha = getCAlpha(source)

        // Especially for when the residue is 'crossing' we need to switch the crossproduct's direction by -1
        // or referencing the mirrored CB instead of CB
        val targetAlpha = getCAlpha(target)
        val targetBeta = getCBeta(target)
        val targetMirrorBeta = getMirroredCBeta(target)

        val dest = sourceAlpha.subtract(targetAlpha)
        val ref: Point3D

        // Depending on the last reference point (either C beta or CBeta mirrored) we need to use the correct one
        // in order to get the shift right and not crossing.
        if (crossing) {
            if (lastRef == "CB") {
                // Last ref was CBeta so we need to take the mirrored target CB point since we are 'crossing'
                ref = targetMirrorBeta.subtract(targetAlpha)
                lastRef = "CBM"
            } else {
                ref = targetBeta.subtract(targetAlpha)
                lastRef = "CB"
            }
        } else {
            if (lastRef == "CB") {
                // Last ref was CBeta so we need to take the CBeta point fo the target since we are NOT 'crossing'
                ref = targetBeta.subtract(targetAlpha)
            } else {
                ref = targetMirrorBeta.subtract(targetAlpha)
            }
        }
        // Get the perpendicular vectore of the CB CB mirrored line and the CAlpha source CALpha target line -> this is the shift
        // But always neccessaryly in the correct direction.
        result =
            dest.crossProduct(ref).normalize().multiply(sourceAlpha.distance(targetAlpha)).multiply(BETA_SHEET_DEPTH)
        return result
    }

    /**
     * Get the mirrored point of the given residue's c beta.
     *
     * @param residue Reference residue
     * @return Point which is C beta mirrored at C alpha.
     */
    private fun getMirroredCBeta(residue: Residue): Point3D {
        val alpha = getCAlpha(residue)
        val beta = getCBeta(residue)
        return beta.subtract(alpha).multiply(-1.0).add(alpha)
    }

    /**
     * Get a points C alpha in 3D space.
     *
     * @param residue The residue to get C alpha from.
     * @return Position in 3D space of C alpha of the given residue.
     */
    private fun getCAlpha(residue: Residue): Point3D {
        return Point3D(
            residue.cAlphaAtom!!.xCoordinateProperty.get(),
            residue.cAlphaAtom!!.yCoordinateProperty.get(),
            residue.cAlphaAtom!!.zCoordinateProperty.get()
        )
    }

    /**
     * Get the point in 3D space of C Beta of the given residue.
     *
     * @param residue The residue to get C beta from.
     * @return Position in 3D space of C beta of the given residue.
     */
    private fun getCBeta(residue: Residue): Point3D {
        return Point3D(
            residue.cBetaAtom!!.xCoordinateProperty.get(),
            residue.cBetaAtom!!.yCoordinateProperty.get(),
            residue.cBetaAtom!!.zCoordinateProperty.get()
        )
    }

    companion object {
        val BETA_SHEET_DEPTH = 0.2
        var lastRef: String? = null
    }
}
