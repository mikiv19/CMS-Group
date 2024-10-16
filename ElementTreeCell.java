package com.main.ecommerceprototype.CMS;

import javafx.scene.control.TreeCell;
import org.w3c.dom.Element;

public class ElementTreeCell extends TreeCell<Element> {
    public ElementTreeCell() {
        super();
    }
    @Override
    protected void updateItem(Element element, boolean empty) {
        super.updateItem(element, empty);

        if (empty || element == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(element.getTagName());
        }
    }
}
