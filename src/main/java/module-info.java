module xyz.yuelai.timer {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires xyz.yuelai.ikonli.win11;

    opens xyz.yuelai.timer to javafx.fxml;
    exports xyz.yuelai.timer;
}