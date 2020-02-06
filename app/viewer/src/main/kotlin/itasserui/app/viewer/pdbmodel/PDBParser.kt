package itasserui.app.viewer.pdbmodel

import itasserui.common.logger.Logger
import itasserui.lib.pdb.parser.Element
import itasserui.lib.pdb.parser.NormalizedAtom
import javafx.geometry.Point3D
import javafx.scene.transform.Rotate
import javafx.util.Pair
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Parser for PDB files.
 *
 * @author Patrick Grupp
 */
object PDBParser : Logger {

    private val ATOM_DISTANCE_FACTOR = 20

    private enum class Status {
        header, remarks, helix, betasheet, atom, term
    }

    /**
     * Parse the input in the given reader to the given pdbEntry model.
     *
     * @param pdbEntry The model to be written to.
     * @param reader   The reader with PDB entry conform information to be parsed.
     * @throws Exception If no nodes were added to the model until EOF. Or for any IOException.
     */
    @Throws(Exception::class)
    fun parse(pdbEntry: PDBEntry, reader: BufferedReader) {

        var curr: String
        // Here all atoms and secondary structures will be saved for later post processing in order to build up the
        // model, when all information is present
        val atomArrayList = ArrayList<Atom>()
        val helices = ArrayList<Pair<String, String>>()
        val betaSheets = ArrayList<Pair<String, String>>()
        var status = Status.header
        // Loop over the pdb file and parse it
        reader.readLines().forEach { curr ->
            status = processLine(curr, pdbEntry, atomArrayList, helices, betaSheets)
            if (status == Status.term)
                return@forEach

        }
        // Post process to build up an actual model of the protein described by the PDB file.
        val residues = postProcess(pdbEntry, atomArrayList, helices, betaSheets)
        // Get nice coordinate positions out of the file
        normalizeCoordinates(residues)
        pdbEntry.residues.addAll(residues)
        // Bond the atoms together in a correct way, since a PDB dous not give awa information about
        // how the atoms are connected
        setUpBonds(pdbEntry)
        info { "Atom is ${atomArrayList.last()}" }
        info { "Atom is ${atomArrayList.last().chemicalElement}" }
        info { "Atom is ${atomArrayList.last().residue.resNum}" }
        info { "Helix is ${helices}" }
        info { "beta is ${betaSheets}" }
        info { "residue is ${residues.last()}" }
        // Something went wrong, could not parse any nodes. Maybe wrong file format?
        if (pdbEntry.nodes.size == 0) {
            throw Exception("No nodes were read from PDB file. Exiting.")
        }
    }

