package itasserui.app.views.renderer.data.atom

import tornadofx.ItemViewModel

class AtomViewModel(item: AtomController) : ItemViewModel<AtomController>(item) {
    val outEdges = bind(AtomController::outEdges)
    val inEdges = bind(AtomController::inEdges)
    val atom = bind(AtomController::atomProperty)
    val text = bind(AtomController::textProperty)
    val xCoordinate = bind(AtomController::xCoordinateProperty)
    val yCoordinate = bind(AtomController::yCoordinateProperty)
    val zCoordinate = bind(AtomController::zCoordinateProperty)
    val color = bind(AtomController::colorProperty)
    val radius = bind(AtomController::radiusProperty)
}