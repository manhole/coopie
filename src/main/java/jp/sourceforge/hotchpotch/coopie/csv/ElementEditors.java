package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Text;

public class ElementEditors {

    /**
     * 何もしません。
     */
    public static final ElementEditor NO_EDIT = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return element;
        }
    };

    /**
     * {@link String#trim()}します。
     */
    public static final ElementEditor TRIM = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return element.trim();
        }
    };

    /**
     * {@link Character#isWhitespace(char)}である文字をtrimします。
     */
    public static final ElementEditor TRIM_WHITESPACE = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return Text.trimWhitespace(element);
        }
    };

}
