package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Text;

public interface ElementEditor {

    /**
     * {@link ReadEditor#readRecord(ElementReader)}で読んだ各要素を編集できます。
     */
    String edit(String element);

    ElementEditor NO_EDIT = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return element;
        }
    };

    ElementEditor TRIM = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return element.trim();
        }
    };

    ElementEditor TRIM_WHITESPACE = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return Text.trimWhitespace(element);
        }
    };

}
