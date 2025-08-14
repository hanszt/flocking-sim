@file:Suppress("unused")

package hzt.controller.scenes

enum class SceneType(val fxmlFileName: String, val englishDescription: String) {
    MAIN_SCENE("mainScene.fxml", "Main Scene"),
    ABOUT_SCENE("aboutScene.fxml", "About Scene");
}