    /**
     * Process a line of the given PDB file and persist the contents in the pdbEntry. This assumes a certain order of lines.
     * Especially atoms of residues must be according to PDB guidelines in consecutive lines in the file.
     *
     * @param line       Line of a PDB file to be processed.
     * @param pdbEntry   The pdb model to be updated with the data from the file.
     * @param atoms      List of atoms, where results will be saved into for post processing.
     * @param helices    List of secondary structure helices, used for post processing. Should be empty from the get-go.
     * @param betaSheets List of secondary structure betasheets, used for post processing. Should be empty at startup.
     * @return A status which cna be used to update messages to the user. And [Status] term, when the outer
     * program should end parsind, since EOF or end of model is reached.
     */
    private fun processLine(
        line: String, pdbEntry: PDBEntry,
        atoms: ArrayList<Atom>,
        helices: ArrayList<Pair<String, String>>,
        betaSheets: ArrayList<Pair<String, String>>
    ): Status {
        when {
            line.startsWith("HEADER") -> {
                // Read the protein description and the four letter PDB ID and save it in the model for later
                // reference and presentation
                pdbEntry.titleProperty.value = line.substring(10, 50).trim { it <= ' ' }
                pdbEntry.pdbCodeProperty.value = line.substring(62, 66).trim { it <= ' ' }
                return Status.header
            }
            line.startsWith("HELIX") -> {
                // Read alpha helix secondary structures.
                val startResSeqNum = line.substring(21, 26).trim { it <= ' ' }
                val endResSeqNum = line.substring(33, 38).trim { it <= ' ' }
                helices.add(Pair(startResSeqNum, endResSeqNum))
                return Status.helix
            }
            line.startsWith("SHEET") -> {
                // Read beta sheet secondary structures.
                val startResSeqNum = line.substring(22, 27).trim { it <= ' ' }
                val endResSeqNum = line.substring(33, 38).trim { it <= ' ' }
                betaSheets.add(Pair(startResSeqNum, endResSeqNum))
                return Status.betasheet
            }
            line.startsWith("ATOM") -> {
                // Read atom instances, used to determine each atoms place in 3d space. and to determine the protein's
                // residue sequence.
                val atomName = line.substring(12, 16).trim { it <= ' ' }
                if (atomName == "CA" || atomName == "CB" || atomName == "C" || atomName == "N"
                    || atomName == "O"
                ) {
                    val x =
                        java.lang.Double.parseDouble(line.substring(30, 38).trim { it <= ' ' }) * ATOM_DISTANCE_FACTOR
                    val y =
                        java.lang.Double.parseDouble(line.substring(38, 46).trim { it <= ' ' }) * ATOM_DISTANCE_FACTOR
                    val z =
                        java.lang.Double.parseDouble(line.substring(46, 54).trim { it <= ' ' }) * ATOM_DISTANCE_FACTOR
                    val residueName = line.substring(17, 20).trim { it <= ' ' }
                    val resSeqNum = line.substring(22, 27).trim { it <= ' ' }

                    atoms.add(Atom(x, y, z, atomName, "$resSeqNum$$residueName"))
                }
                return Status.atom
            }
            else -> return if (line.startsWith("TER"))
            // This terminates the process in outer method, since it is the end of the model.
                Status.term
            else
            // This is output when anything is read which is not parsed by this program, since the information are
            // of no use for its purposes.
                Status.remarks
        }
    }

    /**
     * After having read in all the necessary lines from the PDB file. Use the data structures built up in order
     * to construct a proper model of the information.
     *
     * @param pdbEntry      The containing element holding all information of a pdb file (not fully built yet,
     * may be empty).
     * @param atomArrayList List of all atoms in PDB file.
     * @param helices       List of all helices in PDB file as [Pair] of Strings with starting and ending
     * sequence residue number as key and value.
     * @param betaSheets    List of all beta sheets in PDB file as [Pair] of Strings with starting and
     * ending sequence residue number as key and value.
     * @return List of residues still to be added to the model, after the coordinated have been normalized.
     */
    private fun postProcess(
        pdbEntry: PDBEntry,
        atomArrayList: ArrayList<Atom>,
        helices: ArrayList<Pair<String, String>>,
        betaSheets: ArrayList<Pair<String, String>>
    ): ArrayList<Residue> {
        val residues = ArrayList<Residue>()
        var currentResidue: Residue? = null
        for (a in atomArrayList) {
            val residueSeqNum = a.textProperty.value.split("\$")[0]
            val residueName = a.textProperty.value.split("\$")[1]
            if (currentResidue == null) {
                currentResidue = Residue(residueSeqNum, residueName)
            } else if (currentResidue.resNum != residueSeqNum) {
                residues.add(currentResidue)
                // If the now completed Residue is Glycine, add an interpolated C beta atom to the residue.
                if (currentResidue.aminoAcid == Residue.AminoAcid.GLY) {
                    handleGlycine(currentResidue)
                }
                addToGraph(pdbEntry, currentResidue)
                currentResidue = Residue(residueSeqNum, residueName)
            }
            when (a.chemicalElementProperty.value.toString()) {
                "CA" -> currentResidue.cAlphaAtom = a
                "CB" -> currentResidue.cBetaAtom = a
                "C" -> currentResidue.cAtom = a
                "N" -> currentResidue.nAtom = a
                "O" -> currentResidue.oAtom = a
            }
            a.residueProperty.value = currentResidue
        }
        if (currentResidue != null) {
            residues.add(currentResidue)
            // If last amino acid is glycine, add an interpolated C beta atom to the residue
            if (currentResidue.aminoAcid == Residue.AminoAcid.GLY) {
                handleGlycine(currentResidue)
            }
            addToGraph(pdbEntry, currentResidue)
        }

        for (struc in helices) {
            handleSecondaryStructures(pdbEntry, residues, struc, SecondaryStructure.StructureType.alphahelix)
        }

        for (struc in betaSheets) {
            handleSecondaryStructures(pdbEntry, residues, struc, SecondaryStructure.StructureType.betasheet)
        }

        return residues
    }

