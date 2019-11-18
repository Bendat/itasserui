package itasser.app.mytasser.app.process.newDialog

import tornadofx.cssclass


object NewProteinDialogCss {
    @JvmStatic
    val userField by cssclass("sequence-user-field")
    @JvmStatic
    val name by cssclass("sequence-details-name")
    @JvmStatic
    val description by cssclass("sequence-details-description")
    @JvmStatic
    val sequenceName by cssclass("sequence-name")
    @JvmStatic
    val dataDir by cssclass("sequence-datadir")
    @JvmStatic
    val outDir by cssclass("sequence-outdir")
    @JvmStatic
    val idCut by cssclass("sequence-idut")
    @JvmStatic
    val nTemp by cssclass("sequence-ntemp")
    @JvmStatic
    val nModel by cssclass("sequence-nmodel")
    @JvmStatic
    val ec by cssclass("sequence-ec")
    @JvmStatic
    val lbs by cssclass("sequence-lbs")
    @JvmStatic
    val go by cssclass("sequence-go")
    @JvmStatic
    val light by cssclass("sequence-light")
    @JvmStatic
    val traj by cssclass("sequence-traj")
    @JvmStatic
    val homoflag by cssclass("sequence-homoflag")
    @JvmStatic
    val tempexcl by cssclass("sequence-tempexcl")
    @JvmStatic
    val restraint1 by cssclass("sequence-restraint1")
    @JvmStatic
    val restraint2 by cssclass("sequence-restraint2")
    @JvmStatic
    val restraint3 by cssclass("sequence-restraint3")
    @JvmStatic
    val restraint4 by cssclass("sequence-restraint4")
    @JvmStatic
    val hours by cssclass("sequence-hours")
    @JvmStatic
    val cancelButton by cssclass("sequence-cancel-button")
    @JvmStatic
    val createButton by cssclass("sequence-create-button")
    @JvmStatic
    val errorLabel by cssclass("new-process-invalid-text")
    @JvmStatic
    val proteinDetailsFieldSet by cssclass("protein-details-fieldset")
    @JvmStatic
    val fastaLocation by cssclass("new-protein-seq-file")
    @JvmStatic
    val requiredFieldSet by cssclass("protein-details-required-parameters")
    @JvmStatic
    val optionalParams by cssclass("protein-details-optional-parameters")
    @JvmStatic
    val defaultParams by cssclass("protein-details-default-parameters")

}