    /**
     * Add the nodes of a residue to the nodes list in the graph of the pdbentry.
     *
     * @param pdbEntry       Where the nodes will be added.
     * @param currentResidue The residue to be added to the graph model.
     */
    private fun addToGraph(pdbEntry: PDBEntry, currentResidue: Residue) {
        pdbEntry.addNode(currentResidue.cAtom!!)
        pdbEntry.addNode(currentResidue.cBetaAtom!!)
        pdbEntry.addNode(currentResidue.cAlphaAtom!!)
        pdbEntry.addNode(currentResidue.nAtom!!)
        pdbEntry.addNode(currentResidue.oAtom!!)
    }

    /**
     * Compute an interpolated position for C beta of Glycine, which does not have a C beta atom.
     *
     * @param residue The glycine residue to be handled.
     */
    private fun handleGlycine(residue: Residue) {
        val ca = residue.cAlphaAtom
        val c = residue.cAtom
        val n = residue.nAtom

        // Point of C alpha in 3D space
        val caPoint = Point3D(
            ca!!.xCoordinateProperty.get(), ca.yCoordinateProperty.get(),
            ca.zCoordinateProperty.get()
        )
        // Point of C in 3D space
        val cPoint = Point3D(
            c!!.xCoordinateProperty.get(), c.yCoordinateProperty.get(),
            c.zCoordinateProperty.get()
        )
        // Point of N in 3D space
        val nPoint = Point3D(
            n!!.xCoordinateProperty.get(), n.yCoordinateProperty.get(),
            n.zCoordinateProperty.get()
        )

        // In the following we subtract C alpha in order to have it as our origin (0,0,0)
        // Find the middle between N and C
        val midNC = cPoint.midpoint(nPoint).subtract(caPoint) // subtract ca in order to get the direction vector
        // Find the rotation axis which will rotate this point by 1/3 (120°) keeping the same distance to C and N around C alpha
        // For that we need the normal vector of C alpha -> N and C alpha -> C. With that vector we compute the perpendicular
        // vector of it and the C alpha -> midpoint (of N and C). That is our rotation axis.
        val nCaCPerpendicular =
            cPoint.subtract(caPoint).crossProduct(nPoint.subtract(caPoint)) // Perpendiculat on plane C -> C alpha -> N
        val rotationAxis = nCaCPerpendicular.crossProduct(midNC)

        // Set the correct C - C bond length for the resulting vector since Calpha and Cbeta will have similar distance
        var resultingPoint = midNC.normalize().multiply(cPoint.subtract(caPoint).magnitude())

        // The rotation is approx. 120°. At -120° we expect the H-Atom of C alpha to be. So they will have equal distance.
        val rotate = Rotate(120.0, rotationAxis)
        resultingPoint = rotate.transform(resultingPoint)

        // Set C-alpha as origin point (the point was moved to (0,0,0) as origin for computation)
        resultingPoint = resultingPoint.add(caPoint)

        // Set the results
        residue.cBetaAtom = Atom(resultingPoint.x, resultingPoint.y, resultingPoint.z, "CB", "")
        residue.cBetaAtom!!.residueProperty.setValue(residue)

    }

    /**
     * Handle the read secondary structures and add them to the [PDBEntry] model.
     *
     * @param pdbEntry The model to be manipulated.
     * @param residues List of residues for which the secondary structures should be added.
     * @param struc    The structure read from PDB file. Pair of Strings key being begin residue Number and value being end residue Number.
     * @param type     The type of the [SecondaryStructure]. Can either be alphahelix or betasheet.
     */
    private fun handleSecondaryStructures(
        pdbEntry: PDBEntry, residues: ArrayList<Residue>,
        struc: Pair<String, String>, type: SecondaryStructure.StructureType
    ) {
        var current: SecondaryStructure? = null
        for (res in residues) {
            if (current == null && res.resNum == struc.key) {
                // at the begin of a secondary structure
                current = SecondaryStructure(type)
                res.secondaryStructure = current // Set this secondary structure for the residue
                current.addResidue(res)
            } else if (current != null && res.resNum != struc.value) {
                // in the 'middle' of a secondary structure
                res.secondaryStructure = current // Set this secondary structure for the residue
                current.addResidue(res)
            } else if (current != null && res.resNum == struc.value) {
                // at the end of a secondary structure
                res.secondaryStructure = current // Set this secondary structure for the residue
                current.addResidue(res)
                break
            }
        }
        if (current != null) {
            pdbEntry.secondaryStructuresProperty().add(current)
        }
    }

    /**
     * Normalize the coordinated given by PDB aound the (0,0,0) point in the 3d model, in order to have it
     * centered at all times.
     */
    private fun normalizeCoordinates(residues: ArrayList<Residue>) {
        var x = 0.0
        var y = 0.0
        var z = 0.0

        var atoms = 0
        for (res in residues) {
            for (a in res.atoms) {
                x += a.xCoordinateProperty.value
                y += a.yCoordinateProperty.value
                z += a.zCoordinateProperty.value
                atoms++
            }
        }
        x /= atoms
        y /= atoms
        z /= atoms

        for (res in residues) {
            for (a in res.atoms) {
                a.xCoordinateProperty.value = a.xCoordinateProperty.value - x
                a.yCoordinateProperty.value = a.yCoordinateProperty.value - y
                a.zCoordinateProperty.value = a.zCoordinateProperty.value - z
                a.textProperty.value =
                    "Residue: ${a.residueProperty.value.resNum}, amino acid: ${a.residueProperty.value.name}"
            }
        }
    }

    /**
     * Set up bonds, using the given residues in the model's (pdbEntry) nodes list.
     *
     * @param pdbEntry The model instance for which bons should be built up.
     */
    private fun setUpBonds(pdbEntry: PDBEntry) {
        for (i in 0 until pdbEntry.residues.size) {
            val res = pdbEntry.residues[i]
            try {
                if (i != 0) {
                    // not N terminal, N terminus does not need to be connected to anything (to the 'left')
                    // Connect C of last ('left') amino acid with current amino acid's N
                    pdbEntry.connectNodes(pdbEntry.residues[i - 1].cAtom!!, res.nAtom!!)
                }
                // internal amino acid or c terminus and n terminus need this
                // Connect N - Calpha
                pdbEntry.connectNodes(res.nAtom!!, res.cAlphaAtom!!)
                // Connect Calpha - Cbeta
                pdbEntry.connectNodes(res.cAlphaAtom!!, res.cBetaAtom!!)
                // Connect Calpha - C
                pdbEntry.connectNodes(res.cAlphaAtom!!, res.cAtom!!)
                // Connect C - O
                pdbEntry.connectNodes(res.cAtom!!, res.oAtom!!)

            } catch (e: GraphException) {
                System.err.println(e.message)
            }

        }
    }
}
